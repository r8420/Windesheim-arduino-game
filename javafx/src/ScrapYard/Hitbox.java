package ScrapYard;

import javafx.scene.shape.Rectangle;

public class Hitbox extends Rectangle {

    public Hitbox(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public boolean intersects(Rectangle that) {
        return this.intersects(
                that.getX(),
                that.getY(),
                that.getWidth(),
                that.getHeight()
        );
    }

}
