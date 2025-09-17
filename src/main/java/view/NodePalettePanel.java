
package view;

import model.SystemNode;
import util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NodePalettePanel extends JPanel {

    private final List<SystemNodeView> availableNodes;


    private final List<SystemNodeView> stage2Nodes = new ArrayList<>();
    private boolean stage2Unlocked = false;

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


    public void addStage2Nodes(List<SystemNode> nodes) {
        if (nodes == null) return;
        for (SystemNode n : nodes) {
            stage2Nodes.add(new SystemNodeView(n));
        }
        repaint();
    }


    public void unlockStage2() {
        this.stage2Unlocked = true;
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padding = 35;
        int y = padding;


        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Given Nodes", 60, 20);

        for (SystemNodeView view : availableNodes) {
            view.getNode().setPosition(new Vector2D(50, y));
            view.draw(g2d);
            y += 120;
        }


        if (stage2Unlocked && !stage2Nodes.isEmpty()) {
            y += 10; // کمی فاصله از بخش بالا
            g2d.drawString("Stage 2 Nodes", 60, y);
            y += 15;

            for (SystemNodeView view : stage2Nodes) {
                view.getNode().setPosition(new Vector2D(50, y));
                view.draw(g2d);
                y += 120;
            }
        }
    }


    public List<SystemNodeView> getAvailableNodes() {
        if (!stage2Unlocked) return availableNodes;


        List<SystemNodeView> all = new ArrayList<>(availableNodes);
        all.addAll(stage2Nodes);
        return all;
    }


    public void removeNode(SystemNodeView view) {
        if (view == null) return;
        if (!availableNodes.remove(view)) {
            stage2Nodes.remove(view);
        }
        repaint();
    }
}
