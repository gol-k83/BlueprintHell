//// ✅ BendSegment.java — ساختار خم با رعایت SOLID و Clean Code
//
//package model;
//
//import util.Vector2D;
//
///**
// * نماینده‌ی یک نقطه خم روی سیم که در یک سگمنت خاص قرار گرفته است.
// * این کلاس با رعایت اصول SOLID طراحی شده است.
// */
//public class BendSegment {
//
//    private final int segmentIndex;    // اندیس سگمنت مربوط به خم (بین کدام دو نقطه کنترل)
//    private final Vector2D bendPoint;  // مختصات دقیق نقطه خم (در screen coordinates)
//    private final Port fromPort;
//    /**
//     * سازنده کلاس خم.
//     * @param segmentIndex اندیس سگمنت بین دو نقطه کنترل که این خم در آن درج شده
//     * @param bendPoint مختصات نقطه خم در مختصات صفحه
//     */
//    public BendSegment(int segmentIndex, Vector2D bendPoint) {
//
//        if (segmentIndex < 0) {
//            throw new IllegalArgumentException("Segment index must be non-negative.");
//        }
//        if (bendPoint == null) {
//            throw new IllegalArgumentException("Bend point cannot be null.");
//        }
//        this.segmentIndex = segmentIndex;
//        this.bendPoint = bendPoint;
//    }
//    public BendSegment(int segmentIndex, Vector2D bendPoint, Port fromPort) {
//        this.segmentIndex = segmentIndex;
//        this.bendPoint = bendPoint;
//        this.fromPort = fromPort;
//    }
//
//    /**
//     * دریافت اندیس سگمنت مربوط به این خم
//     * @return اندیس سگمنت (0 به بعد)
//     */
//    public int getSegmentIndex() {
//        return segmentIndex;
//    }
//
//    /**
//     * دریافت مختصات نقطه خم
//     * @return مختصات Vector2D در screen
//     */
//    public Vector2D getBendPoint() {
//        return bendPoint;
//    }
//
//    /**
//     * نمایش قابل‌خواندن برای لاگ‌گیری یا دیباگ
//     */
//    @Override
//    public String toString() {
//        return "BendSegment{segmentIndex=" + segmentIndex + ", bendPoint=" + bendPoint + "}";
//    }
//
//    /**
//     * دو BendSegment برابرند اگر segmentIndex و مختصات‌شان یکی باشد.
//     */
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (obj == null || getClass() != obj.getClass()) return false;
//
//        BendSegment that = (BendSegment) obj;
//        return segmentIndex == that.segmentIndex && bendPoint.equals(that.bendPoint);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = Integer.hashCode(segmentIndex);
//        result = 31 * result + bendPoint.hashCode();
//        return result;
//    }
//
//   // public Vector2D getFromPortPosition() {
//        public Vector2D getFromPortPosition() {
//            return fromPort.getPosition();  // دریافت موقعیت از پورت
//        }
//    }
//
package model;

import util.Vector2D;


public class BendSegment {

    private final int segmentIndex;
    private final Vector2D bendPoint;
    private final Port fromPort;

    public BendSegment(int segmentIndex, Vector2D bendPoint) {

        if (segmentIndex < 0) {
            throw new IllegalArgumentException("Segment index must be non-negative.");
        }
        if (bendPoint == null) {
            throw new IllegalArgumentException("Bend point cannot be null.");
        }
        this.segmentIndex = segmentIndex;
        this.bendPoint = bendPoint;
        this.fromPort = null;
    }

    public BendSegment(int segmentIndex, Vector2D bendPoint, Port fromPort) {
        this.segmentIndex = segmentIndex;
        this.bendPoint = bendPoint;
        this.fromPort = fromPort;
    }


    public int getSegmentIndex() {
        return segmentIndex;
    }


    public Vector2D getBendPoint() {
        return bendPoint;
    }


    public Vector2D getFromPortPosition() {
        return (fromPort != null) ? fromPort.getPosition() : null;
    }


    @Override
    public String toString() {
        return "BendSegment{segmentIndex=" + segmentIndex + ", bendPoint=" + bendPoint + ", fromPort=" + fromPort + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BendSegment that = (BendSegment) obj;
        return segmentIndex == that.segmentIndex && bendPoint.equals(that.bendPoint);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(segmentIndex);
        result = 31 * result + bendPoint.hashCode();
        return result;
    }
}
