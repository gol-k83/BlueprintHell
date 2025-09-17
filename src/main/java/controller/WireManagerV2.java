//package controller;
//
//import model.BendSegment;
//import model.Wire;
//import model.Port;
//import util.Vector2D;
//import java.util.List;
//
//public class WireManagerV2 {
//    private final List<Wire> wires;
//
//    public WireManagerV2(List<Wire> wires) {
//        this.wires = wires;
//    }
//
//    // تبدیل مختصات screen به world
//    public Vector2D toWorld(Vector2D screenPosition, double zoom, int cameraX, int cameraY) {
//        double worldX = (screenPosition.getX() - cameraX) / zoom;
//        double worldY = (screenPosition.getY() - cameraY) / zoom;
//        return new Vector2D(worldX, worldY);
//    }
//
//    // محاسبه طول سیم‌ها (با bend‌ها)
//    public double calculateWireLength(Wire wire) {
//        List<Vector2D> points = wire.getAllControlPoints();  // دریافت تمام نقاط کنترل سیم
//        double totalLength = 0;
//
//        // برای هر دو نقطه کنترل، طول را محاسبه می‌کنیم
//        for (int i = 0; i < points.size() - 1; i++) {
//            totalLength += points.get(i).distanceTo(points.get(i + 1));  // فاصله بین دو نقطه
//        }
//
//        // اگر خم‌هایی در سیم وجود دارد، طول خم‌ها را نیز محاسبه می‌کنیم
//        List<BendSegment> bends = wire.getBends();
//        for (BendSegment bend : bends) {
//            // برای هر خم، طولش را محاسبه می‌کنیم
//            totalLength += calculateBendLength(bend);
//        }
//
//        return totalLength;
//    }
//
//    // محاسبه طول خم‌ها
//    private double calculateBendLength(BendSegment bend) {
//        // استفاده از نمونه‌برداری یا روش‌های عددی برای محاسبه طول خم
//        // به‌طور ساده می‌توانید از فاصله نقاط کنترلی برای یک خم استفاده کنید
//        return bend.getBendPoint().distanceTo(bend.getFromPortPosition());
//    }
//
//    // محاسبه طول کل سیم‌های موجود
//    public double getTotalWireLength() {
//        double total = 0;
//        for (Wire wire : wires) {
//            total += calculateWireLength(wire);
//        }
//        return total;
//    }
//
//    // افزودن سیم جدید به لیست
//    public void addWire(Wire newWire) {
//        wires.add(newWire);
//    }
//
//    // حذف سیم از لیست
//    public void removeWire(Wire wire) {
//        wires.remove(wire);
//    }
//}
package controller;

import model.Wire;
import model.Port;
import util.Vector2D;
import java.util.List;
import model.BendSegment;
public class WireManagerV2 {
    private final List<Wire> wires;

    public WireManagerV2(List<Wire> wires) {
        this.wires = wires;
    }


    public double calculateWireLength(Wire wire) {
        List<Vector2D> points = wire.getAllControlPoints();  // دریافت تمام نقاط کنترل سیم
        double totalLength = 0;


        for (int i = 0; i < points.size() - 1; i++) {
            totalLength += points.get(i).distanceTo(points.get(i + 1));  // فاصله بین دو نقطه
        }


        List<BendSegment> bends = wire.getBends();
        for (BendSegment bend : bends) {
            totalLength += calculateBendLength(bend);
        }
        System.out.println("Total wire length: " + totalLength);
        return totalLength;

    }


    private double calculateBendLength(BendSegment bend) {

        return calculateBezierLength(bend.getBendPoint(), bend.getFromPortPosition());
    }


    private double calculateBezierLength(Vector2D bendPoint, Vector2D fromPortPosition) {
        double length = 0;
        final int SAMPLES = 20;

        for (int i = 0; i < SAMPLES - 1; i++) {
            double t1 = (double) i / SAMPLES;
            double t2 = (double) (i + 1) / SAMPLES;


            Vector2D point1 = getBezierPoint(fromPortPosition, bendPoint, t1);
            Vector2D point2 = getBezierPoint(fromPortPosition, bendPoint, t2);


            length += point1.distanceTo(point2);
        }

        return length;
    }


    private Vector2D getBezierPoint(Vector2D p0, Vector2D p1, double t) {
        double x = Math.pow(1 - t, 2) * p0.getX() + 2 * (1 - t) * t * p1.getX() + Math.pow(t, 2) * p1.getX();
        double y = Math.pow(1 - t, 2) * p0.getY() + 2 * (1 - t) * t * p1.getY() + Math.pow(t, 2) * p1.getY();
        return new Vector2D(x, y);
    }


    public double getTotalWireLength() {
        double total = 0;
        for (Wire wire : wires) {
            total += calculateWireLength(wire);
        }
        return total;
    }


    public void addWire(Wire newWire) {
        wires.add(newWire);
    }


    public void removeWire(Wire wire) {
        wires.remove(wire);
    }
}
