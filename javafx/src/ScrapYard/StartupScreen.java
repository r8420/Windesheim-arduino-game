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

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;


public class StartupScreen extends Application {

    private static final double WIDTH = 600;
    private static final double HEIGHT = 700;
    private final static int STARTHOOGTE = 30;
    private static final Rectangle BAK = new Rectangle(WIDTH - 110, HEIGHT - 110, 110, 110);
    private Text newgame = new Text("Spelen");
    private Text leaveGame = new Text("Leave game");
    private Text title = new Text("ScrapYard");
    private static MediaPlayer mediaPlayer;
    private Boolean doosRaaktMagneet = false;
    private Boolean loslaten = false;
    private Boolean Begin = true;
    public static SerialPort sp;
    private static boolean arduinoConnected;
    public static Stage stage;
    private Boolean enableInput = true;

    private Main main;


    // magneet en doos
    Magneet magneet;
    PhysicsObject doos = new PhysicsObject(50, HEIGHT - 30, 80, 30);


    @Override
    public void start(Stage stage) {
        this.stage = stage;

        arduinoConnected = arduinoStart();
        // achtergrond muziekje

        try {
            String musicFile = "src/Music/backgroundMusic.mp3";
            Media sound = new Media(new File(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setAutoPlay(true);
        } catch (Exception mediafout) {
            System.out.println("Achtergrond muziek kan niet afgespeeld worden");
        }

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        magneet = new Magneet(WIDTH / 2, STARTHOOGTE);

        // texten aanpassen
        title.setFont(new Font("Arial", 60));

        // Creating a Group object
        Group root = new Group();
        root.getChildren().add(canvas);
        root.getChildren().add(newgame);
        root.getChildren().add(leaveGame);
        root.getChildren().add(title);


        // positie texten
        title.setX(160);
        title.setY(200);
        newgame.setX(260);
        newgame.setY(350);
        leaveGame.setX(260);
        leaveGame.setY(370);


        // event handler knoppen
        EventHandler<MouseEvent> eventHandler = mouseEvent -> {
            if(main == null){
                main = new Main();
                main.start(new Stage());
            } else {
                main.showMainScherm();
            }
            // stop intro muziek
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                System.out.println("Geen muziek");
            }
            // start game
            try {
                main.start(new Stage());
                stage.hide();
                arduinoConnected = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        EventHandler<MouseEvent> eventHandler2 = mouseEvent -> System.exit(0);
        newgame.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        leaveGame.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler2);

        //Creating a scene object
        Scene scene = new Scene(root, WIDTH, HEIGHT);


        //Setting title to the Stage
        stage.setTitle("Start-up");

        //title bar icon
        File file = new File("images/magneet_uit.png");
        Image image = new Image(file.toURI().toString());
        stage.getIcons().add(image);

        //Adding scene to the stage
        stage.setScene(scene);
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            if (arduinoConnected) arduinoSensor();
            draw(gc);
        }));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
        draw(gc);

        //Displaying the contents of the stage
        stage.show();
    }

    private void draw(GraphicsContext gc) {

        // achtergrond
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        //fase 1 van animatie
        if (Begin) {
            if (magneet.getX() > 40) {
                magneet.setX(magneet.getX() - 1);
            } else if (magneet.getY() < HEIGHT - 125) {
                magneet.setY(magneet.getY() + 1);
            } else {
                magneet.setAan(true);
                doosRaaktMagneet = true;
                Begin = false;
            }

            // fase 2 van animatie
        } else if (doosRaaktMagneet) {
            if (magneet.getY() < HEIGHT - 120 && magneet.getY() > 100) {
                magneet.setY(magneet.getY() - 1);
            } else if (magneet.getX() < 500) {
                magneet.setX(magneet.getX() + 1);
            } else {
                doosRaaktMagneet = false;
                loslaten = true;
            }
            doos.setX(magneet.getX() + 10);
            doos.setY(magneet.getY() + 95);

            // fase 3 van animatie
        } else if (loslaten) {
            magneet.setAan(false);
            doos.setY(doos.getY() + 1);
        }

        if (doos.getX() > 500 && doos.getY() > 600) {
            loslaten = false;
            Begin = true;
            resetLevel();
        }

        // doos
        doos.draw(gc);

        // ketting
        gc.setFill(Color.GRAY);
        gc.fillRect(magneet.getX() + magneet.getWidth() / 2 - 5, 0, 10, magneet.getY());

        // magneet
        magneet.draw(gc);

        // eind bak
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(BAK.getX(), BAK.getY(), BAK.getWidth(), BAK.getHeight());


    }

    // de magneet en doos weer op begin plek zetten

    private void resetLevel() {
        doos.setX(40);
        doos.setY(HEIGHT - 30);
        doos.setYMotion(0);
        doos.setXMotion(0);
        magneet.setX(WIDTH / 2 - magneet.getWidth() / 2);
        magneet.setY(STARTHOOGTE);
        magneet.setYMotion(0);
        magneet.setXMotion(0);
        magneet.setAan(false);
    }

    public static boolean arduinoStart() {
        sp = SerialPort.getCommPort("COM3");
        sp.setComPortParameters(9600, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

        if (sp.openPort()) {
            System.out.println("Successfully connected to Arduino");
            return true;

        } else {
            System.out.println("Couldn't connect to Arduino");
            return false;
        }
    }

    public void arduinoSensor() {
        // Tegen spam wanneer je gewonnen hebt (game over bent) zonder arduino
        if (!arduinoConnected) return;
        try {
            System.out.println("Arduino");
            while (sp.getInputStream().available() > 0) {
                byte[] bytes = sp.getInputStream().readNBytes(1);
                char lezing = (char) bytes[0];
                if (lezing == 'A') {
                    try {
                        mediaPlayer.stop();
                    } catch (Exception e) {
                        System.out.println("Geen muziek");
                    }
                    try {
//                        sp.closePort();

                        if(main == null){
                            main = new Main();
                            main.start(new Stage());
                        } else {
                            main.showMainScherm();
                        }




                        stage.hide();
                        Main.setArduinoConnected(true);
                        arduinoConnected = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (lezing == 'B') {
                    System.exit(0);
                }
            }
        } catch (NullPointerException | IOException ignored) {


        } catch (NumberFormatException NFE) {
            System.out.println("gemiste getal");
        }
    }

    public static void showStartupScherm(){
        stage.show();
        Main.setArduinoConnected(true);
        mediaPlayer.play();
    }

    public static void setArduinoConnected(Boolean arduinoConnected) {
        StartupScreen.arduinoConnected = arduinoConnected;

    }

    public static void main(String[] args) {
        launch(args);
    }
}

