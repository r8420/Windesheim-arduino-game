package ScrapYard;


import javafx.scene.shape.Rectangle;

/**
 * The Hitbox class.
 */
public class Hitbox extends Rectangle {


    /**
     * Instantiates a new Hitbox.
     *
     * @param x      the x position
     * @param y      the y position
     * @param width  the width
     * @param height the height
     */
    public Hitbox(double x, double y, double width, double height) {
        super(x, y, width, height);
    }


    /**
     * Intersects boolean.
     *
     * @param that the Rectangle
     * @return boolean if rectangle intersects
     */
    public boolean intersects(Rectangle that) {
        return this.intersects(
                that.getX(),
                that.getY(),
                that.getWidth(),
                that.getHeight()
        );
    }



}
