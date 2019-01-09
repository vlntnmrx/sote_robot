package hsurobots.components;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class EnvironmentObject {

    private Color color;
    private int x;
    private int y;
    private int width;
    private int length;
    private double orientation;

    public EnvironmentObject(int x, int y, int width, int length, double orientation, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.length = length;
        this.orientation = orientation;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Rectangle2D getRectangle() {
        Rectangle2D r = new Rectangle2D.Double(x-length/2,y-width/2,length,width);
        return r;
    }
}

