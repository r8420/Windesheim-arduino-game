package MagnetGame;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 700;
    private static final int MAGNEET_WIDTH = 100;
    private static final int MAGNEET_HALF_WIDTH = MAGNEET_WIDTH/2;
    private static final int MAGNEET_HEIGHT = 20;

    private double magneetX = WIDTH/2.0;
    private double magneetY = 0;
    private double magneetXmotion = 0;
    private double magneetYmotion = 0;

    private Scene scene1;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
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

        // toetsen registreren
        scene1.setOnKeyTyped(e -> {
            String toets = e.getCharacter();
            toetsActies(toets);
        });

        primaryStage.setScene(scene1);
        primaryStage.show();
        tl.play();
    }

    private void gamelogic() {
        magneetX += magneetXmotion;
        magneetX = Math.min(magneetX, WIDTH-MAGNEET_HALF_WIDTH);
        magneetX = Math.max(magneetX, MAGNEET_HALF_WIDTH);

        magneetY = Math.max(0, magneetY + magneetYmotion);
        magneetY = Math.min(magneetY, HEIGHT-MAGNEET_HEIGHT);
    }

    private void draw(GraphicsContext gc) {

        // achtergrond
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // ketting
        gc.setFill(Color.GRAY);
        gc.fillRect(magneetX-5, 0, 10, magneetY);

        // magneet
        gc.setFill(Color.RED);
        gc.fillRect(magneetX-MAGNEET_HALF_WIDTH, magneetY, MAGNEET_WIDTH, MAGNEET_HEIGHT);

    }

    private void toetsActies(String toets) {
        switch(toets) {
            case "a": // links
                magneetXmotion -= 0.2;
                magneetXmotion = Math.max(magneetXmotion, -1);
                break;
            case "d": // rechts
                magneetXmotion += 0.2;
                magneetXmotion = Math.min(magneetXmotion, 1);
                break;
            case "w": // omhoog
                magneetYmotion = -1;
                break;
            case "s": // omlaag
                magneetYmotion = 1;
                break;
        }
    }




    public static void main(String[] args) {
        launch(args);
    }
}
