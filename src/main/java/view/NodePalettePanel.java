package view;

import model.SystemNode;
import util.Vector2D;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NodePalettePanel extends JPanel {
    private final List<SystemNodeView> availableNodes;

    public NodePalettePanel() {
        this.availableNodes = new ArrayList<>();
        setBackground(Color.GRAY);
        setLayout(null);
    }

    public void addNode(SystemNode node) {
        SystemNodeView view = new SystemNodeView(node);
        availableNodes.add(view);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padding = 35;
        int y = padding;
        for (SystemNodeView view : availableNodes) {
            view.getNode().setPosition(new Vector2D(50, y));
            view.draw(g2d);
            y += 120; // فاصله بین نودها در ستون
        }
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Given Nodes", 60, 20);
    }
    public List<SystemNodeView> getAvailableNodes() {
        return availableNodes;
    }
    public void removeNode(SystemNodeView view) {
        availableNodes.remove(view);
        repaint();
    }

}
