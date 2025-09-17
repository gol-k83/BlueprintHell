

package view;

import model.BendSegment;
import model.Packet;
import model.Wire;
import util.Constants;
import util.ImageAssets;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WireView {
    private final Wire wire;
    private Color color = Color.BLUE;

    public WireView(Wire wire) { this.wire = wire; }

    public Wire getWire() { return wire; }
    public void setColor(Color color) { this.color = color; }
    public Color getColor() { return color; }

    public void draw(Graphics2D g, GameStageView stage) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Point> anchors = buildAnchorsOnScreen(stage);
        if (anchors.size() < 2) return;

        List<BendSegment> controls = new ArrayList<>(wire.getBends());
        controls.sort(Comparator.comparingInt(BendSegment::getSegmentIndex));

        Path2D path = new Path2D.Double();
        path.moveTo(anchors.get(0).x, anchors.get(0).y);
        for (int i = 0; i < anchors.size() - 1; i++) {
            Point p0 = anchors.get(i), p2 = anchors.get(i + 1);
            BendSegment ctrl = getControlOnSegment(controls, i);
            if (ctrl != null) {
                double cx = ctrl.getBendPoint().getX(), cy = ctrl.getBendPoint().getY();
                path.quadTo(cx, cy, p2.x, p2.y);
            } else {
                path.lineTo(p2.x, p2.y);
            }
        }

        g.setColor(color != null ? color : Color.GREEN);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(path);

        // anchors (yellow)
        g.setColor(new Color(255, 200, 0));
        for (int i = 1; i < anchors.size() - 1; i++) {
            Point a = anchors.get(i);
            g.fill(new Ellipse2D.Double(a.x - 2.5, a.y - 2.5, 5, 5));
        }

        // controls (red)
        g.setColor(Color.RED);
        for (int i = 0; i < anchors.size() - 1; i++) {
            BendSegment c = getControlOnSegment(controls, i);
            if (c == null) continue;
            g.fill(new Ellipse2D.Double(c.getBendPoint().getX() - 3, c.getBendPoint().getY() - 3, 6, 6));
        }

        // packets
        drawPackets(g, stage);
    }

    public boolean isNear(Point p, GameStageView stage) {
        final double R = Constants.WIRE_HITBOX_RADIUS;
        List<Point> anchors = buildAnchorsOnScreen(stage);
        if (anchors.size() < 2) return false;

        List<BendSegment> controls = new ArrayList<>(wire.getBends());
        controls.sort(Comparator.comparingInt(BendSegment::getSegmentIndex));

        for (int i = 0; i < anchors.size() - 1; i++) {
            Point a = anchors.get(i), b = anchors.get(i + 1);
            BendSegment ctrl = getControlOnSegment(controls, i);

            double d = (ctrl == null)
                    ? distancePointToSegment(p, a, b)
                    : distancePointToQuadApprox(p, a,
                    new Point((int)Math.round(ctrl.getBendPoint().getX()),
                            (int)Math.round(ctrl.getBendPoint().getY())),
                    b);
            if (d <= R) return true;
        }
        return false;
    }

    public List<Point> getScreenControlPoints(GameStageView stage) { return buildAnchorsOnScreen(stage); }

    // ----------------- Packets Rendering -----------------
    private void drawPackets(Graphics2D g2d, Component comp) {
        List<Packet> packets = fetchPacketsSnapshot();
        if (packets == null || packets.isEmpty()) return;

        List<Point> anchors = buildAnchorsOnScreen((GameStageView) comp);
        if (anchors.size() < 2) return;

        List<BendSegment> controls = new ArrayList<>(wire.getBends());
        controls.sort(Comparator.comparingInt(BendSegment::getSegmentIndex));

        for (Packet p : packets) {
            if (p == null || p.isLost()) continue;

            // distance on this wire (needs Wire.getDistanceOnWire)
            Double s = wire.getDistanceOnWire(p);
            if (s == null) continue;

            Point screenPos = pointOnScreenPathByDistance(anchors, controls, s);
            if (screenPos == null) continue;

            // draw image if available; otherwise a dot
            int size = 36;
            var skin = p.getSkin();
            if (skin != null) {
                size = (int)Math.round(skin.basePx());
                Image img = ImageAssets.getScaledAlias(skin.key(), size, size);
                if (img != null) {
                    g2d.drawImage(img, screenPos.x - size/2, screenPos.y - size/2, size, size, comp);
                    continue;
                }
            }
            int r = Math.max(4, size/10);
            g2d.setColor(new Color(30,144,255));
            g2d.fill(new Ellipse2D.Double(screenPos.x - r, screenPos.y - r, 2*r, 2*r));
        }
    }

    @SuppressWarnings("unchecked")
    private List<Packet> fetchPacketsSnapshot() {
        try {
            Method m = wire.getClass().getMethod("getPacketsOnWire");
            Object res = m.invoke(wire);
            if (res instanceof List<?>) return (List<Packet>) res;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            return null;
        }
        try {
            Method m = wire.getClass().getMethod("getPacketsSnapshot");
            Object res = m.invoke(wire);
            if (res instanceof List<?>) return (List<Packet>) res;
        } catch (Exception ignored) {}
        return null;
    }


    private List<Point> buildAnchorsOnScreen(GameStageView stage) {
        List<Point> out = new ArrayList<>();
        SystemNodeView fromNodeView = stage.findViewFor(wire.getFromPort().getParentNode());
        SystemNodeView toNodeView   = stage.findViewFor(wire.getToPort().getParentNode());
        PortView fromPortView       = stage.findPortView(wire.getFromPort());
        PortView toPortView         = stage.findPortView(wire.getToPort());
        if (fromNodeView==null || toNodeView==null || fromPortView==null || toPortView==null) return out;

        out.add(fromPortView.getScreenPosition(fromNodeView));
        for (var bendAnchor : wire.getBendPoints()) {
            out.add(new Point((int)Math.round(bendAnchor.getX()),
                    (int)Math.round(bendAnchor.getY())));
        }
        out.add(toPortView.getScreenPosition(toNodeView));
        return out;
    }

    private BendSegment getControlOnSegment(List<BendSegment> sorted, int segIndex) {
        for (BendSegment b : sorted) {
            int si = b.getSegmentIndex();
            if (si == segIndex) return b;
            if (si > segIndex) break;
        }
        return null;
    }

    private static double segLenLine(Point a, Point b) { return a.distance(b); }

    private static double segLenQuadApprox(Point p0, Point c, Point p2) {
        final int SAMPLES = 24;
        double len = 0; Point prev = p0;
        for (int i=1;i<=SAMPLES;i++){
            double t = (double)i/SAMPLES;
            Point cur = quadPoint(p0,c,p2,t);
            len += prev.distance(cur);
            prev = cur;
        }
        return len;
    }

    private Point pointOnScreenPathByDistance(List<Point> anchors, List<BendSegment> controls, double s) {
        if (anchors.size()<2) return null;

        for (int i=0;i<anchors.size()-1;i++){
            Point p0 = anchors.get(i), p2 = anchors.get(i+1);
            BendSegment ctrl = getControlOnSegment(controls, i);

            if (ctrl == null){
                double seg = segLenLine(p0,p2);
                if (seg <= 1e-6) continue;
                if (s <= seg){
                    double t = s/seg;
                    int x = (int)Math.round(p0.x + (p2.x-p0.x)*t);
                    int y = (int)Math.round(p0.y + (p2.y-p0.y)*t);
                    return new Point(x,y);
                }
                s -= seg;
            } else {
                Point c = new Point((int)Math.round(ctrl.getBendPoint().getX()),
                        (int)Math.round(ctrl.getBendPoint().getY()));
                double segTotal = segLenQuadApprox(p0,c,p2);
                if (s <= segTotal){
                    final int SAMPLES = 24;
                    double acc=0; Point prev=p0;
                    for (int k=1;k<=SAMPLES;k++){
                        double t=(double)k/SAMPLES;
                        Point cur=quadPoint(p0,c,p2,t);
                        double dl = prev.distance(cur);
                        if (acc+dl >= s){
                            double remain=s-acc, tt=remain/dl;
                            int x=(int)Math.round(prev.x+(cur.x-prev.x)*tt);
                            int y=(int)Math.round(prev.y+(cur.y-prev.y)*tt);
                            return new Point(x,y);
                        }
                        acc+=dl; prev=cur;
                    }
                    return p2;
                } else s -= segTotal;
            }
        }
        return anchors.get(anchors.size()-1);
    }

    private double distancePointToSegment(Point p, Point a, Point b) {
        double px=b.x-a.x, py=b.y-a.y, norm=px*px+py*py;
        if (norm==0) return a.distance(p);
        double u = ((p.x-a.x)*px + (p.y-a.y)*py) / norm;
        u = Math.max(0, Math.min(1,u));
        double x = a.x + u*px, y = a.y + u*py;
        return Point.distance(x,y,p.x,p.y);
    }

    private double distancePointToQuadApprox(Point p, Point p0, Point c, Point p2) {
        final int SAMPLES = 20;
        Point prev=p0; double best=Double.MAX_VALUE;
        for (int s=1;s<=SAMPLES;s++){
            double t=(double)s/SAMPLES;
            Point cur=quadPoint(p0,c,p2,t);
            double d=distancePointToSegment(p,prev,cur);
            if (d<best) best=d;
            prev=cur;
        }
        return best;
    }

    private static Point quadPoint(Point p0, Point c, Point p2, double t) {
        double mt=1.0-t;
        double x = mt*mt*p0.x + 2*mt*t*c.x + t*t*p2.x;
        double y = mt*mt*p0.y + 2*mt*t*c.y + t*t*p2.y;
        return new Point((int)Math.round(x),(int)Math.round(y));
    }
}
