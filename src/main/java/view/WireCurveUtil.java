package view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

public class WireCurveUtil {
    public static void drawWire(Graphics2D g, Point2D from, Point2D to, Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke(2));


        Point2D control = new Point2D.Double(
                (from.getX() + to.getX()) / 2,
                (from.getY() + to.getY()) / 2
        );

        QuadCurve2D curve = new QuadCurve2D.Double();
        curve.setCurve(from, control, to);
        g.draw(curve);
    }
}
