package MagnetGame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 700;
    private static final int MAGNEET_WIDTH = 100;
    private static final int MAGNEET_HALF_WIDTH = MAGNEET_WIDTH/2;
    private static final int MAGNEET_HEIGHT = 20;

    private static final double MAX_X_SPEED = 2;
    private static final double Y_UP_SPEED = 5;
    private static final double Y_DOWN_SPEED = 2;
    private static final double START_HOOGTE = 30;
    private double magneetX = WIDTH/2.0;
    private double magneetY = START_HOOGTE;
    private double magneetXmotion = 0;
    private double magneetYmotion = 0;
    private boolean magneetAan;
    private boolean magneetMagVeranderen = true;
    private boolean magneet_ophalen;

    // controls
    private boolean knop_B;
    private boolean knop_A;
    private boolean links;
    private boolean rechts;

    private Scene scene1;

    @Override
    public void start(Stage primaryStage) throws Exception{

//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setScene(new Scene(root, 300, 275));

        primaryStage.setTitle("MagnetGame");
        primaryStage.setResizable(false);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        scene1 = new Scene(new StackPane(canvas));
        GraphicsContext gc = canvas.getGraphicsContext2D();


        // start de timeline
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            gamelogic();
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

    private void gamelogic() {
        // magneet links / rechts
        magneetX += magneetXmotion;
        if (magneetX > WIDTH-MAGNEET_HALF_WIDTH) { // collision rechterrand
            magneetX = WIDTH-MAGNEET_HALF_WIDTH;
            magneetXmotion = 0;

        } else if (magneetX < MAGNEET_HALF_WIDTH) { // collision linkerrand
            magneetX = MAGNEET_HALF_WIDTH;
            magneetXmotion = 0;
        }

        // magneet omhoog / omlaag
        magneetY += magneetYmotion;
        if (magneetY < START_HOOGTE) { // collision bovenrand
            magneetYmotion = 0;
            magneetY = START_HOOGTE;
            magneet_ophalen = false;

        } else if (magneetY > HEIGHT-MAGNEET_HEIGHT) { // collision onderrand
            magneetY = HEIGHT-MAGNEET_HEIGHT;
            magneetYmotion = -Y_UP_SPEED;
            magneet_ophalen = true;
        }

        // acties per toets
        if (links) {
            magneetXmotion = Math.max(magneetXmotion - 0.05, -MAX_X_SPEED);
        }
        if (rechts) {
            magneetXmotion = Math.min(magneetXmotion + 0.05, MAX_X_SPEED);
        }
        if (knop_B && magneetMagVeranderen) {
            magneetAan = !magneetAan; // switch magneet status;
            magneetMagVeranderen = false;
        }
        if (knop_A && !magneet_ophalen) { // omlaag zolang A is ingedrukt
            magneetYmotion = Y_DOWN_SPEED;
        }

    }

    private void draw(GraphicsContext gc) {

        // achtergrond
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // ketting
        gc.setFill(Color.GRAY);
        gc.fillRect(magneetX-5, 0, 10, magneetY);

        // magneet
        if (magneetAan) {
            gc.setFill(Color.RED);
            gc.fillRect(magneetX-MAGNEET_HALF_WIDTH, magneetY+3, MAGNEET_WIDTH, MAGNEET_HEIGHT);
        }
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(magneetX-MAGNEET_HALF_WIDTH, magneetY, MAGNEET_WIDTH, MAGNEET_HEIGHT);

    }

    private void drukToetsIn(int toets) {
//        System.out.println(toets);
        switch(toets) {
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
        switch(toets) {
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
                magneet_ophalen = true;
                magneetYmotion = -Y_UP_SPEED;
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
