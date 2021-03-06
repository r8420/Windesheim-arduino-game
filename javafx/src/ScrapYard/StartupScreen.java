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
    private static final int STARTHOOGTE = 30;
    private static final Rectangle BAK = new Rectangle(WIDTH - 110, HEIGHT - 110, 110, 110);
    private final Text newgame = new Text("Spelen");
    private final Text leaveGame = new Text("Leave game");
    private final Text title = new Text("ScrapYard");
    private static MediaPlayer mediaPlayer;
    private Boolean autoRaaktMagneet = false;
    private Boolean loslaten = false;
    private Boolean begin = true;

    public static SerialPort sp;
    private static boolean arduinoConnected;

    public static Stage stage;
    private static String comPort = "COM3";

    private Main main;

    private Magneet magneet;
    private final PhysicsObject auto = new PhysicsObject(50, HEIGHT - 30, 80, 30);


    @Override
    public void start(Stage stage) {
        StartupScreen.stage = stage;
        stage.setResizable(false);
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
            // maak voor de eerste keer van het staren van de game het main object aan.
            if (main == null) {
                main = new Main();
                main.start(new Stage());
            }
            // stop intro muziek
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                System.out.println("Geen muziek");
            }
            // start game
            try {
                Main.showMainScherm();
                stage.hide();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        EventHandler<MouseEvent> eventHandler2 = mouseEvent -> {
            sp.closePort();
            System.exit(0);
        };
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
        if (begin) {
            if (magneet.getX() > 40) {
                magneet.setX(magneet.getX() - 1);
            } else if (magneet.getY() < HEIGHT - 125) {
                magneet.setY(magneet.getY() + 1);
            } else {
                magneet.setAan(true);
                autoRaaktMagneet = true;
                begin = false;
            }

            // fase 2 van animatie
        } else if (autoRaaktMagneet) {
            if (magneet.getY() < HEIGHT - 120 && magneet.getY() > 100) {
                magneet.setY(magneet.getY() - 1);
            } else if (magneet.getX() < 500) {
                magneet.setX(magneet.getX() + 1);
            } else {
                autoRaaktMagneet = false;
                loslaten = true;
            }
            auto.setX(magneet.getX() + 10);
            auto.setY(magneet.getY() + 95);

            // fase 3 van animatie
        } else if (loslaten) {
            magneet.setAan(false);
            auto.setY(auto.getY() + 1);
        }

        if (auto.getX() > 500 && auto.getY() > 600) {
            loslaten = false;
            begin = true;
            resetLevel();
        }

        // auto
        auto.draw(gc);

        // ketting
        gc.setFill(Color.GRAY);
        gc.fillRect(magneet.getX() + magneet.getWidth() / 2 - 5, 0, 10, magneet.getY());

        // magneet
        magneet.draw(gc);

        // eind bak
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(BAK.getX(), BAK.getY(), BAK.getWidth(), BAK.getHeight());


    }

    // de magneet en auto weer op begin plek zetten

    private void resetLevel() {
        auto.setX(40);
        auto.setY(HEIGHT - 30);
        auto.setYMotion(0);
        auto.setXMotion(0);
        magneet.setX(WIDTH / 2 - magneet.getWidth() / 2);
        magneet.setY(STARTHOOGTE);
        magneet.setYMotion(0);
        magneet.setXMotion(0);
        magneet.setAan(false);
    }

    public static boolean arduinoStart() {
        for (SerialPort comm : SerialPort.getCommPorts()) {
            if (comm.getDescriptivePortName().contains("Arduino") || comm.getDescriptivePortName().contains("USB")) {
                comPort = comm.getSystemPortName();
            }
        }
        sp = SerialPort.getCommPort(comPort);
        sp.setComPortParameters(9600, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

        if (sp.openPort()) {
            System.out.println("Successfully connected to Arduino on: " + comPort);

            //Flush serial buffer
            serialFlush();
            return true;

        } else {
            System.out.println("Couldn't connect to Arduino");
            return false;
        }
    }

    private static void serialFlush() {
        try {
            while (sp.getInputStream().available() > 0) {
                //noinspection ResultOfMethodCallIgnored
                sp.getInputStream().read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void arduinoSensor() {
        // Tegen spam wanneer je gewonnen hebt (game over bent) zonder arduino
        if (!arduinoConnected) return;
        try {
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
                        // maak voor de eerste keer van het staren van de game het main object aan.
                        if (main == null) {
                            main = new Main();
                            main.start(new Stage());
                        }
                        Main.showMainScherm();

                        stage.hide();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (lezing == 'B' && arduinoConnected) {
                    sp.closePort();
                    System.exit(0);
                }
            }
        } catch (NullPointerException | IOException ignored) {


        } catch (NumberFormatException NFE) {
            System.out.println("gemiste getal");
        }
    }

    static void showStartupScherm() {
        // zet controls uit in game
        Main.setArduinoConnected(false);
        stage.show();
        // zet controls aan op startup screen
        arduinoConnected = true;
        mediaPlayer.play();
    }

    public static void setArduinoConnected(Boolean arduinoConnected) {
        StartupScreen.arduinoConnected = arduinoConnected;

    }

    public static void main(String[] args) {
        launch(args);
    }
}

