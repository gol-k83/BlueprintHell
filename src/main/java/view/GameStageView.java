
//package view;
//
//import config.StageConfig;
//import controller.PacketSpawner;
//import controller.WireBuilder;
//import controller.WireManager;
//import model.*;
//import util.Constants;
//import util.ImageAssets;
//import util.Vector2D;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//
//public class GameStageView extends JPanel {
//    private final BackgroundGridPanel backgroundPanel;
//    private final NodePalettePanel nodePalettePanel;
//    private final GameTipBarView tipBarView;
//    private final HUDPanel hudPanel;
//    private final StageLabel stageLabel;
//
//    private final List<SystemNodeView> nodeViews = new ArrayList<>();
//    private final WireManager wireManager = new WireManager();
//    private final List<WireView> wireViews = new ArrayList<>();
//
//    private WireBuilder wireBuilder;
//    private PacketSpawner spawner;
//
//    private double zoom = 1.0;
//    private int cameraX = 0;
//    private int cameraY = 0;
//
//    private Timer readinessTimer;
//
//    public GameStageView() {
//        setLayout(null);
//        setBounds(0, 0, 1200, 800);
//
//        backgroundPanel = new BackgroundGridPanel();
//        backgroundPanel.setBounds(250, 50, 1000, 900);
//        // add(backgroundPanel);
//
//        nodePalettePanel = new NodePalettePanel();
//        nodePalettePanel.setBounds(0, 50, 250, 700);
//        nodePalettePanel.setOpaque(true);
//        add(nodePalettePanel);
//
//        ImageIcon tipImage = new ImageIcon(ImageAssets.get("tip_bar.png"));
//        tipBarView = new GameTipBarView(tipImage);
//        tipBarView.setBounds(250, 0, 950, 70);
//        tipBarView.setOpaque(true);
//        add(tipBarView);
//
//        stageLabel = new StageLabel("DAY 1");
//        stageLabel.setBounds(0, 0, 300, 50);
//        stageLabel.setOpaque(false);
//        add(stageLabel);
//
//        hudPanel = new HUDPanel();
//        hudPanel.setBounds(250, 750, 900, 50);
//        hudPanel.setOpaque(true);
//        add(hudPanel);
//
//        // Stage 1 nodes → palette
//        List<SystemNode> allGivenNodes = new ArrayList<>();
//        allGivenNodes.addAll(StageConfig.getStage1InitialNodes());
//        allGivenNodes.addAll(StageConfig.getStage1GivenNodes());
//        for (SystemNode node : allGivenNodes) nodePalettePanel.addNode(node);
//
//        SwingUtilities.invokeLater(this::wireUpRunButtonIfReferenceExists);
//    }
//
//    // ------------ API ------------
//
//    public void setSpawner(PacketSpawner spawner) { this.spawner = spawner; }
//    public PacketSpawner getSpawner() { return spawner; }
//
//    public StageLabel getStageLabel() { return stageLabel; }
//    public NodePalettePanel getNodePalettePanel() { return nodePalettePanel; }
//    public BackgroundGridPanel getBackgroundPanel() { return backgroundPanel; }
//    public HUDPanel getHudPanel() { return hudPanel; }
//    public GameTipBarView getTipBarView() { return tipBarView; }
//    public WireManager getWireManager() { return wireManager; }
//    public List<SystemNodeView> getSystemNodeViews() { return nodeViews; }
//    public void setWireBuilder(WireBuilder builder) { this.wireBuilder = builder; }
//
//    public void addWire(Wire wire) {
//        wireViews.add(new WireView(wire));
//        repaint();
//    }
//
//    public void removeWire(Wire wire) {
//        wireViews.removeIf(view -> view.getWire() == wire);
//        repaint();
//    }
//
//    public WireView getWireViewFor(Wire wire) {
//        for (WireView view : wireViews) if (view.getWire() == wire) return view;
//        return null;
//    }
//
//    public Port findPortAt(Point click) {
//        for (SystemNodeView nodeView : nodeViews) {
//            Point nodeLocation = nodeView.getLocation();
//            for (PortView portView : nodeView.getAllPorts()) {
//                Rectangle bounds = portView.getBounds();
//                Rectangle globalBounds = new Rectangle(
//                        bounds.x + nodeLocation.x,
//                        bounds.y + nodeLocation.y,
//                        bounds.width,
//                        bounds.height
//                );
//                if (globalBounds.contains(click)) return portView.getPort();
//            }
//        }
//        return null;
//    }
//
//    public PortView findPortViewAt(Point click) {
//        for (SystemNodeView nodeView : nodeViews)
//            for (PortView portView : nodeView.getAllPorts())
//                if (portView.contains(click)) return portView;
//        return null;
//    }
//
//    public WireView findWireNear(Point point) {
//        for (WireView wireView : wireViews)
//            if (wireView.isNear(point, this)) return wireView;
//        return null;
//    }
//
//    public void addNodeToStage(SystemNodeView view) {
//        if (view == null) return;
//        view.setBounds(
//                (int) view.getNode().getPosition().getX(),
//                (int) view.getNode().getPosition().getY(),
//                Constants.NODE_WIDTH, Constants.NODE_HEIGHT
//        );
//        view.setVisible(true);
//        this.add(view);
//        nodeViews.add(view);
//        revalidate();
//        repaint();
//
//        if (view.getNode().isReference()) wireUpRunButtonIfReferenceExists();
//    }
//
//    public PortView findPortView(Port port) {
//        for (SystemNodeView nodeView : nodeViews)
//            for (PortView pv : nodeView.getAllPorts())
//                if (pv.getPort() == port) return pv;
//        return null;
//    }
//
//    public SystemNodeView findViewFor(SystemNode node) {
//        for (SystemNodeView view : nodeViews)
//            if (view.getNode() == node) return view;
//        return null;
//    }
//
//    public Vector2D toWorld(Point screen) {
//        double worldX = (screen.x - cameraX) / zoom;
//        double worldY = (screen.y - cameraY) / zoom;
//        return new Vector2D(worldX, worldY);
//    }
//
//    // ------------ Update (logic & render helpers) ------------
//
//    public void update(double dt) {
//        if (spawner != null) spawner.update(dt);
//
//        for (SystemNodeView nv : nodeViews) {
//            SystemNode n = nv.getNode();
//            if (n != null) n.update(dt);
//        }
//        for (WireView wv : wireViews) {
//            Wire w = wv.getWire();
//            if (w != null) w.update(dt);
//        }
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2d = (Graphics2D) g;
//
//        Image bgImage = ImageAssets.get("grid_background.png");
//        if (bgImage != null) g2d.drawImage(bgImage, 250, 50, 900, 700, this);
//
//        for (WireView wireView : wireViews) {
//            wireView.draw(g2d, this);
//        }
//        if (wireBuilder != null) wireBuilder.drawPreview(g2d, this);
//    }
//
//    // =================================================================
//    //                           Run Button
//    // =================================================================
//
//    private void wireUpRunButtonIfReferenceExists() {
//        SystemNodeView refView = null;
//        for (SystemNodeView v : nodeViews) {
//            if (v.getNode().isReference()) { refView = v; break; }
//        }
//        if (refView == null) return;
//
//        JButton runBtn = refView.getRunButton();
//        if (runBtn == null) return;
//
//        for (ActionListener al : runBtn.getActionListeners()) runBtn.removeActionListener(al);
//        runBtn.addActionListener(e -> onRunClicked(runBtn));
//
//        // برای تست، فعلاً همیشه قابل کلیک:
//        runBtn.setEnabled(true);
//    }
//
//    private void onRunClicked(JButton runBtn) {
//        System.out.println("[RUN] clicked");
//
//        if (spawner == null) {
//            System.out.println("[RUN] spawner is null");
//            return;
//        }
//        SystemNode ref = findReferenceNode();
//        if (ref == null) {
//            System.out.println("[RUN] no reference node found");
//            return;
//        }
//
//        // اگر خواستی «بدون شرط» همین حالا یک پکت روی اولین وایر بینداز:
//        // spawner.forceSpawnOnFirstWire(ref, PacketSpawner.Kind.MSG_SQUARE);
//
//        // زمان‌بندی یک‌باره و شروع
//        spawner.clearAll();
//        spawner.scheduleOnce(ref, PacketSpawner.Kind.MSG_SQUARE, 0.0);
//        System.out.println("[RUN] scheduled once. tasks=" + spawner.activeTasks());
//        spawner.start();
//
//        runBtn.setEnabled(false);
//    }
//
//    private SystemNode findReferenceNode() {
//        for (SystemNodeView v : nodeViews)
//            if (v.getNode().isReference()) return v.getNode();
//        return null;
//    }
//
//    // =================================================================
//    //                    Optional: Stage readiness checks
//    // =================================================================
//
//    @SuppressWarnings("unused")
//    private boolean isStageReady() {
//        for (SystemNodeView nv : nodeViews) if (!nv.getNode().isFullyConnected()) return false;
//        return !isAnyWireCrossingAnyNode();
//    }
//
//    private boolean isAnyWireCrossingAnyNode() {
//        for (WireView wv : wireViews) if (isWireCrossingAnyNode(wv)) return true;
//        return false;
//    }
//
//    private boolean isWireCrossingAnyNode(WireView wv) {
//        Wire wire = wv.getWire();
//        SystemNode src = wire.getFromPort().getParentNode();
//        SystemNode dst = wire.getToPort().getParentNode();
//
//        List<Point> samples = sampleWirePoints(wv, 22);
//        for (SystemNodeView nv : nodeViews) {
//            SystemNode node = nv.getNode();
//            if (node == src || node == dst) continue;
//            Rectangle bounds = nv.getBounds();
//            for (Point s : samples) if (bounds.contains(s)) return true;
//        }
//        return false;
//    }
//
//    private List<Point> sampleWirePoints(WireView wv, int samplesPerSeg) {
//        List<Point> anchors = wv.getScreenControlPoints(this);
//        List<BendSegment> controls = new ArrayList<>(wv.getWire().getBends());
//        controls.sort(Comparator.comparingInt(BendSegment::getSegmentIndex));
//
//        List<Point> out = new ArrayList<>();
//        if (anchors.size() < 2) return out;
//
//        for (int i = 0; i < anchors.size() - 1; i++) {
//            Point p0 = anchors.get(i);
//            Point p2 = anchors.get(i + 1);
//            BendSegment ctrl = null;
//            for (BendSegment b : controls) {
//                if (b.getSegmentIndex() == i) { ctrl = b; break; }
//                if (b.getSegmentIndex() > i) break;
//            }
//            if (ctrl == null) {
//                for (int s = 0; s <= samplesPerSeg; s++) {
//                    double t = (double) s / samplesPerSeg;
//                    int x = (int) Math.round(p0.x + (p2.x - p0.x) * t);
//                    int y = (int) Math.round(p0.y + (p2.y - p0.y) * t);
//                    out.add(new Point(x, y));
//                }
//            } else {
//                Point c = new Point((int) Math.round(ctrl.getBendPoint().getX()),
//                        (int) Math.round(ctrl.getBendPoint().getY()));
//                for (int s = 0; s <= samplesPerSeg; s++) {
//                    double t = (double) s / samplesPerSeg;
//                    double mt = 1.0 - t;
//                    double x = mt*mt*p0.x + 2*mt*t*c.x + t*t*p2.x;
//                    double y = mt*mt*p0.y + 2*mt*t*c.y + t*t*p2.y;
//                    out.add(new Point((int) Math.round(x), (int) Math.round(y)));
//                }
//            }
//        }
//        return out;
//    }
//}
package view;

