package ScrapYard;

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

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;


public class StartupScreen extends Application {

    private static final double WIDTH = 600;
    private static final double HEIGHT = 700;
    private final static int STARTHOOGTE = 30;
    private static final Rectangle BAK = new Rectangle(WIDTH - 110, HEIGHT - 110, 110, 110);
    private Text newgame = new Text("Spelen");
    private Text title = new Text("ScrapYard");
    private Text naamSpeler = new Text("Naam:");
    private javafx.scene.control.TextField textFieldNaamSpeler = new javafx.scene.control.TextField();
    private Text highscore = new Text("Highscore");
    private MediaPlayer mediaPlayer;
    private Boolean doosRaaktMagneet = false;
    private Boolean loslaten = false;
    private Boolean Begin = true;

    // magneet en doos
    Magneet magneet;
    PhysicsObject doos = new PhysicsObject(50, HEIGHT - 30, 80, 30);


    @Override
    public void start(Stage stage) {

        // achtergrond muziekje
        try {
            String musicFile = "src/Music/backgroundMusic.mp3";
            Media sound = new Media(new File(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setAutoPlay(true);
        } catch (Exception mediafout) {
            System.out.println("ging wat fout");
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
        root.getChildren().add(title);
        root.getChildren().add(textFieldNaamSpeler);
        root.getChildren().add(naamSpeler);
        root.getChildren().add(highscore);

        // positie texten
        title.setX(160);
        title.setY(200);
        naamSpeler.setX(200);
        naamSpeler.setY(300);
        textFieldNaamSpeler.setLayoutX(200);
        textFieldNaamSpeler.setLayoutY(310);
        newgame.setX(260);
        newgame.setY(350);
        highscore.setX(200);
        highscore.setY(500);

        // event handler knoppen
        EventHandler<MouseEvent> eventHandler = mouseEvent -> {
            Main main = new Main();
            try {
                mediaPlayer.stop();
                main.start(new Stage());
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        newgame.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);

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

    public static void main(String args[]) {
        launch(args);
    }
}
