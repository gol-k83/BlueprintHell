//// ✅ نسخه نهایی MouseInteractionController با رفع باگ‌های منطقی و پشتیبانی از خم (Bend Point)
//package controller;
//
//import model.Port;
//import model.Wire;
//import util.Constants;
//import util.Vector2D;
//import view.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseEvent;
//import java.util.ArrayList;
//import java.util.List;
//public class MouseInteractionController {
//    private final WireBuilder wireBuilder;
//    private final GamePanel gamePanel;
//    private final GameStageView gameStageView;
//
//    private int nextDropX = 400;
//    private int nextDropY = 300;
//    private final int DROP_OFFSET = 100;
//
//    private SystemNodeView draggedNode = null;
//    private Point dragOffset = null;
//
//    // 🆕 برای درج خم
//    private Wire targetWireForBend = null;
//    private int bendInsertIndex = -1;
//    private boolean isDraggingBend = false;
//
//    public MouseInteractionController(WireBuilder wireBuilder, GamePanel gamePanel) {
//        this.wireBuilder = wireBuilder;
//        this.gamePanel = gamePanel;
//        this.gameStageView = gamePanel.getGameStageView();
//    }
//
//    public void handleMousePressed(MouseEvent e) {
//        Point click = e.getPoint();
//        System.out.println("🖱️ MousePressed at: " + click);
//        // 🧹 پاک‌سازی متغیرهای خم قبلی
//        targetWireForBend = null;
//        bendInsertIndex = -1;
//        isDraggingBend = false;
//
//        WireView wireView = gameStageView.findWireNear(click);
//        System.out.println("🔍 wireView = " + wireView);
//        if (wireView != null) {
//            Wire wire = wireView.getWire();
//            Vector2D clickPos = new Vector2D(click.x, click.y);
//            int segmentIndex = findClosestSegmentIndex(clickPos, wire);
//            if (segmentIndex >= 0 ) {//&& wire.getRemainingBendSlots() > 0
//                targetWireForBend = wire;
//                bendInsertIndex = segmentIndex;
//                dragOffset = click;
//                System.out.println("✅ Ready to insert bend at segment: " + segmentIndex);
//
//                return; // ❗ مانع بررسی درگ نود می‌شود
//            }
//        }
//
//        // 🎯 شروع سیم‌کشی از پورت خروجی
//        Port clickedPort = gamePanel.findPortAt(click);
//        if (clickedPort != null && clickedPort.getType() == Port.PortType.OUTPUT) {
//            wireBuilder.startDraggingFrom(clickedPort);
//            dragOffset = click;
//            return;
//        }
//
////        if (e.getButton() == MouseEvent.BUTTON3) {
////            Wire targetWire = gamePanel.findWireNear(click);
////          // if(click==null) System.out.println("🎯 کلیک راست روی نقطه: " + click);
////
////            if (targetWire != null) {
////                wireBuilder.getWireManager().removeWire(targetWire);
////                gamePanel.repaint();
////                return;
////            }
////        }
//        if (e.getButton() == MouseEvent.BUTTON3) {
//
//
//
//            System.out.println("🎯 کلیک راست روی نقطه: " + click);
//            Wire targetWire = gamePanel.findWireNear(click);
//
//            if (targetWire != null) {
//
//                wireBuilder.getWireManager().removeWire(targetWire);//مدل
//                gameStageView.removeWire(targetWire);//نما
//                gamePanel.repaint();
//                System.out.println("🧨 وایر پیدا شد برای حذف: " + targetWire);
//
//                return;
//
//            } else {
//                System.out.println("⚠️ هیچ سیمی نزدیک نبود برای حذف.");
//            }
//            return;
//        }
//
//        // کلیک روی پنل نودها
//        NodePalettePanel palette = gameStageView.getNodePalettePanel();
//        if (palette.getBounds().contains(click)) {
//            Point localClick = SwingUtilities.convertPoint(gamePanel, click, palette);
//            for (SystemNodeView view : palette.getAvailableNodes()) {
//                Rectangle bounds = new Rectangle(50, (int) view.getNode().getPosition().getY(),
//                        Constants.NODE_WIDTH, Constants.NODE_HEIGHT);
//                if (bounds.contains(localClick)) {
//                    palette.removeNode(view);
//                    gameStageView.addNodeToStage(view);
//                    view.setBounds(nextDropX, nextDropY, Constants.NODE_WIDTH, Constants.NODE_HEIGHT);
//                    view.getNode().setPosition(new Vector2D(nextDropX, nextDropY));
//                    draggedNode = view;
//                    dragOffset = new Point(click.x - nextDropX, click.y - nextDropY);
//                    nextDropX += DROP_OFFSET;
//                    nextDropY += DROP_OFFSET;
//                    gamePanel.repaint();
//                    return;
//                }
//            }
//        }
//
//        cancelWireDrawing();
//        for (SystemNodeView nodeView : gamePanel.getSystemNodeViews()) {
//            Rectangle bounds = nodeView.getBounds();
//            if (bounds.contains(click)) {
//                draggedNode = nodeView;
//                dragOffset = new Point(click.x - bounds.x, click.y - bounds.y);
//                gamePanel.getSystemNodeViews().forEach(n -> n.setSelected(false));
//                nodeView.setSelected(true);
//                gamePanel.repaint();
//                return;
//            }
//        }
//    }
//
//    public void handleMouseDragged(MouseEvent e) {
//        if (targetWireForBend != null && bendInsertIndex >= 0) {
//            isDraggingBend = true;
//            gamePanel.repaint();
//            return;
//        }
//
//        if (draggedNode != null && dragOffset != null) {
//            int newX = e.getX() - dragOffset.x;
//            int newY = e.getY() - dragOffset.y;
//            draggedNode.setLocation(newX, newY);
//            draggedNode.getNode().setPosition(new Vector2D(newX, newY));
//            draggedNode.moveTo(newX, newY);
//            gamePanel.repaint();
//        }
//
//        if (wireBuilder.isActive()) {
//            wireBuilder.onMouseMove(new Vector2D(e.getX(), e.getY()));
//            gamePanel.repaint();
//        }
//    }
//
//    public void handleMouseReleased(MouseEvent e) {
//
//       if(draggedNode!=null) {
//           gameStageView.addNodeToStage(draggedNode);}
//        draggedNode = null;
//        dragOffset = null;
//
//        if (wireBuilder.isActive()) {
//            Port target = gamePanel.findPortAt(e.getPoint());
//            if (target != null) {
//                wireBuilder.finishDraggingTo(target);
//            } else {
//                wireBuilder.cancel();
//            }
//            gamePanel.repaint();
//        }
//
//        if (isDraggingBend && targetWireForBend != null && bendInsertIndex >= 0) {
//            Vector2D releasePos = new Vector2D(e.getX(), e.getY());
//            System.out.println("🔴 خم در حال درج در: " + releasePos);
////            targetWireForBend.insertBendPointAt(releasePos, bendInsertIndex + 1);
//            targetWireForBend.insertBendAt(releasePos, bendInsertIndex);
//
//            System.out.println("🔴 خم اضافه شد در سگمنت: " + bendInsertIndex);
//            gamePanel.repaint();
//        }
//
//        targetWireForBend = null;
//        bendInsertIndex = -1;
//        isDraggingBend = false;
//    }
//
//    public void handleMouseMoved(MouseEvent e) {
//        if (wireBuilder.isActive()) {
//            wireBuilder.onMouseMove(new Vector2D(e.getX(), e.getY()));
//            gamePanel.repaint();
//        }
//    }
//
//    public void cancelWireDrawing() {
//        if (wireBuilder.isActive()) {
//            wireBuilder.cancel();
//            gamePanel.repaint();
//        }
//    }
//
//    public WireBuilder getWireBuilder() {
//        return wireBuilder;
//    }
//
//    private int findClosestSegmentIndex(Vector2D clickPos, Wire wire) {
//        double minDist = Double.MAX_VALUE;
//        int closestSegment = -1;
//        List<Point> screenPoints = getScreenControlPointsForWire(wire);
//
//        for (int i = 0; i < screenPoints.size() - 1; i++) {
//            double dist = distancePointToSegment(clickPos.toPoint(),screenPoints .get(i), screenPoints .get(i + 1));
//            if (dist < Constants.WIRE_HITBOX_RADIUS && dist < minDist) {
//                minDist = dist;
//                closestSegment = i;
//            }
//        }
//        return closestSegment;
//    }
//
//    private List<Point> getScreenControlPointsForWire(Wire wire) {
//        WireView view = gameStageView.getWireViewFor(wire);
//        if (view != null) {
//            return view.getScreenControlPoints(gameStageView);
//        }
//        return new ArrayList<>();
//    }
//
//    private double distancePointToSegment(Point p, Point a, Point b) {
//        double px = b.x - a.x;
//        double py = b.y - a.y;
//        double norm = px * px + py * py;
//        if (norm == 0) return a.distance(p);
//        double u = ((p.x - a.x) * px + (p.y - a.y) * py) / norm;
//        u = Math.max(0, Math.min(1, u));
//        double x = a.x + u * px;
//        double y = a.y + u * py;
//        return Point.distance(x, y, p.x, p.y);
//    }
//}
//package controller;
//
//import model.BendSegment;
//import model.Port;
//import model.Wire;
//import util.Constants;
//import util.Vector2D;
//import view.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseEvent;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MouseInteractionController {
//    private final WireBuilder wireBuilder;
//    private final GamePanel gamePanel;
//    private final GameStageView gameStageView;
//
//    private int nextDropX = 400;
//    private int nextDropY = 300;
//    private final int DROP_OFFSET = 100;
//
//    private SystemNodeView draggedNode = null;
//    private Point dragOffset = null;
//
//    // --- درگ خم ---
//    private Wire  draggingWire = null;
//    private int   draggingSegIndex = -1;           // کدام سگمنت؟
//    private boolean isDraggingNewBend = false;     // درج خم جدید
//    private boolean isDraggingExistingBend = false;// جابجایی خم موجود
//
//    // شعاع مجاز فاصله کنترل از پای عمود روی همان سگمنت
//    private static final double BEND_MAX_RADIUS = Constants.WIRE_HITBOX_RADIUS * 3.0;
//
//    public MouseInteractionController(WireBuilder wireBuilder, GamePanel gamePanel) {
//        this.wireBuilder = wireBuilder;
//        this.gamePanel = gamePanel;
//        this.gameStageView = gamePanel.getGameStageView();
//    }
//
//    public void handleMousePressed(MouseEvent e) {
//        Point click = e.getPoint();
//
//        // ریست حالت‌های قبلی
//        draggingWire = null;
//        draggingSegIndex = -1;
//        isDraggingNewBend = false;
//        isDraggingExistingBend = false;
//
//        // حذف با راست‌کلیک: حذف کل وایر
//        if (e.getButton() == MouseEvent.BUTTON3) {
//            WireView wv = gameStageView.findWireNear(click);
//            Wire targetWire = (wv != null) ? wv.getWire() : null;
//            if (targetWire != null) {
//                wireBuilder.getWireManager().removeWire(targetWire);
//                gameStageView.removeWire(targetWire);
//                gamePanel.repaint();
//                return;
//            }
//        }
//
//        // آیا روی وایر کلیک شده؟ (جابجایی کنترل موجود یا درج خم جدید)
//        WireView wireView = gameStageView.findWireNear(click);
//        if (wireView != null) {
//            Wire w = wireView.getWire();
//
//            // 1) اگر روی کنترل موجود کلیک شد → جابجایی کنترل
//            int segHit = findBendSegmentHit(w, click, Constants.WIRE_HITBOX_RADIUS);
//            if (segHit != -1) {
//                draggingWire = w;
//                draggingSegIndex = segHit;
//                isDraggingExistingBend = true;
//                return;
//            }
//
//            // 2) درج خم جدید روی نزدیک‌ترین سگمنت
//            int segIdx = findClosestSegmentIndex(new Vector2D(click.x, click.y), w);
//            if (segIdx >= 0) {
//                List<Point> anchors = getScreenControlPointsForWire(w); // start → anchor → end
//                Point a = anchors.get(segIdx);
//                Point b = anchors.get(segIdx + 1);
//
//                // Anchor (پای عمود) + Control (clamp شده)
//                Point foot = projectOnSegment(click, a, b);
//                Vector2D ctrl = clampToRadius(click, foot, BEND_MAX_RADIUS);
//
//                // 1) درج Anchor روی مسیر
//                w.insertAnchorAtSegment(segIdx, new Vector2D(foot.x, foot.y));
//                // 2) ست کنترل برای همین سگمنت
//                w.setControlForSegment(segIdx, ctrl);
//
//                draggingWire = w;
//                draggingSegIndex = segIdx;
//                isDraggingNewBend = true;
//
//                gamePanel.repaint();
//                return;
//            }
//        }
//
//        // شروع سیم‌کشی از پورت خروجی
//        Port clickedPort = gamePanel.findPortAt(click);
//        if (clickedPort != null && clickedPort.getType() == Port.PortType.OUTPUT) {
//            wireBuilder.startDraggingFrom(clickedPort);
//            dragOffset = click;
//            return;
//        }
//
//        // درگ نود از پالت
//        NodePalettePanel palette = gameStageView.getNodePalettePanel();
//        if (palette.getBounds().contains(click)) {
//            Point localClick = SwingUtilities.convertPoint(gamePanel, click, palette);
//            for (SystemNodeView view : palette.getAvailableNodes()) {
//                Rectangle bounds = new Rectangle(50, (int) view.getNode().getPosition().getY(),
//                        Constants.NODE_WIDTH, Constants.NODE_HEIGHT);
//                if (bounds.contains(localClick)) {
//                    palette.removeNode(view);
//                    gameStageView.addNodeToStage(view);
//                    view.setBounds(nextDropX, nextDropY, Constants.NODE_WIDTH, Constants.NODE_HEIGHT);
//                    view.getNode().setPosition(new Vector2D(nextDropX, nextDropY));
//                    draggedNode = view;
//                    dragOffset = new Point(click.x - nextDropX, click.y - nextDropY);
//                    nextDropX += DROP_OFFSET;
//                    nextDropY += DROP_OFFSET;
//                    gamePanel.repaint();
//                    return;
//                }
//            }
//        }
//
//        cancelWireDrawing();
//
//        // انتخاب/درگ نود روی استیج
//        for (SystemNodeView nodeView : gamePanel.getSystemNodeViews()) {
//            Rectangle bounds = nodeView.getBounds();
//            if (bounds.contains(click)) {
//                draggedNode = nodeView;
//                dragOffset = new Point(click.x - bounds.x, click.y - bounds.y);
//                gamePanel.getSystemNodeViews().forEach(n -> n.setSelected(false));
//                nodeView.setSelected(true);
//                gamePanel.repaint();
//                return;
//            }
//        }
//    }
//
//    public void handleMouseDragged(MouseEvent e) {
//        // درگ کنترل (برای درج جدید یا جابجایی موجود)
//        if ((isDraggingNewBend || isDraggingExistingBend) && draggingWire != null && draggingSegIndex >= 0) {
//            List<Point> anchors = getScreenControlPointsForWire(draggingWire);
//            if (anchors.size() >= draggingSegIndex + 2) {
//                Point a = anchors.get(draggingSegIndex);
//                Point b = anchors.get(draggingSegIndex + 1);
//
//                Point foot = projectOnSegment(e.getPoint(), a, b);
//                Vector2D pos = clampToRadius(e.getPoint(), foot, BEND_MAX_RADIUS);
//
//                draggingWire.updateBendOnSegment(draggingSegIndex, pos); // فقط کنترل آپدیت می‌شود
//                gamePanel.repaint();
//                return;
//            }
//        }
//
//        // درگ نود
//        if (draggedNode != null && dragOffset != null) {
//            int newX = e.getX() - dragOffset.x;
//            int newY = e.getY() - dragOffset.y;
//            draggedNode.setLocation(newX, newY);
//            draggedNode.getNode().setPosition(new Vector2D(newX, newY));
//            draggedNode.moveTo(newX, newY);
//            gamePanel.repaint();
//        }
//
//        // پیش‌نمایش سیم
//        if (wireBuilder.isActive()) {
//            wireBuilder.onMouseMove(new Vector2D(e.getX(), e.getY()));
//            gamePanel.repaint();
//        }
//    }
//
//    public void handleMouseReleased(MouseEvent e) {
//        // تثبیت نود در صورت درگ از پالت
//        if (draggedNode != null) {
//            gameStageView.addNodeToStage(draggedNode);
//        }
//        draggedNode = null;
//        dragOffset = null;
//
//        // اتمام سیم‌کشی
//        if (wireBuilder.isActive()) {
//            Port target = gamePanel.findPortAt(e.getPoint());
//            if (target != null) wireBuilder.finishDraggingTo(target);
//            else wireBuilder.cancel();
//            gamePanel.repaint();
//        }
//
//        // پایان حالات درگ کنترل
//        isDraggingNewBend = false;
//        isDraggingExistingBend = false;
//        draggingSegIndex = -1;
//        draggingWire = null;
//    }
//
//    public void handleMouseMoved(MouseEvent e) {
//        if (wireBuilder.isActive()) {
//            wireBuilder.onMouseMove(new Vector2D(e.getX(), e.getY()));
//            gamePanel.repaint();
//        }
//    }
//
//    public void cancelWireDrawing() {
//        if (wireBuilder.isActive()) {
//            wireBuilder.cancel();
//            gamePanel.repaint();
//        }
//    }
//
//    public WireBuilder getWireBuilder() {
//        return wireBuilder;
//    }
//
//    // ---------------------- Helpers ----------------------
//
//    /** نزدیک‌ترین سگمنت به کلیک (برای سگمنت‌های منحنی، فاصله تا خود منحنی سنجیده می‌شود). */
//    private int findClosestSegmentIndex(Vector2D clickPos, Wire wire) {
//        List<Point> anchors = getScreenControlPointsForWire(wire);
//        if (anchors.size() < 2) return -1;
//
//        List<BendSegment> controls = new ArrayList<>(wire.getBends());
//        controls.sort(java.util.Comparator.comparingInt(BendSegment::getSegmentIndex));
//
//        Point p = clickPos.toPoint();
//        double minDist = Double.MAX_VALUE;
//        int closestSegment = -1;
//
//        for (int i = 0; i < anchors.size() - 1; i++) {
//            Point a = anchors.get(i);
//            Point b = anchors.get(i + 1);
//
//            BendSegment ctrl = null;
//            for (BendSegment bs : controls) {
//                if (bs.getSegmentIndex() == i) { ctrl = bs; break; }
//                if (bs.getSegmentIndex() > i)  break;
//            }
//
//            double dist;
//            if (ctrl == null) {
//                dist = distancePointToSegment(p, a, b);
//            } else {
//                Point c = new Point(
//                        (int) Math.round(ctrl.getBendPoint().getX()),
//                        (int) Math.round(ctrl.getBendPoint().getY())
//                );
//                dist = distancePointToQuadApprox(p, a, c, b);
//            }
//
//            if (dist < Constants.WIRE_HITBOX_RADIUS && dist < minDist) {
//                minDist = dist;
//                closestSegment = i;
//            }
//        }
//        return closestSegment;
//    }
//
//    /** اگر روی یک کنترل کلیک شود، ایندکس سگمنت آن را می‌دهد؛ وگرنه -1 */
//    private int findBendSegmentHit(Wire wire, Point p, double r) {
//        for (BendSegment b : wire.getBends()) {
//            Vector2D bp = b.getBendPoint();
//            if (bp != null && bp.toPoint().distance(p) <= r) {
//                return b.getSegmentIndex();
//            }
//        }
//        return -1;
//    }
//
//    /** نقاط کنترل روی اسکرین از ویوِ همان وایر (start → anchors → end) */
//    private List<Point> getScreenControlPointsForWire(Wire wire) {
//        WireView view = gameStageView.getWireViewFor(wire);
//        if (view != null) return view.getScreenControlPoints(gameStageView);
//        return new ArrayList<>();
//    }
//
//    /** فاصله نقطه تا پاره‌خط AB */
//    private double distancePointToSegment(Point p, Point a, Point b) {
//        double px = b.x - a.x;
//        double py = b.y - a.y;
//        double norm = px * px + py * py;
//        if (norm == 0) return a.distance(p);
//        double u = ((p.x - a.x) * px + (p.y - a.y) * py) / norm;
//        u = Math.max(0, Math.min(1, u));
//        double x = a.x + u * px;
//        double y = a.y + u * py;
//        return Point.distance(x, y, p.x, p.y);
//    }
//
//    /** فاصله تقریبی نقطه تا منحنی درجه ۲ با نمونه‌برداری */
//    private double distancePointToQuadApprox(Point p, Point p0, Point c, Point p2) {
//        final int SAMPLES = 20;
//        Point prev = p0;
//        double best = Double.MAX_VALUE;
//        for (int s = 1; s <= SAMPLES; s++) {
//            double t = (double) s / SAMPLES;
//            Point cur = quadPoint(p0, c, p2, t);
//            double d = distancePointToSegment(p, prev, cur);
//            if (d < best) best = d;
//            prev = cur;
//        }
//        return best;
//    }
//
//    /** B(t) = (1-t)^2 P0 + 2(1-t)t C + t^2 P2 */
//    private Point quadPoint(Point p0, Point c, Point p2, double t) {
//        double mt = 1.0 - t;
//        double x = mt * mt * p0.x + 2 * mt * t * c.x + t * t * p2.x;
//        double y = mt * mt * p0.y + 2 * mt * t * c.y + t * t * p2.y;
//        return new Point((int) Math.round(x), (int) Math.round(y));
//    }
//
//    /** پای عمود نقطه p روی سگمنت AB */
//    private Point projectOnSegment(Point p, Point a, Point b) {
//        double vx = b.x - a.x, vy = b.y - a.y;
//        double wx = p.x - a.x, wy = p.y - a.y;
//        double c2 = vx * vx + vy * vy;
//        if (c2 == 0) return new Point(a);
//        double t = Math.max(0, Math.min(1, (vx * wx + vy * wy) / c2));
//        return new Point((int) Math.round(a.x + t * vx), (int) Math.round(a.y + t * vy));
//    }
//
//    /** محدود کردن کنترل به شعاع R نسبت به پای عمود روی همان سگمنت */
//    private Vector2D clampToRadius(Point mouse, Point foot, double R) {
//        double dx = mouse.x - foot.x, dy = mouse.y - foot.y;
//        double d = Math.hypot(dx, dy);
//        if (d <= R || d == 0) return new Vector2D(mouse.x, mouse.y);
//        double k = R / d;
//        return new Vector2D(foot.x + dx * k, foot.y + dy * k);
//    }
//}
package controller;