import config.StageConfig;
import controller.PacketSpawner;
import controller.WireBuilder;
import controller.WireManager;
import model.*;
import util.Constants;
import util.ImageAssets;
import util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import controller.StageManager;
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
    private PacketSpawner spawner;
    private JButton advanceBtn;

    private double zoom = 1.0;
    private int cameraX = 0, cameraY = 0;

    private Timer readinessTimer;

    public GameStageView() {
        setLayout(null);
        setBounds(0, 0, 1200, 800);

        backgroundPanel = new BackgroundGridPanel();
        backgroundPanel.setBounds(250, 50, 1000, 900);


        nodePalettePanel = new NodePalettePanel();
        nodePalettePanel.setBounds(0, 50, 250, 700);
        nodePalettePanel.setOpaque(true);
        add(nodePalettePanel);

        Image bgTip = ImageAssets.get("tip_bar.png");
        tipBarView = new GameTipBarView(bgTip != null ? new ImageIcon(bgTip) : null);
        tipBarView.setBounds(250, 0, 950, 70);
        tipBarView.setOpaque(true);
        add(tipBarView);

        stageLabel = new StageLabel("DAY 1");
        stageLabel.setBounds(0, 0, 300, 50);
        add(stageLabel);

        hudPanel = new HUDPanel();
        hudPanel.setBounds(250, 750, 900, 50);
        hudPanel.setOpaque(true);
        add(hudPanel);


        List<SystemNode> allGivenNodes = new ArrayList<>();
        allGivenNodes.addAll(StageConfig.getStage1InitialNodes());
        allGivenNodes.addAll(StageConfig.getStage1GivenNodes());
        for (SystemNode node : allGivenNodes) nodePalettePanel.addNode(node);

        SwingUtilities.invokeLater(this::wireUpRunButtonIfReferenceExists);

        advanceBtn = new JButton("صعود به مرحله ۲ ▶");
        advanceBtn.setBounds(1010, 10, 170, 30);
        advanceBtn.setFocusable(false);
        advanceBtn.setVisible(false);
        advanceBtn.addActionListener(e -> {

            nodePalettePanel.addStage2Nodes(StageConfig.getStage2NewNodes());

            nodePalettePanel.unlockStage2();

            stageLabel.setText("DAY 2");

            advanceBtn.setVisible(false);
        });
        add(advanceBtn);
        setComponentZOrder(advanceBtn, 0);
    }


    public void setSpawner(PacketSpawner spawner) { this.spawner = spawner; }
    public PacketSpawner getSpawner() { return spawner; }
    public StageLabel getStageLabel() { return stageLabel; }
    public NodePalettePanel getNodePalettePanel() { return nodePalettePanel; }
    public BackgroundGridPanel getBackgroundPanel() { return backgroundPanel; }
    public HUDPanel getHudPanel() { return hudPanel; }
    public GameTipBarView getTipBarView() { return tipBarView; }
    public WireManager getWireManager() { return wireManager; }
    public List<SystemNodeView> getSystemNodeViews() { return nodeViews; }
    public void setWireBuilder(WireBuilder builder) { this.wireBuilder = builder; }

    public void addWire(Wire wire) { wireViews.add(new WireView(wire)); repaint(); }
    public void removeWire(Wire wire) { wireViews.removeIf(v -> v.getWire()==wire); repaint(); }

    public WireView getWireViewFor(Wire wire) {
        for (WireView v : wireViews) if (v.getWire()==wire) return v;
        return null;
    }

    public void addNodeToStage(SystemNodeView view) {
        if (view == null) return;
        view.setBounds(
                (int)view.getNode().getPosition().getX(),
                (int)view.getNode().getPosition().getY(),
                Constants.NODE_WIDTH, Constants.NODE_HEIGHT
        );
        view.setVisible(true);
        add(view);
        controller.StageManager.getInstance().registerNode(view.getNode());


        nodeViews.add(view);
        revalidate(); repaint();

        if (view.getNode().isReference()) wireUpRunButtonIfReferenceExists();
    }

    public SystemNodeView findViewFor(SystemNode node) {
        for (SystemNodeView v : nodeViews) if (v.getNode()==node) return v;
        return null;
    }

    public PortView findPortView(Port port) {
        for (SystemNodeView nv : nodeViews)
            for (PortView pv : nv.getAllPorts())
                if (pv.getPort()==port) return pv;
        return null;
    }


    public Port findPortAt(Point click) {
        for (SystemNodeView nodeView : nodeViews) {
            Point nodeLocation = nodeView.getLocation();
            for (PortView portView : nodeView.getAllPorts()) {
                Rectangle b = portView.getBounds();
                Rectangle global = new Rectangle(
                        b.x + nodeLocation.x, b.y + nodeLocation.y, b.width, b.height
                );
                if (global.contains(click)) return portView.getPort();
            }
        }
        return null;
    }

    public PortView findPortViewAt(Point click) {
        for (SystemNodeView nodeView : nodeViews)
            for (PortView portView : nodeView.getAllPorts())
                if (portView.contains(click)) return portView;
        return null;
    }

    public WireView findWireNear(Point p) {
        for (WireView wv : wireViews)
            if (wv.isNear(p, this)) return wv;
        return null;
    }

    public Vector2D toWorld(Point screen) {
        double worldX = (screen.x - cameraX) / zoom;
        double worldY = (screen.y - cameraY) / zoom;
        return new Vector2D(worldX, worldY);
    }


    public void update(double dt) {
        if (spawner != null) spawner.update(dt);
        for (SystemNodeView nv : nodeViews) {
            SystemNode n = nv.getNode();
            if (n != null) n.update(dt);
        }
        for (WireView wv : wireViews) {
            Wire w = wv.getWire();
            if (w != null) w.update(dt);
        }


        maybeShowAdvanceButton();

    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        Image bg = ImageAssets.get("grid_background.png");
        if (bg != null) g2.drawImage(bg, 250, 50, 900, 700, this);

        for (WireView wv : wireViews) wv.draw(g2, this);
        if (wireBuilder != null) wireBuilder.drawPreview(g2, this);
    }


    private void wireUpRunButtonIfReferenceExists() {
        SystemNodeView refView = null;
        for (SystemNodeView v : nodeViews) {
            if (v.getNode().isReference()) { refView = v; break; }
        }
        if (refView == null) return;

        JButton runBtn = refView.getRunButton();
        if (runBtn == null) return;

        for (ActionListener al : runBtn.getActionListeners()) runBtn.removeActionListener(al);
        runBtn.addActionListener(e -> onRunClicked(runBtn));
        runBtn.setEnabled(true);
    }


