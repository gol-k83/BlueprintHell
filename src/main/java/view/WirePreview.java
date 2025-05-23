package view;

import util.Vector2D;
import model.*;

import java.awt.geom.Point2D;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class WirePreview {
    private Vector2D currentMousePosition;
    private final Port fromPort;
    private boolean valid;
    private Color color;
    public WirePreview(Port fromPort, Vector2D customStart) {
        this.fromPort = fromPort;
        this.currentMousePosition = customStart;
        this.valid = false;
        this.color = Color.BLUE;
    }


    public WirePreview(Port fromPort) {
        this.fromPort = fromPort;
        this.currentMousePosition = fromPort.getPosition();
        this.valid = false;
        this.color = Color.BLUE;
    }

    public void updateMousePosition(Vector2D mousePosition) {
        this.currentMousePosition = mousePosition;
    }

    public void updateValidity(boolean isValid) {
        this.valid = isValid;
        this.color = isValid ? Color.GREEN : Color.RED; // اگر اتصال معتبر بود، سبز؛ در غیر این صورت قرمز
    }

    public void draw(Graphics2D g) {
        Point2D from = fromPort.getPosition().toPoint2D();
        Point2D to = currentMousePosition.toPoint2D();

        Point2D control = new Point2D.Double(
                (from.getX() + to.getX()) / 2, (from.getY() + to.getY()) / 2);
        QuadCurve2D curve = new QuadCurve2D.Double();
        curve.setCurve(from, control, to);
        g.setColor(color);
        g.draw(curve);


    }

    public boolean isValid() {
        return valid;
    }

    public Port getFromPort() {
        return fromPort;
    }
}




















