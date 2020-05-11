package ScrapYard;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Main extends Application {

    private static final double WIDTH = 600;
    private static final double HEIGHT = 700;

    private static final double MAX_X_SPEED = 2;
    private static final double Y_UP_SPEED = 5;
    private static final double Y_DOWN_SPEED = 2;
    private static final double START_HOOGTE = 30;

    private boolean magneetMagVeranderen = true;
    private boolean magneetBinnenHalen;
    private boolean victory = false;

    // controls
    private boolean knop_B;
    private boolean knop_A;
    private boolean links;
    private boolean rechts;

    Magneet magneet;
    ArrayList<Doos> dozen;

    private Scene scene1;
    private StackPane pane;

    private Text text = new Text();


    @Override
    public void start(Stage primaryStage) throws Exception{

//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setScene(new Scene(root, 300, 275));

        primaryStage.setTitle("ScrapYard");
        primaryStage.setResizable(false);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        pane = new StackPane(canvas);
        scene1 = new Scene(pane);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        pane.getChildren().add(text);
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                cleanUp();
                victory = false;
                text.setText("");
                System.out.println("clicked");
            }
        };
        text.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);

        // startinstellingen voor scherminhoud
        dozen = new ArrayList<>();
        dozen.add(new Doos(100, -100, 100, 100));

        magneet = new Magneet(WIDTH/2, START_HOOGTE);






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

        magneet.updatePos();

        if (magneet.getX() + magneet.getWidth() > WIDTH) { // collision rechterrand
            magneet.setX(WIDTH - magneet.getWidth());
            magneet.setXMotion(0);

        } else if (magneet.getX() < 0) { // collision linkerrand
            magneet.setX(0);
            magneet.setXMotion(0);
        }

        // magneet omhoog / omlaag
        if (magneet.getY() < START_HOOGTE) { // collision bovenrand
            magneet.setYMotion(0);
            magneet.setY(START_HOOGTE);
            magneetBinnenHalen = false;

        } else if (magneet.getY() > HEIGHT-magneet.getHeight()) { // collision onderrand
            magneet.setY(HEIGHT-magneet.getHeight());
            magneetBinnenHalen();
        }


        for (Doos d : dozen) {
            d.updatePos(WIDTH, HEIGHT);
            if (d.getY()>550 && d.getX() > 420){
                victory = true;

            }
            if (d.intersects(magneet)) {
                System.out.println("doos raakt magneet!");
                if (magneet.getYMotion() > 0) {
                    magneet.setYMotion(0);
                }
                d.setXMotion(magneet.getXMotion());
                d.setYMotion(magneet.getYMotion());
            }
        }


        // acties per toets
        if (links) {
            magneet.setXMotion(Math.max(magneet.getXMotion() - 0.05, -MAX_X_SPEED));
        }
        if (rechts) {
            magneet.setXMotion(Math.min(magneet.getXMotion() + 0.05, MAX_X_SPEED));
        }
        if (knop_B && magneetMagVeranderen) {
            magneet.setAan(!magneet.isAan()); // switch magneet status;
            magneetMagVeranderen = false;
        }
        if (knop_A && !magneetBinnenHalen) { // omlaag zolang A is ingedrukt
            magneet.setYMotion(Y_DOWN_SPEED);
        }

    }

    private void draw(GraphicsContext gc) {
        if(victory){
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            text.setFill(Color.GREEN);
            text.setFont(new Font("Arial", 20));
            text.setText("new game");
            gc.setFill(Color.GREEN);
            gc.setFont(new Font("Arial",50));
            gc.fillText("Victory",250,300);
        }else {
            // achtergrond
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            // paaltje
            gc.setFill(Color.RED);
            gc.fillRect(420, 600, 30, 200);


            // ketting
            gc.setFill(Color.GRAY);
            gc.fillRect(magneet.getX() + magneet.getWidth() / 2 - 5, 0, 10, magneet.getY());

            for (Doos d : dozen) {
                d.draw(gc);
            }
            magneet.draw(gc);
        }
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
                magneetBinnenHalen();
                break;
        }
    }

    private void magneetBinnenHalen() {
        magneetBinnenHalen = true;
        magneet.setYMotion(-Y_UP_SPEED);
    }
    private void cleanUp(){
        magneet.setYMotion(0);
        magneet.setXMotion(0);
        magneet.setX(WIDTH/2);
        magneet.setY(START_HOOGTE);
        for (Doos d : dozen) {
        d.setYMotion(0);
        d.setXMotion(0);
        d.setX(100);
        d.setY(-100);
        }

    }

    public static void main(String[] args) {
        launch(args);

    }
}
