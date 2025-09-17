
package view;

import model.Port;
import model.ShapeType;
import util.Constants;
import util.Vector2D;

import java.awt.*;

public class PortView {
    private final Port port;
    private static final int SIZE = Constants.PORT_SIZE;
    private static final int TOLERANCE = 8;


    private int x, y;

    public PortView(Port port) {
        this.port = port;
    }

    public void draw(Graphics2D g, int x, int y) {
        // ذخیره برای hit-test و screen mapping
        this.x = x;
        this.y = y;

        // آنتی‌الیاس
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g.setColor(port.getType() == Port.PortType.INPUT ? Color.BLACK : Color.LIGHT_GRAY);
        g.fillRect(x - 2, y - 2, SIZE + 4, SIZE + 4);

      //مرکز
        port.setPosition(new Vector2D(x + SIZE / 2.0, y + SIZE / 2.0));

        // رسم شکل پورت بر اساس شکل سازگار
        ShapeType shape = port.getCompatibleShape();
        g.setColor(shape.getColor());

        switch (shape) {
            case SQUARE: {
                g.fillRect(x, y, SIZE, SIZE);
                break;
            }
            case TRIANGLE: {
                Polygon tri = new Polygon();
                tri.addPoint(x + SIZE / 2, y);
                tri.addPoint(x, y + SIZE);
                tri.addPoint(x + SIZE, y + SIZE);
                g.fillPolygon(tri);
                break;
            }
            case CIRCLE: {
                g.fillOval(x, y, SIZE, SIZE);
                break;
            }

            default: {

                break;
            }
        }
    }

    public Port getPort() {
        return port;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    public boolean contains(Point p) {
        Rectangle bounds = getBounds();
        if (bounds.contains(p)) return true;

        // تولرانسر
        int cx = bounds.x + SIZE / 2;
        int cy = bounds.y + SIZE / 2;
        return p.distance(cx, cy) <= TOLERANCE;
    }

    public Point getCenterPoint() {
        return new Point(x + SIZE / 2, y + SIZE / 2);
    }


    public Point getScreenPosition(SystemNodeView nodeView) {
        Point nodeLoc = nodeView.getLocation();
        return new Point(
                nodeLoc.x + this.x + SIZE / 2,
                nodeLoc.y + this.y + SIZE / 2
        );
    }
}
