
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
    private JButton runButton; // ← دکمهٔ واقعی

    public SystemNodeView(SystemNode node) {
        this.node = node;
        this.inputPortViews  = node.getInputPorts().stream().map(PortView::new).collect(Collectors.toList());
        this.outputPortViews = node.getOutputPorts().stream().map(PortView::new).collect(Collectors.toList());
        this.selected = false;

        setOpaque(false);
        setLayout(null);
        setSize(width, height);

        // فقط برای نود مرجع دکمه بساز
        if (node.isReference()) {
            runButton = new JButton("▶ RUN");
            runButton.setFocusable(false);
            runButton.setMargin(new Insets(2, 8, 2, 8));
            runButton.setToolTipText("Start stage");
            runButton.setVisible(true);
            add(runButton);
        }
    }


    @Override
    public void doLayout() {
        super.doLayout();
        if (runButton != null) {
            int bw = 64, bh = 24;
            int bx = (width - bw) / 2;
            int by = height - bh - 6;
            runButton.setBounds(bx, by, bw, bh);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    public void draw(Graphics2D g) {
        int x = 0, y = 0;

        // بدنهٔ نود
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, width, height);

        // هایلایت انتخاب
        if (selected) {
            g.setColor(Color.YELLOW);
            g.setStroke(new BasicStroke(2));
            g.drawRect(x - 2, y - 2, width + 4, height + 4);
        }

        // اندیکاتور نود مرجع
        if (node.isReference()) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            g.drawRect(x, y, width, height);
        }

        // دایموند وضعیت اتصال
        g.setColor(node.isFullyConnected() ? Color.CYAN : Color.GRAY);
        Polygon diamond = new Polygon();
        int cx = x + width / 2;
        int cy = y + 25;
        diamond.addPoint(cx, cy - 10);
        diamond.addPoint(cx - 30, cy);
        diamond.addPoint(cx, cy + 10);
        diamond.addPoint(cx + 30, cy);
        g.fillPolygon(diamond);

        // پورت‌های ورودی
        int spacing = height / (inputPortViews.size() + 1);
        for (int i = 0; i < inputPortViews.size(); i++) {
            int portY = y + (i + 1) * spacing;
            inputPortViews.get(i).draw(g, x, portY);
        }

        // پورت‌های خروجی
        spacing = height / (outputPortViews.size() + 1);
        for (int i = 0; i < outputPortViews.size(); i++) {
            int portY = y + (i + 1) * spacing;
            outputPortViews.get(i).draw(g, x + width - Constants.PORT_SIZE, portY);
        }


        if (node.isReference()) {
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("RUN", x + width / 2 - 12, y + height - 34);
        }
    }

    public JButton getRunButton() { return runButton; }

    public void setRunEnabled(boolean enabled) {
        if (runButton != null) runButton.setEnabled(enabled);
    }


    public Rectangle getNodeRect() {
        Vector2D pos = node.getPosition();
        return new Rectangle((int) pos.getX(), (int) pos.getY(), width, height);
    }

    public void moveTo(int newX, int newY) {
        node.setPosition(new Vector2D(newX, newY));
        setLocation(newX, newY);
        revalidate();
        repaint();
    }

    public boolean contains(Point p) {
        return super.getBounds().contains(p);
    }

    public SystemNode getNode() { return node; }

    public void setSelected(boolean selected) { this.selected = selected; }
    public boolean isSelected() { return selected; }

    public List<PortView> getAllPorts() {
        List<PortView> all = new ArrayList<>();
        all.addAll(inputPortViews);
        all.addAll(outputPortViews);
        return all;
    }
}
