package view;

import model.Port;
import model.SystemNode;
import util.Constants;
import util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SystemNodeView extends JComponent {
    private final SystemNode node;
    private final int width = Constants.NODE_WIDTH;
    private final int height = Constants.NODE_HEIGHT;
    private final List<PortView> inputPortViews;
    private final List<PortView> outputPortViews;
    private boolean selected;

    public SystemNodeView(SystemNode node) {
        this.node = node;
        this.inputPortViews = node.getInputPorts().stream().map(PortView::new).collect(Collectors.toList());
        this.outputPortViews = node.getOutputPorts().stream().map(PortView::new).collect(Collectors.toList());
        this.selected = false;

        setOpaque(false); // بک‌گراند کامپوننت شفاف بشه تا چیزا دیده بشن
        setSize(Constants.NODE_WIDTH, Constants.NODE_HEIGHT); // سایز فیزیکی نود
        setLayout(null);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g); // همون متد draw فعلی پس

    }

    public void draw(Graphics2D g) {
        Vector2D pos = node.getPosition();
        int x = 0;
        int y = 0;

        // Draw node body
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, width, height);

        // Highlight if selected
        if (selected) {
            g.setColor(Color.YELLOW);
            g.setStroke(new BasicStroke(2));
            g.drawRect(x - 2, y - 2, width + 4, height + 4);
        }

        // Draw indicator if reference
        if (node.isReference()) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            g.drawRect(x, y, width, height);
        }

//        // Draw title
//        g.setColor(Color.LIGHT_GRAY);
//        g.fillRoundRect(x + 5, y + 5, 60, 15, 10, 10);
//        g.setColor(Color.WHITE);
//        g.setFont(new Font("Arial", Font.PLAIN, 10));
//        g.drawString(node.getDisplayName(), x + 10, y + 17);

        // Status indicator
        g.setColor(node.isFullyConnected() ? Color.CYAN : Color.GRAY);
       // g.fillOval(x + width / 2 - 5, y + 25, 10, 10);



        Polygon diamond = new Polygon();
        int cx = x + width / 2;
        int cy = y + 25;
        diamond.addPoint(cx, cy - 10);
        diamond.addPoint(cx -30, cy);
        diamond.addPoint(cx, cy + 10);
        diamond.addPoint(cx + 30, cy);
        g.fillPolygon(diamond);


        // Draw input ports (left side)
        int spacing = height / (inputPortViews.size() + 1);
        for (int i = 0; i < inputPortViews.size(); i++) {
            int portY = y + (i + 1) * spacing;
            inputPortViews.get(i).draw(g, x , portY);
        }

        // Draw output ports (right side)
        spacing = height / (outputPortViews.size() + 1);
        for (int i = 0; i < outputPortViews.size(); i++) {
            int portY = y + (i + 1) * spacing;
            outputPortViews.get(i).draw(g, x + width - Constants.PORT_SIZE, portY);
        }

        // Draw RUN label if reference
        if (node.isReference()) {
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("RUN", x + width / 2 - 10, y + height - 10);
        }
    }

    public Rectangle getBounds() {
        Vector2D pos = node.getPosition();
        return new Rectangle((int) pos.getX(), (int) pos.getY(), width, height);
    }

    public void moveTo(int newX, int newY) {
        node.setPosition(new Vector2D(newX, newY));
    }

    public boolean contains(Point p) {
        return getBounds().contains(p);
    }

    public SystemNode getNode() {
        return node;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public List<PortView> getAllPorts() {
        List<PortView> all = new ArrayList<>();
        all.addAll(inputPortViews);
        all.addAll(outputPortViews);
        return all;
    }
}
