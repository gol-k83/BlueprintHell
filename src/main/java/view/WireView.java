package view;

import model.Port;
import model.Wire;
import util.Constants;
import util.Vector2D;

import java.awt.*;
import java.awt.geom.Point2D;

public class WireView {
    private final Wire wire;
    private Color color;

    public WireView(Wire wire) {
        this.wire = wire;
        this.color = Color.BLUE;
    }

//    public void draw(Graphics2D g) {
//        Point2D.Double from = wire.getFromPort().getPosition().toPoint2D();
//        Point2D to = wire.getToPort().getPosition().toPoint2D();
//        WireCurveUtil.drawWire(g, from, to, color);
//    }
public void draw(Graphics2D g, GameStageView stage) {
    Port fromPort = wire.getFromPort();
    Port toPort = wire.getToPort();

    PortView fromView = stage.findPortView(fromPort);
    PortView toView = stage.findPortView(toPort);

    if (fromView == null || toView == null) return;

    Point from = fromView.getCenterPoint();
    Point to = toView.getCenterPoint();

    WireCurveUtil.drawWire(g, from, to, color);
}


    public boolean isNear(Point p) {
        Vector2D a = wire.getFromPort().getPosition();
        Vector2D b = wire.getToPort().getPosition();
        double dist = distancePointToSegment(p, a.toPoint2D(), b.toPoint2D());
        return dist < Constants.WIRE_HITBOX_RADIUS;

    }

    private double distancePointToSegment(Point p, Point2D a, Point2D b) {
        double px = b.getX() - a.getX();
        double py = b.getY() - a.getY();

        double norm = px * px + py * py;
        double u = ((p.x - a.getX()) * px + (p.y - a.getY()) * py) / norm;
        u = Math.max(0, Math.min(1, u));

        double x = a.getX() + u * px;
        double y = a.getY() + u * py;

        double dx = x - p.x;
        double dy = y - p.y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public Wire getWire() {
        return wire;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
