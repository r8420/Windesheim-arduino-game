package ScrapYard;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Magneet extends Hitbox {

    private double xMotion;
    private double yMotion;
    private boolean aan;


    public Magneet(double x, double y) {
        super(x, y, 100, 100);
        this.xMotion = 0;
        this.yMotion = 0;
    }

    public void updatePos() {
        setX(getX() + xMotion);
        setY(getY() + yMotion);
    }

    public void draw(GraphicsContext gc) {
                if (aan) {
            gc.setFill(Color.RED);
            gc.fillRect(getX(), getY()+3, getWidth(), getHeight());
        }
        gc.setFill(Color.DARKGRAY);
//        gc.fillRect(getX(), getY(), getWidth(), getHeight());
        try {
            Image image = new Image(new FileInputStream("src/Images/magnet.jpg"));
            gc.drawImage(image, getX(),getY(),getWidth(),getHeight());
        }catch (FileNotFoundException fnfe){
            System.out.println("Geen plaatje");
        }d
    }

    public void setXMotion(double xMotion) {
        this.xMotion = xMotion;
    }

    public void setYMotion(double yMotion) {
        this.yMotion = yMotion;
    }

    public void setAan(boolean aan) {
        this.aan = aan;
    }

    public double getXMotion() {
        return xMotion;
    }

    public double getYMotion() {
        return yMotion;
    }

    public boolean isAan() {
        return aan;
    }
}
