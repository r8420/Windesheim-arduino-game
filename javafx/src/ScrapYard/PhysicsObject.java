package ScrapYard;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * The type Physics object.
 */
public class PhysicsObject extends Hitbox {

    private static final double zwaartekracht = 0.05;
    private static final double maxValSnelheid = 8;
    private static final double wrijvingsWeerstand = 0.05;

    private double xMotion;
    private double yMotion;
    private Image auto;

    /**
     * Instantiates a new Physics object.
     *
     * @param x      the x position
     * @param y      the y position
     * @param width  the width
     * @param height the height
     */
    public PhysicsObject(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /**
     * Draws car image.
     *
     * @param gc the GraphicsContext
     */
    public void draw(GraphicsContext gc) {
        try {
            this.auto = new Image(new FileInputStream("images/autotje.png"));
        } catch (FileNotFoundException fnfe) {
            System.out.println("Kon geen plaatjes vinden.");
        }
        gc.drawImage(auto, getX(), getY(), getWidth(), getHeight());
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
     * Update position.
     *
     * @param boundWidth  the bound width
     * @param boundHeight the bound height
     */
    public void updatePos(double boundWidth, double boundHeight) {

        yMotion = Math.min(yMotion + zwaartekracht, maxValSnelheid);
        setX(getX() + xMotion);
        setY(getY() + yMotion);

        if (getX() + getWidth() > boundWidth) { // collision rechterrand
            setX(boundWidth - getWidth());
            xMotion = -xMotion / 3;

        } else if (getX() < 0) { // collision linkerrand
            setX(0);
            xMotion = -xMotion / 3;
        }

        if (getY() < 0) { // collision bovenrand
            yMotion = 0;
            setY(0);

        } else if (getY() > boundHeight - getHeight()) { // collision onderrand

            if (xMotion > 0) {
                xMotion = Math.max(xMotion - wrijvingsWeerstand, 0);
            } else {
                xMotion = Math.min(xMotion + wrijvingsWeerstand, 0);
            }
            yMotion = -yMotion / 5;
            setY(boundHeight - getHeight());
        }
    }
}
