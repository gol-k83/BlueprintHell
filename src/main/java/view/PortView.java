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

    private int x, y; // ذخیره آخرین مکان رسم شده برای تشخیص برخورد

    public PortView(Port port) {
        this.port = port;
    }

    public void draw(Graphics2D g, int x, int y) {
        // ذخیره مختصات برای hit-test
        this.x = x;
        this.y = y;

        // حاشیه پورت‌ها بر اساس نوع: ورودی = مشکی، خروجی = خاکستری
        g.setColor(port.getType() == Port.PortType.INPUT ? Color.BLACK : Color.LIGHT_GRAY);
        g.fillRect(x - 2, y - 2, SIZE + 4, SIZE + 4);

//        g.setColor(port.getType() == Port.PortType.INPUT ? Color.BLACK : Color.LIGHT_GRAY);
//        g.fillRect(x, y, SIZE, SIZE);
        port.setPosition(new Vector2D(x + SIZE / 2.0, y + SIZE / 2.0)); // مرکز پورت
/////++++++++++++++++++
        // رسم شکل داخل پورت بر اساس نوع پکت سازگار
        g.setColor(port.getCompatibleShape().getColor());
        ShapeType shape = port.getCompatibleShape();

        if (shape == ShapeType.SQUARE) {
            g.fillRect(x, y, SIZE, SIZE);
        } else if (shape == ShapeType.TRIANGLE) {
            Polygon triangle = new Polygon();
            triangle.addPoint(x + SIZE / 2, y);
            triangle.addPoint(x, y + SIZE);
            triangle.addPoint(x + SIZE, y + SIZE);
            g.fillPolygon(triangle);
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

        if (bounds.contains(p)) {
            return true;
        }

        // بررسی فاصله اقلیدسی برای دقت بالاتر
        int centerX = bounds.x + SIZE / 2;
        int centerY = bounds.y + SIZE / 2;
        double distance = p.distance(centerX, centerY);

        return distance <= TOLERANCE;
    }
    public Point getCenterPoint() {
        return new Point(x + SIZE / 2, y + SIZE / 2);
    }

}