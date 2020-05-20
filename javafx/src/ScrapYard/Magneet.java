package ScrapYard;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * The type Magneet.
 */
public class Magneet extends Hitbox {

    private double xMotion;
    private double yMotion;
    private boolean aan;
    private Image aanPlaatje;
    private Image uitPlaatje;


    /**
     * Instantiates a new Magneet.
     *
     * @param x the x position
     * @param y the y position
     */
    public Magneet(double x, double y) {
        super(x, y, 100, 100);
        this.xMotion = 0;
        this.yMotion = 0;
        try {
            this.uitPlaatje = new Image(new FileInputStream("images/magneet_uit.png"));
            this.aanPlaatje = new Image(new FileInputStream("images/magneet_aan.png"));
        } catch (FileNotFoundException fnfe) {
            System.out.println("Kon geen plaatjes vinden.");
        }
    }

    /**
     * Update position.
     */
    public void updatePos() {
        setX(getX() + xMotion);
        setY(getY() + yMotion);
    }

    /**
     * Draws magnet image.
     *
     * @param gc the GraphicsContext
     */
    public void draw(GraphicsContext gc) {
        if (aan) {
            gc.drawImage(aanPlaatje, getX(), getY(), getWidth(), getHeight());
        } else {
            gc.drawImage(uitPlaatje, getX(), getY(), getWidth(), getHeight());
        }
    }

    /**
     * Sets x motion.
     *
     * @param xMotion the x motion
     */
    public void setXMotion(double xMotion) {
        this.xMotion = xMotion;
    }

    /**
     * Sets y motion.
     *
     * @param yMotion the y motion
     */
    public void setYMotion(double yMotion) {
        this.yMotion = yMotion;
    }

    /**
     * Sets aan variable.
     *
     * @param aan the aan
     */
    public void setAan(boolean aan) {
        this.aan = aan;
    }

    /**
     * Gets x motion.
     *
     * @return the x motion
     */
    public double getXMotion() {
        return xMotion;
    }

    /**
     * Gets y motion.
     *
     * @return the y motion
     */
    public double getYMotion() {
        return yMotion;
    }

    /**
     * Gets is aan boolean.
     *
     * @return the boolean
     */
    public boolean isAan() {
        return aan;
    }
}
