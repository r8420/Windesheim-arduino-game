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
    private Image aanPlaatje;
    private Image uitPlaatje;


    public Magneet(double x, double y) {
        super(x, y, 100, 100);
        this.xMotion = 0;
        this.yMotion = 0;
        try {
            this.uitPlaatje = new Image(new FileInputStream("images/magneet_uit.png"));
            this.aanPlaatje = new Image(new FileInputStream("images/magneet_aan.png"));
        } catch (FileNotFoundException fnfe){
            System.out.println("Kon geen plaatjes vinden.");
        }
    }

    public void updatePos() {
        setX(getX() + xMotion);
        setY(getY() + yMotion);
    }

    public void draw(GraphicsContext gc) {
        if (aan) {
            gc.drawImage(aanPlaatje, getX(),getY(),getWidth(),getHeight());
        } else {
            gc.drawImage(uitPlaatje, getX(),getY(),getWidth(),getHeight());
        }
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
