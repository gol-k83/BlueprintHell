package util;

import java.awt.*;
import java.awt.geom.Point2D;

public class Vector2D {
    private double x;
    private double y ;

    public Vector2D(double x,double y){
        this.x=x;
        this.y=y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    public Vector2D add(Vector2D other){
        return new Vector2D(this.x+other.x,this.y+other.y);
    }
    public Vector2D subtract(Vector2D other){
        return new Vector2D(this.x-other.x,this.y-other.y);
    }
    public Vector2D multiply(double scaler){
        return  new Vector2D(this.x*scaler,this.y*scaler);

    }
    // محاسبه فاصله با بردار دیگر
    public double distanceTo(Vector2D other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
         }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

public Vector2D normalize(){
        double len=length();
        if (len!=0){
    return  new Vector2D(this.x/len,this.y/len);
    }return  new Vector2D(0,0);
}

public double angleWith(Vector2D other){
    double dotProduct=this.x*other.x+this.y*other.y;
    double lengthes=this.length()*other.length();
    if (lengthes!=0){
        return Math.acos(dotProduct/lengthes);
    }
    return 0;
}

@Override
public String toString() {
    return String.format("(%.2f, %.2f)", x, y);
}

    public Point toPoint() {
        return new Point((int) x, (int) y);
    }

    public Point2D.Double toPoint2D() {
        return new Point2D.Double(x, y);
    }

    }















