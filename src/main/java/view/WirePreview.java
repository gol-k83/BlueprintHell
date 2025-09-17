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
    private Point2D fromScreenPosition;

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
        this.color = isValid ? Color.GREEN : Color.RED;
    }


public void draw(Graphics2D g, GameStageView stage) {

    SystemNodeView fromNodeView = stage.findViewFor(fromPort.getParentNode());
    PortView fromPortView = stage.findPortView(fromPort);

    if (fromPortView == null || fromNodeView == null) return;


    Point from = fromPortView.getScreenPosition(fromNodeView);


    Point to = currentMousePosition.toPoint();


    Point control = new Point(
            (from.x + to.x) / 2,
            (from.y + to.y) / 2
    );


    QuadCurve2D curve = new QuadCurve2D.Double();
    curve.setCurve(from, control, to);

    g.setColor(color);
    g.draw(curve);

    System.out.println(" WirePreview from: " + from.x + "," + from.y + " → to: " + to.x + "," + to.y);
}


    public boolean isValid() {
        return valid;
    }

    public Port getFromPort() {
        return fromPort;
    }
    public Point2D getCurrentMousePoint() {
        return currentMousePosition.toPoint2D();
    }
    public Point2D getStartScreenPoint() {
        return fromScreenPosition;
    }
}