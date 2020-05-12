package ScrapYard;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Doos extends Hitbox {

    private static double zwaartekracht = 0.05;
    private static double maxValSnelheid = 8;
    private static double wrijvingsWeerstand = 0.05;

    private double xMotion;
    private double yMotion;

    public Doos(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BROWN);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());
    }

    public void setXMotion(double xMotion) {
        this.xMotion = xMotion;
    }

    public void setYMotion(double yMotion) {
        this.yMotion = yMotion;
    }

    public void updatePos(double boundWidth, double boundHeight) {
        
        yMotion = Math.min(yMotion + zwaartekracht, maxValSnelheid);
        setX(getX() + xMotion);
        setY(getY() + yMotion);

        if (getX() + getWidth() > boundWidth) { // collision rechterrand
            setX(boundWidth - getWidth());
            xMotion = -xMotion/3;

        } else if (getX() < 0) { // collision linkerrand
            setX(0);
            xMotion = -xMotion/3;
        }

        if (getY() < 0) { // collision bovenrand
            yMotion = 0;
            setY(0);

        } else if (getY() > boundHeight-getHeight()) { // collision onderrand

            if (xMotion > 0) {
                xMotion = Math.max(xMotion - wrijvingsWeerstand, 0);
            } else {
                xMotion = Math.min(xMotion + wrijvingsWeerstand, 0);
            }
            yMotion = -yMotion/5;
            setY(boundHeight-getHeight());
        }
    }
}