private void onRunClicked(JButton runBtn) {
    if (spawner == null) {
        JOptionPane.showMessageDialog(this, "Spawner مقداردهی نشده است.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    SystemNode ref = findReferenceNode();
    if (ref == null) {
        JOptionPane.showMessageDialog(this, "Reference node پیدا نشد.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }


    ref.setOn(true);
    Port firstOut = null;
    for (Port p : ref.getOutputPorts()) {
        if (p != null && p.getConnectedWire() != null) { firstOut = p; break; }
    }
    if (firstOut != null && firstOut.getConnectedWire().getToPort() != null) {
        SystemNode dst = firstOut.getConnectedWire().getToPort().getParentNode();
        if (dst != null) dst.setOn(true);
    }

    // فقط یک پکت سبز  همین الان
    spawner.spawnNow(ref, controller.PacketSpawner.Kind.MSG_SQUARE);

    runBtn.setEnabled(false);
}
    private SystemNode findFirstNodeWithTriangleOutput(){
        for (SystemNodeView v : nodeViews){
            SystemNode n = v.getNode();
            for (Port p : n.getOutputPorts()){
                if (p != null && p.getCompatibleShape() == ShapeType.TRIANGLE && p.getConnectedWire()!=null){
                    return n;
                }
            }
        }
        return null;
    }

    private SystemNode findReferenceNode() {
        for (SystemNodeView v : nodeViews) if (v.getNode().isReference()) return v.getNode();
        return null;
    }



    private boolean isStageReady() {
        for (SystemNodeView nv : nodeViews) if (!nv.getNode().isFullyConnected()) return false;
        return !isAnyWireCrossingAnyNode();
    }

    private boolean isAnyWireCrossingAnyNode() {
        for (WireView wv : wireViews) if (isWireCrossingAnyNode(wv)) return true;
        return false;
    }

    private boolean isWireCrossingAnyNode(WireView wv) {
        Wire w = wv.getWire();
        SystemNode src = w.getFromPort().getParentNode(), dst = w.getToPort().getParentNode();

        List<Point> samples = sampleWirePoints(wv, 22);
        for (SystemNodeView nv : nodeViews) {
            SystemNode n = nv.getNode();
            if (n==src || n==dst) continue;
            Rectangle r = nv.getBounds();
            for (Point s : samples) if (r.contains(s)) return true;
        }
        return false;
    }

    private List<Point> sampleWirePoints(WireView wv, int samplesPerSeg) {
        List<Point> anchors = wv.getScreenControlPoints(this);
        List<BendSegment> ctrls = new ArrayList<>(wv.getWire().getBends());
        ctrls.sort(Comparator.comparingInt(BendSegment::getSegmentIndex));

        List<Point> out = new ArrayList<>();
        if (anchors.size()<2) return out;

        for (int i=0;i<anchors.size()-1;i++){
            Point p0 = anchors.get(i), p2 = anchors.get(i+1);
            BendSegment c = null;
            for (BendSegment b: ctrls){ if (b.getSegmentIndex()==i){ c=b; break; } if (b.getSegmentIndex()>i) break; }

            if (c==null){
                for (int s=0;s<=samplesPerSeg;s++){
                    double t = (double)s/samplesPerSeg;
                    int x = (int)Math.round(p0.x + (p2.x-p0.x)*t);
                    int y = (int)Math.round(p0.y + (p2.y-p0.y)*t);
                    out.add(new Point(x,y));
                }
            } else {
                Point cc = new Point((int)Math.round(c.getBendPoint().getX()),
                        (int)Math.round(c.getBendPoint().getY()));
                for (int s=0;s<=samplesPerSeg;s++){
                    double t=(double)s/samplesPerSeg, mt=1.0-t;
                    double x = mt*mt*p0.x + 2*mt*t*cc.x + t*t*p2.x;
                    double y = mt*mt*p0.y + 2*mt*t*cc.y + t*t*p2.y;
                    out.add(new Point((int)Math.round(x),(int)Math.round(y)));
                }
            }
        }
        return out;
    }


    private boolean isNetworkIdle() {

        if (spawner != null && spawner.activeTasks() > 0) return false;

        for (WireView wv : wireViews) {
            if (!wv.getWire().getPacketsOnWire().isEmpty()) return false;
        }
        return true;
    }

private void maybeShowAdvanceButton() {
    boolean tasks = (spawner != null && spawner.activeTasks() > 0);
    boolean wiresBusy = false;
    for (WireView wv : wireViews) if (!wv.getWire().getPacketsOnWire().isEmpty()) { wiresBusy = true; break; }

    if (!tasks && !wiresBusy && controller.StageManager.getInstance().getCurrentStageNumber() == 1) {
        System.out.println("[ADV] idle => show button");
        advanceBtn.setVisible(true);
    } else {
        System.out.println("[ADV] tasks=" + tasks + " wiresBusy=" + wiresBusy + " stage=" +
                controller.StageManager.getInstance().getCurrentStageNumber());
    }
}


}
