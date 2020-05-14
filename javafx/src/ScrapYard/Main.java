package ScrapYard;

import com.fazecast.jSerialComm.SerialPort;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {

    private static final double WIDTH = 600;
    private static final double HEIGHT = 700;

    private static final double MAX_X_SPEED = 2;
    private static final double Y_UP_SPEED = 5;
    private static final double Y_DOWN_SPEED = 2;
    private static final double START_HOOGTE = 30;
    private static final Rectangle BAK = new Rectangle(WIDTH - 110, HEIGHT - 110, 110, 110);

    private Integer timer;


    private boolean magneetMagVeranderen = true;
    private boolean magneetBinnenHalen;

    private boolean victory;
    private boolean gameover;

    // controls
    private boolean knop_B;
    private boolean knop_A;
    private boolean links;
    private boolean rechts;

    Magneet magneet;
    PhysicsObject opgepakteDoos;
    ArrayList<PhysicsObject> dozen;

    private static SerialPort sp;

    private Text newgameText;
    private Text startschermText;


    private boolean arduinoConnected;
    private Stage primaryStage;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setScene(new Scene(root, 300, 275));

        arduinoConnected = arduinoStart();
        primaryStage.setTitle("ScrapYard");
        primaryStage.setResizable(false);
        File file = new File("images/magneet_uit.png");
        Image image = new Image(file.toURI().toString());
        primaryStage.getIcons().add(image);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        Group rootPane = new Group(canvas);
        Scene scene1 = new Scene(rootPane);

        newgameText = new Text();
        newgameText.setX(WIDTH / 2 - 60);
        newgameText.setY(350);
        startschermText = new Text();
        startschermText.setY(375);
        startschermText.setX(WIDTH / 2 - 60);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        rootPane.getChildren().addAll(newgameText, startschermText);
        EventHandler<MouseEvent> eventHandler = mouseEvent -> {
            resetLevel();
            victory = false;
            gameover = false;
            newgameText.setText("");
            startschermText.setText("");
        };

        EventHandler<MouseEvent> eventHandler2 = mouseEvent -> {
            StartupScreen startupScreen = new StartupScreen();
            startupScreen.start(new Stage());
            primaryStage.close();
        };
        newgameText.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        startschermText.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler2);


        // startinstellingen voor scherminhoud
        dozen = new ArrayList<>();
        magneet = new Magneet(WIDTH / 2, START_HOOGTE);


        resetLevel();

        // start de timeline
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            gamelogic();
            if (arduinoConnected) arduinoSensor();
            draw(gc);
        }));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();

        // toetsen registreren
        scene1.setOnKeyPressed(e -> drukToetsIn(e.getCode().getCode()));
        scene1.setOnKeyReleased(e -> laatToetsLos(e.getCode().getCode()));

        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    private void resetLevel() {
        opgepakteDoos = null;
        dozen.clear();
        dozen.add(new PhysicsObject(randomWaarde(0, WIDTH - 200), HEIGHT / 2, randomWaarde(25, 100), randomWaarde(25, 100)));
//        dozen.add(new Doos(randomWaarde(0,WIDTH-200), HEIGHT/2, randomWaarde(25,100), randomWaarde(25,100)));
//        dozen.add(new Doos(randomWaarde(0,WIDTH-200), HEIGHT/2, randomWaarde(25,100), randomWaarde(25,100)));

        magneet.setX(WIDTH / 2 - magneet.getWidth() / 2);
        magneet.setY(START_HOOGTE);
        magneet.setYMotion(0);
        magneet.setXMotion(0);
        magneet.setAan(false);
        timer = 3000;
    }

    private void gamelogic() {

        if (dozen.size() == 0 && opgepakteDoos == null) victory = true;  // winconditie

        if (victory || gameover) {
            if (arduinoSensor() == 'A') {
                resetLevel();
                victory = false;
                gameover = false;
                newgameText.setText("");
                startschermText.setText("");
            }
            if (arduinoSensor() == 'B') {
                sp.closePort();
                StartupScreen startupScreen = new StartupScreen();
                startupScreen.start(new Stage());
                primaryStage.close();
            }

            return;
        }

        if (timer <= 0) {  // timer updaten
            gameover = true;
        } else {
            timer--;
        }

        magneet.updatePos();

        if (magneet.getX() + magneet.getWidth() > WIDTH) {  // collision rechterrand
            magneet.setX(WIDTH - magneet.getWidth());
            magneet.setXMotion(0);

        } else if (magneet.getX() < 0) {  // collision linkerrand
            magneet.setX(0);
            magneet.setXMotion(0);
        }

        // magneet omhoog / omlaag
        if (magneet.getY() < START_HOOGTE) {  // collision bovenrand
            magneet.setYMotion(0);
            magneet.setY(START_HOOGTE);
            magneetBinnenHalen = false;

        } else if (opgepakteDoos == null) {

            if (magneet.getY() > HEIGHT - magneet.getHeight()) {  // collision onderrand

                magneet.setY(HEIGHT - magneet.getHeight());
                magneetBinnenHalen();
            }
        } else {

            if (magneet.getY() > HEIGHT - magneet.getHeight() - opgepakteDoos.getHeight()) {  // collision onderrand

                magneet.setY(HEIGHT - magneet.getHeight() - opgepakteDoos.getHeight());
                magneetBinnenHalen();
            }
        }

        int i = 0;
        int pakDezeDoos = -1;
        int doosInBak = -1;
        for (PhysicsObject d : dozen) {
            d.updatePos(WIDTH, HEIGHT);


            if (d.getX() > BAK.getX() && d.getY() > BAK.getY()) {
                doosInBak = i;
            }

            if (magneet.isAan() && opgepakteDoos == null && d.intersects(magneet)) {
                pakDezeDoos = i;

            }

            // moet onderaan blijven staan
            i++;
        }

        if (doosInBak != -1) dozen.remove(doosInBak);

        if (pakDezeDoos != -1) {
            magneetBinnenHalen = true;
            opgepakteDoos = dozen.get(pakDezeDoos);
            opgepakteDoos.setXMotion(0);
            opgepakteDoos.setYMotion(0);
            dozen.remove(pakDezeDoos);
        }

        if (opgepakteDoos != null) {
            opgepakteDoos.setX(magneet.getX() + magneet.getWidth() / 2 - opgepakteDoos.getWidth() / 2);
            opgepakteDoos.setY(magneet.getY() + magneet.getHeight() * 0.9);
        }


        // acties per toets
        if (links) {
            magneet.setXMotion(Math.max(magneet.getXMotion() - 0.05, -MAX_X_SPEED));
        }
        if (rechts) {
            magneet.setXMotion(Math.min(magneet.getXMotion() + 0.05, MAX_X_SPEED));
        }
        if (knop_B && magneetMagVeranderen) {
            magneetMagVeranderen = false;

            if (magneet.isAan() && opgepakteDoos != null) {  // wanneer je een object laat vallen
                opgepakteDoos.setXMotion(magneet.getXMotion());
                opgepakteDoos.setYMotion(magneet.getYMotion());
                dozen.add(opgepakteDoos);
                opgepakteDoos = null;
            }

            magneet.setAan(!magneet.isAan()); // switch magneet status;
        }
        if (knop_A && !magneetBinnenHalen) { // omlaag zolang A is ingedrukt
            magneet.setYMotion(Y_DOWN_SPEED);
        }

    }

    private void draw(GraphicsContext gc) {


        // achtergrond
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        if (opgepakteDoos != null) opgepakteDoos.draw(gc);

        for (PhysicsObject d : dozen) {
            d.draw(gc);
        }

        // ketting
        gc.setFill(Color.GRAY);
        gc.fillRect(magneet.getX() + magneet.getWidth() / 2 - 5, 0, 10, magneet.getY());
        magneet.draw(gc);

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(BAK.getX(), BAK.getY(), BAK.getWidth(), BAK.getHeight());

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 20));
        int seconds = timer / 100;
        int centiseconds = timer % 100;
        gc.fillText(seconds + "." + (centiseconds < 10 ? "0" : "") + centiseconds, 0, 20);


        if (victory) {
            gc.setFill(new Color(1, 1, 1, 0.5));
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            newgameText.setFill(Color.GREEN);
            newgameText.setFont(new Font("Arial", 20));
            newgameText.setText("(A) New game");
            startschermText.setFill(Color.GREEN);
            startschermText.setFont(new Font("Arial", 20));
            startschermText.setText("(B) Terug naar start");
            gc.setFill(Color.GREEN);
            gc.setFont(new Font("Arial", 50));
            gc.fillText("Victory", WIDTH / 2 - 75, 300);

        }


        if (gameover) {
            gc.setFill(new Color(1, 1, 1, 0.5));
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            newgameText.setFill(Color.RED);
            newgameText.setFont(new Font("Arial", 20));
            newgameText.setText("(A) New game");
            startschermText.setFill(Color.RED);
            startschermText.setFont(new Font("Arial", 20));
            startschermText.setText("(B) Terug naar start");
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 50));
            gc.fillText("Game Over", WIDTH / 2 - 125, 300);
        }
    }

    private void drukToetsIn(int toets) {
//        System.out.println(toets);
        switch (toets) {
            case 65: // a
                links = true;
                break;
            case 68: // d
                rechts = true;
                break;
            case 32: // spatie
                knop_B = true;
                break;
            case 83: // s
                knop_A = true;
                break;
        }
    }

    private void laatToetsLos(int toets) {
        switch (toets) {
            case 65: // a
                links = false;
                break;
            case 68: // d
                rechts = false;
                break;
            case 32: // spatie
                knop_B = false;
                magneetMagVeranderen = true;
                break;
            case 83: // s
                knop_A = false;
                magneetBinnenHalen();
                break;
        }
    }

    private void magneetBinnenHalen() {
        magneetBinnenHalen = true;
        magneet.setYMotion(-Y_UP_SPEED);
    }


    private double randomWaarde(double min, double max) {
        return (Math.random() * ((max - min) + 1)) + min;
    }

    public boolean arduinoStart() {
        sp = SerialPort.getCommPort("COM3");
        sp.setComPortParameters(9600, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

        if (sp.openPort()) {
            System.out.println("Succesfully connected to Arduino");
            return true;

        } else {
            System.out.println("Couldn't connect to Arduino");
            return false;
        }
    }

    public char arduinoSensor() {
        // Tegen spam wanneer je gewonnen hebt (game over bent) zonder arduino
        if (!arduinoConnected) {
            return 0;
        }
        try {
            while (sp.getInputStream().available() > 0) {
                byte[] bytes = sp.getInputStream().readNBytes(1);
                char lezing = (char) bytes[0];
                if (lezing == 'B') {
                    knop_B = true;
                    return lezing;
                } else if (lezing == 'b') {
                    knop_B = false;
                    magneetMagVeranderen = true;
                    return lezing;
                } else if (lezing == 'A') {
                    knop_A = true;
                    return lezing;
                } else if (lezing == 'a') {
                    knop_A = false;
                    magneetBinnenHalen();
                    return lezing;
                } else {
                    int getal = lezing - 48;
                    magneet.setXMotion((getal - 4.5) / 4.5 * 2);
                    return lezing;
                }
            }
        } catch (NullPointerException | IOException ignored) {
        } catch (NumberFormatException NFE) {
            System.out.println("gemiste getal");
        }
        return 0;
    }


    public static void main(String[] args) {
        launch(args);

    }
}
