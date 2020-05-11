package ScrapYard;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class StartupScreen extends Application {
    private static final double WIDTH = 600;
    private static final double HEIGHT = 700;
    private Text newgame = new Text("NEW GAME");
    private Rectangle blokje =  new Rectangle();
    private int rotation;
    private int XasBlokje;


    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //Drawing a Circle
        Circle circle = new Circle();

        //Setting the position of the circle
        circle.setCenterX(300.0f);
        circle.setCenterY(135.0f);
        //Setting the radius of the circle
        circle.setRadius(50.0f);

        //Setting the color of the circle
        circle.setFill(Color.BROWN);

        //Setting the stroke width of the circle
        circle.setStrokeWidth(20);

        //Creating scale Transition
        ScaleTransition scaleTransition = new ScaleTransition();

        //Setting the duration for the transition
        scaleTransition.setDuration(Duration.millis(1000));

        //Setting the node for the transition
        scaleTransition.setNode(circle);

        //Setting the dimensions for scaling
        scaleTransition.setByY(1.5);
        scaleTransition.setByX(1.5);

        //Setting the cycle count for the translation
        scaleTransition.setCycleCount(50);

        //Setting auto reverse value to true
        scaleTransition.setAutoReverse(true);
        //Playing the animation
        scaleTransition.play();
        //Creating a Group object
        Group root = new Group();
        root.getChildren().add(canvas);
        root.getChildren().add(circle);
        root.getChildren().add(newgame);
        root.getChildren().add(blokje);
        newgame.setX(260);
        newgame.setY(350);
        blokje.setX(0);
        blokje.setY(HEIGHT-100);
        blokje.setWidth(50);
        blokje.setHeight(50);



        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Main main = new Main();
                try {
                    main.start(new Stage());
                    stage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("clicked");
            }
        };
        newgame.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);

        //Creating a scene object
        Scene scene = new Scene(root, WIDTH, HEIGHT);


        //Setting title to the Stage
        stage.setTitle("Scale transition example");

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

        Timeline t2 = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            getRotation();
        }));
        t2.setCycleCount(Timeline.INDEFINITE);
        t2.play();

    //Displaying the contents of the stage
        stage.show();
}
    private void draw(GraphicsContext gc) {
        gc.restore();
        newgame.setStroke(Color.BLACK);
        gc.setFill(Color.GREEN);
        gc.fillRect(0,HEIGHT-50,WIDTH,50);
        blokje.setFill(Color.BLUE);
    }
    public void getRotation(){

        if (blokje.getX() >600){
            XasBlokje = 0;

        }else {
            XasBlokje += 10;
        }
        blokje.setX(XasBlokje);
        if (rotation < 360){
            rotation = rotation +45;
        }else {
            rotation = 45;
        }
        blokje.setRotate(rotation);
        if (rotation % 10 == 5){
            blokje.setY(blokje.getY()-5);
        }else {
            blokje.setY(blokje.getY()+5);
        }
    }
    public static void main(String args[]){
        launch(args);
    }
}

