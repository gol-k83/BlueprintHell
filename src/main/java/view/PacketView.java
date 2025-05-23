
package view;

import model.Packet;
import util.Constants;
import util.Vector2D;

import java.awt.*;


public class PacketView {
    private final Packet packet;

    public PacketView(Packet packet) {
        this.packet = packet;
    }

    public void draw(Graphics2D g) {
        Vector2D pos = packet.getPosition();
        /////////////
        int size = Constants.PACKET_BASE_SIZE * packet.getOriginalSize();
        // Size in pixels, can scale with packet.getOriginalSize()

        // Transparency based on noise level
        float alpha = 1.0f - (float) packet.getCurrentNoise() / packet.getOriginalSize();
        alpha = Math.max(0.1f, alpha); // Avoid full invisibility

        Composite original = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int x = (int) pos.getX();
        int y = (int) pos.getY();


        g.setColor(packet.getShape().getColor());
        switch (packet.getShape()) {
            case SQUARE ->
                    g.fillRect(x - size / 2, y - size / 2, size, size); // Filled square

            case TRIANGLE -> {
                Polygon triangle = new Polygon();
                triangle.addPoint(x, y - size / 2);
                triangle.addPoint(x - size / 2, y + size / 2);
                triangle.addPoint(x + size / 2, y + size / 2);
                g.fillPolygon(triangle); // Filled triangle
            }
        }

        // Draw gray border
        g.setColor(Color.LIGHT_GRAY);
        Stroke originalStroke = g.getStroke();
        g.setStroke(new BasicStroke(1));

        switch (packet.getShape()) {
            case SQUARE ->
                    g.drawRect(x - size / 2, y - size / 2, size, size);

            case TRIANGLE -> {
                Polygon triangle = new Polygon();
                triangle.addPoint(x, y - size / 2);
                triangle.addPoint(x - size / 2, y + size / 2);
                triangle.addPoint(x + size / 2, y + size / 2);
                g.drawPolygon(triangle);
            }
        }

        g.setStroke(originalStroke);
        g.setComposite(original);
    }

    public Packet getPacket() {
        return packet;
    }
}










