package view;

import config.StageConfig;
import controller.WireBuilder;
import controller.WireManager;
import model.Port;
import model.SystemNode;
import model.Wire;
import util.Constants;
import util.ImageAssets;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameStageView extends JPanel {
    private final BackgroundGridPanel backgroundPanel;
    private final NodePalettePanel nodePalettePanel;
    private final GameTipBarView tipBarView;
    private final HUDPanel hudPanel;
    private final StageLabel stageLabel;
    private final List<SystemNodeView> nodeViews = new ArrayList<>();
    private final WireManager wireManager = new WireManager();
    private final List<WireView> wireViews = new ArrayList<>();
    private WireBuilder wireBuilder;

    public GameStageView() {
        setLayout(null);
        setBounds(0, 0, 1200, 800);

        // بک‌گراند شطرنجی
        backgroundPanel = new BackgroundGridPanel();
        backgroundPanel.setBounds(250, 50, 1000, 900);
      //  add(backgroundPanel);

        // ستون سمت چپ (Given Nodes)
        nodePalettePanel = new NodePalettePanel();
        nodePalettePanel.setBounds(0, 50, 250, 700);
        add(nodePalettePanel);

        // نوار بالای صفحه
        ImageIcon tipImage = new ImageIcon(ImageAssets.get("tip_bar.png"));
        tipBarView = new GameTipBarView(tipImage);
        tipBarView.setBounds(250, 0, 950, 70);
        add(tipBarView);

        // برچسب مرحله (مثلاً DAY1)
        stageLabel = new StageLabel("DAY 1");
        stageLabel.setBounds(0, 0, 300, 50);
        add(stageLabel);

        // HUD پایین صفحه (پنهان/نمایش‌پذیر)
        hudPanel = new HUDPanel();
        hudPanel.setBounds(250, 750, 900, 50);

        add(hudPanel);

        List<SystemNode> allGivenNodes = new ArrayList<>();
        allGivenNodes.addAll(StageConfig.getStage1InitialNodes());
        allGivenNodes.addAll(StageConfig.getStage1GivenNodes());

        for (SystemNode node : allGivenNodes) {
            nodePalettePanel.addNode(node); // نمایش در ستون سمت چپ
        }
    }

    // دیگر نیازی به paintComponent برای رسم دستی نودها نیست!

    public StageLabel getStageLabel() { return stageLabel; }
    public NodePalettePanel getNodePalettePanel() { return nodePalettePanel; }
    public BackgroundGridPanel getBackgroundPanel() { return backgroundPanel; }
    public HUDPanel getHudPanel() { return hudPanel; }
    public GameTipBarView getTipBarView() { return tipBarView; }

    public Port findPortAt(Point click) {
        for (SystemNodeView nodeView : nodeViews) {
            Point nodeLocation = nodeView.getLocation();

            for (PortView portView : nodeView.getAllPorts()) {
                Rectangle bounds = portView.getBounds();

                // تبدیل به مختصات جهانی (global)
                Rectangle globalBounds = new Rectangle(
                        bounds.x + nodeLocation.x,
                        bounds.y + nodeLocation.y,
                        bounds.width,
                        bounds.height
                );

                if (globalBounds.contains(click)) {
                    return portView.getPort();
                }
            }
        }
        return null;
    }
    public PortView findPortViewAt(Point click) {
        for (SystemNodeView nodeView : nodeViews) {
            for (PortView portView : nodeView.getAllPorts()) {
                if (portView.contains(click)) {
                    return portView;
                }
            }
        }
        return null;
    }


    public WireView findWireNear(Point point) {
        for (WireView wireView : wireViews) {
            if (wireView.isNear(point)) {
                return wireView;
            }
        }
        return null;
    }

    public void addWire(Wire wire) {
        wireViews.add(new WireView(wire));
        // Optionally: repaint();  // برای آپدیت سیم‌ها
    }

    public WireManager getWireManager() { return wireManager; }
    public List<SystemNodeView> getSystemNodeViews() { return nodeViews; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // رسم بک‌گراند (مثلاً grid_background.png)
        Image bgImage = ImageAssets.get("grid_background.png");
        if (bgImage != null) {
            g2d.drawImage(bgImage, 250, 50, 900, 700, this); // ناحیه زمین بازی
        }

        // اگر سیم‌ها رو دستی می‌کشی، اینجا رسمشون کن:
        for (WireView wireView : wireViews) {
            wireView.draw(g2d);
        }
        if (wireBuilder != null) {
            wireBuilder.drawPreview(g2d);
        }
    }
    public void addNodeToStage(SystemNodeView view) {
        if (view != null){
      // nodeViews.add(view);////
    //   this.add(view);////
        view.setBounds(
                (int) view.getNode().getPosition().getX(),
                (int) view.getNode().getPosition().getY(),
                Constants.NODE_WIDTH, Constants.NODE_HEIGHT );
            view.setVisible(true);
            this.add(view);
            nodeViews.add(view); // اضافه به لیست داخلی
            revalidate();
            repaint();}
        else {
            System.err.println("⚠️ View is null!");
        }
    }
    public void setWireBuilder(WireBuilder builder) {
        this.wireBuilder = builder;
    }

}