import model.BendSegment;
import model.Port;
import model.Wire;
import util.Constants;
import util.Vector2D;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MouseInteractionController {
    private final WireBuilder wireBuilder;
    private final GamePanel gamePanel;
    private final GameStageView gameStageView;

    private int nextDropX = 400;
    private int nextDropY = 300;

    private SystemNodeView draggedNode = null;
    private Point dragOffset = null;

    // --- درگ خم ---
    private Wire  draggingWire = null;
    private int   draggingSegIndex = -1;
    private boolean isDraggingNewBend = false;      // خم جدید
    private boolean isDraggingExistingBend = false; // جابجایی کوکب

    public MouseInteractionController(WireBuilder wireBuilder, GamePanel gamePanel) {
        this.wireBuilder = wireBuilder;
        this.gamePanel = gamePanel;
        this.gameStageView = gamePanel.getGameStageView();
    }

    // R = min(MAX, SCALE * segmentLength)
    private double computeBendRadius(Point a, Point b) {
        double segLen = a.distance(b);
        double scaled = segLen * Constants.WIRE_BEND_SCALE;
        return Math.min(Constants.WIRE_BEND_MAX_RADIUS, scaled);
    }

    private boolean isInsideAnyNode(Point p) {
        for (SystemNodeView nv : gamePanel.getSystemNodeViews()) {
            if (nv.getBounds().contains(p)) return true;
        }
        return false;
    }


    private int findBendSegmentHitAdaptive(Wire wire, Point p, GameStageView stage) {
        // برای هر کنترل، شعاع هیت‌تست را بر اساس طول سگمنت تنظیم کنی
        List<Point> anchors = getScreenControlPointsForWire(wire); // start,[anchors],end
        for (BendSegment b : wire.getBends()) {
            int seg = b.getSegmentIndex();
            if (seg < 0 || seg + 1 >= anchors.size()) continue;

            Point a = anchors.get(seg);
            Point c = anchors.get(seg + 1);

            double R = Math.min(Constants.WIRE_BEND_MAX_RADIUS, a.distance(c) * 0.85);
            R = Math.max(Constants.WIRE_HANDLE_HITBOX_RADIUS, R); // حداقلِ قبلی

            if (b.getBendPoint().toPoint().distance(p) <= R) {
                return seg;
            }
        }
        return -1;
    }



    private int findAnchorHit(Wire wire, Point p, GameStageView stage, double r) {
        List<Point> anchors = getScreenControlPointsForWire(wire); // start,[anchors],end
        for (int i = 1; i < anchors.size() - 1; i++) {
            if (anchors.get(i).distance(p) <= r) return i - 1;
        }
        return -1;
    }

    public void handleMousePressed(MouseEvent e) {
        Point click = e.getPoint();


        draggingWire = null;
        draggingSegIndex = -1;
        isDraggingNewBend = false;
        isDraggingExistingBend = false;

        if (e.getButton() == MouseEvent.BUTTON3) {


            if (isInsideAnyNode(click) || gamePanel.findPortAt(click) != null) return;

            WireView wv = gameStageView.findWireNear(click);
            if (wv != null) {
                Wire w = wv.getWire();


//                int segCtrl = findBendSegmentHit(w, click, Constants.WIRE_HANDLE_HITBOX_RADIUS);
                int segCtrl = findBendSegmentHitAdaptive(w, click, gameStageView);

                if (segCtrl != -1) {
                    w.removeBendCompletelyAtSegment(segCtrl);
                    gamePanel.repaint();
                    return;
                }

                // 2حذف Anchor
                int segAnchor = findAnchorHit(w, click, gameStageView, Constants.WIRE_HANDLE_HITBOX_RADIUS);
                if (segAnchor != -1) {
                    w.removeBendCompletelyAtSegment(segAnchor);
                    gamePanel.repaint();
                    return;
                }

                //اگر نه کنترل بود نه Anchor حذف کل سیم بعدا چک کن کوکب
                wireBuilder.getWireManager().removeWire(w);
                gameStageView.removeWire(w);
                gamePanel.repaint();
                return;
            }
            return;
        }


        Port clickedPort = gamePanel.findPortAt(click);
        if (clickedPort != null && clickedPort.getType() == Port.PortType.OUTPUT) {
            wireBuilder.startDraggingFrom(clickedPort);
            dragOffset = click;
            return;
        }


        for (SystemNodeView nodeView : gamePanel.getSystemNodeViews()) {
            if (nodeView.getBounds().contains(click)) {
                draggedNode = nodeView;
                Rectangle b = nodeView.getBounds();
                dragOffset = new Point(click.x - b.x, click.y - b.y);
                gamePanel.getSystemNodeViews().forEach(n -> n.setSelected(false));
                nodeView.setSelected(true);
                gamePanel.repaint();
                return;
            }
        }


        WireView wireView = gameStageView.findWireNear(click);
        if (wireView != null) {
            Wire w = wireView.getWire();


            int segHit =findBendSegmentHitAdaptive(w, click, gameStageView);
            if (segHit != -1) {
                draggingWire = w;
                draggingSegIndex = segHit;
                isDraggingExistingBend = true;
                return;
            }


            int segIdx = findClosestSegmentIndex(new Vector2D(click.x, click.y), w);
            if (segIdx >= 0) {
                List<Point> anchors = getScreenControlPointsForWire(w);
                Point a = anchors.get(segIdx), b = anchors.get(segIdx + 1);

                Point foot = projectOnSegment(click, a, b);
                double R = computeBendRadius(a, b);
                Vector2D ctrl = clampToRadius(click, foot, R);

                w.insertAnchorAtSegment(segIdx, new Vector2D(foot.x, foot.y));
                w.setControlForSegment(segIdx, ctrl);

                draggingWire = w;
                draggingSegIndex = segIdx;
                isDraggingNewBend = true;

                gamePanel.repaint();
                return;
            }
        }


        NodePalettePanel palette = gameStageView.getNodePalettePanel();
        if (palette.getBounds().contains(click)) {
            Point localClick = SwingUtilities.convertPoint(gamePanel, click, palette);
            for (SystemNodeView view : palette.getAvailableNodes()) {
                Rectangle bounds = new Rectangle(50, (int) view.getNode().getPosition().getY(),
                        Constants.NODE_WIDTH, Constants.NODE_HEIGHT);
                if (bounds.contains(localClick)) {
                    palette.removeNode(view);
                    gameStageView.addNodeToStage(view);
                    view.setBounds(nextDropX, nextDropY, Constants.NODE_WIDTH, Constants.NODE_HEIGHT);
                    view.getNode().setPosition(new Vector2D(nextDropX, nextDropY));
                    draggedNode = view;
                    dragOffset = new Point(click.x - nextDropX, click.y - nextDropY);
                    nextDropX += 100; nextDropY += 100;
                    gamePanel.repaint();
                    return;
                }
            }
        }

        cancelWireDrawing();
    }

    public void handleMouseDragged(MouseEvent e) {

        if ((isDraggingNewBend || isDraggingExistingBend) && draggingWire != null && draggingSegIndex >= 0) {
            List<Point> anchors = getScreenControlPointsForWire(draggingWire);
            if (anchors.size() >= draggingSegIndex + 2) {
                Point a = anchors.get(draggingSegIndex);
                Point b = anchors.get(draggingSegIndex + 1);

                Point foot = projectOnSegment(e.getPoint(), a, b);
                double R = computeBendRadius(a, b);
                Vector2D pos = clampToRadius(e.getPoint(), foot, R);

                draggingWire.updateBendOnSegment(draggingSegIndex, pos); // فقط کنترل آپدیت می‌شود
                gamePanel.repaint();
                return;
            }
        }


        if (draggedNode != null && dragOffset != null) {
            int newX = e.getX() - dragOffset.x;
            int newY = e.getY() - dragOffset.y;
            draggedNode.setLocation(newX, newY);
            draggedNode.getNode().setPosition(new Vector2D(newX, newY));
            draggedNode.moveTo(newX, newY);
            gamePanel.repaint();
        }


        if (wireBuilder.isActive()) {
            wireBuilder.onMouseMove(new Vector2D(e.getX(), e.getY()));
            gamePanel.repaint();
        }
    }

    public void handleMouseReleased(MouseEvent e) {

        if (draggedNode != null) {
            gameStageView.addNodeToStage(draggedNode);
        }
        draggedNode = null;
        dragOffset = null;


        if (wireBuilder.isActive()) {
            Port target = gamePanel.findPortAt(e.getPoint());
            if (target != null) wireBuilder.finishDraggingTo(target);
            else wireBuilder.cancel();
            gamePanel.repaint();
        }


        isDraggingNewBend = false;
        isDraggingExistingBend = false;
        draggingSegIndex = -1;
        draggingWire = null;
    }

    public void handleMouseMoved(MouseEvent e) {
        if (wireBuilder.isActive()) {
            wireBuilder.onMouseMove(new Vector2D(e.getX(), e.getY()));
            gamePanel.repaint();
        }
    }

    public void cancelWireDrawing() {
        if (wireBuilder.isActive()) {
            wireBuilder.cancel();
            gamePanel.repaint();
        }
    }

    public WireBuilder getWireBuilder() {
        return wireBuilder;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////

    /*  رو هم بدست میاد نزدیک‌ترین سگمنت به کلیک (برای سگمنت‌های منحنی، فاصله تا خود منحنی  */
    private int findClosestSegmentIndex(Vector2D clickPos, Wire wire) {
        List<Point> anchors = getScreenControlPointsForWire(wire);
        if (anchors.size() < 2) return -1;

        List<BendSegment> controls = new ArrayList<>(wire.getBends());
        controls.sort(java.util.Comparator.comparingInt(BendSegment::getSegmentIndex));

        Point p = clickPos.toPoint();
        double minDist = Double.MAX_VALUE;
        int closestSegment = -1;

        for (int i = 0; i < anchors.size() - 1; i++) {
            Point a = anchors.get(i);
            Point b = anchors.get(i + 1);

            BendSegment ctrl = null;
            for (BendSegment bs : controls) {
                if (bs.getSegmentIndex() == i) { ctrl = bs; break; }
                if (bs.getSegmentIndex() > i)  break;
            }

            double dist;
            if (ctrl == null) {
                dist = distancePointToSegment(p, a, b);
            } else {
                Point c = new Point(
                        (int) Math.round(ctrl.getBendPoint().getX()),
                        (int) Math.round(ctrl.getBendPoint().getY())
                );
                dist = distancePointToQuadApprox(p, a, c, b);
            }

            if (dist < Constants.WIRE_HITBOX_RADIUS && dist < minDist) {
                minDist = dist;
                closestSegment = i;
            }
        }
        return closestSegment;
    }


    private List<Point> getScreenControlPointsForWire(Wire wire) {
        WireView view = gameStageView.getWireViewFor(wire);
        if (view != null) return view.getScreenControlPoints(gameStageView);
        return new ArrayList<>();
    }


    private double distancePointToSegment(Point p, Point a, Point b) {
        double px = b.x - a.x;
        double py = b.y - a.y;
        double norm = px * px + py * py;
        if (norm == 0) return a.distance(p);
        double u = ((p.x - a.x) * px + (p.y - a.y) * py) / norm;
        u = Math.max(0, Math.min(1, u));
        double x = a.x + u * px;
        double y = a.y + u * py;
        return Point.distance(x, y, p.x, p.y);
    }


    private double distancePointToQuadApprox(Point p, Point p0, Point c, Point p2) {
        final int SAMPLES = 20;
        Point prev = p0;
        double best = Double.MAX_VALUE;
        for (int s = 1; s <= SAMPLES; s++) {
            double t = (double) s / SAMPLES;
            Point cur = quadPoint(p0, c, p2, t);
            double d = distancePointToSegment(p, prev, cur);
            if (d < best) best = d;
            prev = cur;
        }
        return best;
    }

    /** B(t) = (1-t)^2 P0 + 2(1-t)t C + t^2 P2 *///فرمول
    private Point quadPoint(Point p0, Point c, Point p2, double t) {
        double mt = 1.0 - t;
        double x = mt * mt * p0.x + 2 * mt * t * c.x + t * t * p2.x;
        double y = mt * mt * p0.y + 2 * mt * t * c.y + t * t * p2.y;
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    // پای عمود نقطه p روی سگمنت
    private Point projectOnSegment(Point p, Point a, Point b) {
        double vx = b.x - a.x, vy = b.y - a.y;
        double wx = p.x - a.x, wy = p.y - a.y;
        double c2 = vx * vx + vy * vy;
        if (c2 == 0) return new Point(a);
        double t = Math.max(0, Math.min(1, (vx * wx + vy * wy) / c2));
        return new Point((int) Math.round(a.x + t * vx), (int) Math.round(a.y + t * vy));
    }

   // محدود کردن کنترل به شعاع R نسبت به پای عمود روی همان سگمنت
    private Vector2D clampToRadius(Point mouse, Point foot, double R) {
        double dx = mouse.x - foot.x, dy = mouse.y - foot.y;
        double d = Math.hypot(dx, dy);
        if (d <= R || d == 0) return new Vector2D(mouse.x, mouse.y);
        double k = R / d;
        return new Vector2D(foot.x + dx * k, foot.y + dy * k);
    }
}
