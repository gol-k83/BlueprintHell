
package model;

import controller.StageManager;
import util.Vector2D;

import java.util.*;


public class Wire {
    private final Port fromPort;
    private final Port toPort;
private final String id;

    private final List<Packet> packetsOnWire = new LinkedList<>();

    private final List<Vector2D> bendPoints  = new ArrayList<>();

    private final List<BendSegment> bends    = new ArrayList<>();

    private int bendableSlots = 0; // 0 ⇒ پیش‌فرض 3


    private final Map<Packet, Double> distOnWire = new HashMap<>();


    private int heavyPassCount = 0;
    private static final int HEAVY_MAX_PASSES = 3;

    private static final double ARRIVE_EPS = 5.0;

    private static final double MAX_OFFTRACK          = 12.0;
    private static final double MAX_TIME_ON_WIRE      = 10.0;
    private static final double MAX_SPEED_AT_ENTRANCE = 300.0;






    public Wire(Port fromPort, Port toPort) {
        this.fromPort = fromPort;
        this.toPort   = toPort;
        this.id=buildId(fromPort,toPort);
    }


    public Port getFromPort() { return fromPort; }
    public Port getToPort()   { return toPort; }


    public List<Vector2D> getBendPoints() { return new ArrayList<>(bendPoints); }

    public List<BendSegment> getBends() {
        bends.sort(Comparator.comparingInt(BendSegment::getSegmentIndex));
        return new ArrayList<>(bends);
    }

    public List<Packet> getPacketsOnWire() {
        return new ArrayList<>(packetsOnWire); // read-only snapshot
    }

    public int getMaxBends() { return (bendableSlots > 0) ? bendableSlots : 3; }
    public void purchaseBendSlot() { bendableSlots++; }
    public boolean canAddBendPoint() { return bends.size() < getMaxBends(); }


    public void insertAnchorAtSegment(int segIndex, Vector2D anchor) {
        if (anchor == null || segIndex < 0) return;
        int insertAt = Math.max(0, Math.min(segIndex, bendPoints.size()));
        bendPoints.add(insertAt, anchor);
        // اندیِسِ سگمنتِ کنترل‌ها را بعد از درج Anchor شیفت بدیمه
        for (int i = 0; i < bends.size(); i++) {
            BendSegment b = bends.get(i);
            if (b.getSegmentIndex() >= segIndex) {
                bends.set(i, new BendSegment(b.getSegmentIndex() + 1, b.getBendPoint()));
            }
        }
    }

 //نقطه خارج خم کنترل
    public void setControlForSegment(int segIndex, Vector2D ctrl) {
        if (ctrl == null || segIndex < 0) return;
        for (int i = 0; i < bends.size(); i++) {
            if (bends.get(i).getSegmentIndex() == segIndex) {
                bends.set(i, new BendSegment(segIndex, ctrl));
                return;
            }
        }
        if (canAddBendPoint()) {
            bends.add(new BendSegment(segIndex, ctrl));
            bends.sort(Comparator.comparingInt(BendSegment::getSegmentIndex));
        }
    }


    public BendSegment getControlOnSegment(int segmentIndex) {
        for (BendSegment b : bends) if (b.getSegmentIndex() == segmentIndex) return b;
        return null;
    }


    public boolean updateBendOnSegment(int segmentIndex, Vector2D newPos) {
        if (segmentIndex < 0 || newPos == null) return false;
        setControlForSegment(segmentIndex, newPos);
        return true;
    }


    public boolean removeBendCompletelyAtSegment(int segmentIndex) {
        boolean removed = false;


        for (int i = 0; i < bends.size(); i++) {
            if (bends.get(i).getSegmentIndex() == segmentIndex) {
                bends.remove(i);
                removed = true;
                break;
            }
        }

        // 2) حذف Anchor متناظر  و شیفت ایندیس کنترل‌های بعدی
        if (segmentIndex >= 0 && segmentIndex < bendPoints.size()) {
            bendPoints.remove(segmentIndex);
            removed = true;

            for (int i = 0; i < bends.size(); i++) {
                BendSegment b = bends.get(i);
                if (b.getSegmentIndex() > segmentIndex) {
                    bends.set(i, new BendSegment(b.getSegmentIndex() - 1, b.getBendPoint()));
                }
            }
        }

        return removed;
    }


    public void clearBendPoints() {
        bends.clear();
        bendPoints.clear();
    }

    public List<Vector2D> getAllControlPoints() {
        List<Vector2D> pts = new ArrayList<>();
        pts.add(fromPort.getAbsolutePosition());  // ⬅️ مطلق
        pts.addAll(bendPoints);                   // bendPoints را همان مطلق نگه دار
        pts.add(toPort.getAbsolutePosition());    // ⬅️ مطلق
        return pts;
    }

    public double getLength() {
        List<Vector2D> pts = getAllControlPoints();
        double total = 0;
        for (int i = 0; i < pts.size() - 1; i++) {
            total += pts.get(i).distanceTo(pts.get(i+1));
        }
        return total;
    }

    private Vector2D pointAtDistance(double s) {
        List<Vector2D> pts = getAllControlPoints();
        for (int i = 0; i < pts.size()-1; i++) {
            Vector2D a = pts.get(i), b = pts.get(i+1);
            double seg = a.distanceTo(b);
            if (seg <= 1e-6) continue;
            if (s <= seg) return a.add(b.subtract(a).normalize().multiply(s));
            s -= seg;
        }
        return toPort.getPosition();
    }

    private Vector2D tangentAtDistance(double s) {
        List<Vector2D> pts = getAllControlPoints();
        for (int i = 0; i < pts.size()-1; i++) {
            Vector2D a = pts.get(i), b = pts.get(i+1);
            double seg = a.distanceTo(b);
            if (seg <= 1e-6) continue;
            if (s <= seg) return b.subtract(a).normalize();
            s -= seg;
        }
        return pts.get(pts.size()-1).subtract(pts.get(pts.size()-2)).normalize();
    }

    public Double getDistanceOnWire(Packet p) {
        return distOnWire.get(p);
    }


    public void addPacket(Packet p) {
        if (!packetsOnWire.isEmpty()) return; // فعلاً یک پکت همزمان
        packetsOnWire.add(p);
        distOnWire.put(p, 0.0);
        boolean fromCompat = fromPort.isCompatibleWith(p.getShape());
        boolean toCompat   = toPort.isCompatibleWith(p.getShape());
        p.onWireEnter(this, fromCompat, toCompat);

        Vector2D tan = tangentAtDistance(0);
        double v0 = Math.max(p.getScalarSpeed(), 1.0);
        p.setPosition(fromPort.getAbsolutePosition());
        p.setVelocity(tan.multiply(v0));

        System.out.println("[Wire] addPacket ok -> packets=" + packetsOnWire.size());
    }
//
public List<Packet> getPacketsSnapshot() {
    return List.copyOf(packetsOnWire);
}


public void update(double dt) {
    if (packetsOnWire.isEmpty()) return;

    final double L = getLength();
    if (L <= 1e-6) {
        System.out.println("[Wire] skip update: zero-length wire");
        return;
    }


    StageManager sm = null;
    try { sm = StageManager.getInstance(); } catch (Throwable ignored) {}
    var tracker = (sm != null) ? sm.getTracker() : null;

    List<Packet> delivered = new ArrayList<>();

    for (Packet p : new ArrayList<>(packetsOnWire)) {
        if (p == null) continue;

        // اگر از قبل Lost شده
        if (p.isLost()) {
            if (tracker != null) tracker.addLost(p);
            System.out.println("[Wire] packet already LOST -> remove | spec=" + p.getSpec().family()
                    + " shape=" + p.getShape());
            delivered.add(p);
            continue;
        }

        // /کامپوننت
        try {
            p.update(dt);
        } catch (Throwable ex) {
            System.out.println("[Wire] ERROR in packet.update: " + ex.getMessage());
            ex.printStackTrace();
        }

        // پیشروی روی مسیر
        double prevS = distOnWire.getOrDefault(p, 0.0);
        double s = prevS + p.getScalarSpeed() * dt;


        LossPolicy policy = p.getLossPolicy();
        if (policy != null) {
            double offTrack = 0.0;
            boolean atEntrance = (s >= L - ARRIVE_EPS);
            LossPolicy.Ctx ctx = new LossPolicy.Ctx(
                    offTrack,
                    p.getTimeOnWire(),
                    atEntrance,
                    p.getScalarSpeed(),
                    MAX_OFFTRACK,
                    MAX_TIME_ON_WIRE,
                    MAX_SPEED_AT_ENTRANCE
            );
            boolean lost = false;
            try {
                lost = policy.isLost(p, ctx);
            } catch (Throwable ex) {
                System.out.println("[Wire] ERROR in LossPolicy: " + ex.getMessage());
                ex.printStackTrace();
            }
            if (lost) {
                p.markLost("policy");
                if (tracker != null) tracker.addLost(p);
                System.out.println("[Wire] packet LOST by policy | spec=" + p.getSpec().family()
                        + " shape=" + p.getShape());
                delivered.add(p);
                continue;
            }
        }
        // ---------------------------------------


        if (s >= L - ARRIVE_EPS) {
            p.onWireExit(this);
            p.setSpeedAtEntrance(p.getScalarSpeed());

            // Massive: فرسایش سیم
            if (p.getSpec().family() == PacketSpec.PacketFamily.MASSIVE) {
                heavyPassCount++;
                if (heavyPassCount >= HEAVY_MAX_PASSES) {
                    p.markLost("wire-broken");
                    if (tracker != null) tracker.addLost(p);
                    System.out.println("[Wire] wire BROKEN by massive packet | passes=" + heavyPassCount);
                    delivered.add(p);
                    continue;
                }
            }

            System.out.println("[Wire] packet ARRIVED at dst | spec=" + p.getSpec().family()
                    + " shape=" + p.getShape());
            delivered.add(p);
        } else {
            // حرکت میانی
            distOnWire.put(p, s);
            Vector2D pos = pointAtDistance(s);
            Vector2D tan = tangentAtDistance(s);
            p.setPosition(pos);
            p.setVelocity(tan.multiply(p.getScalarSpeed()));
        }
    }

    // تحویل/حذف نهایی
    for (Packet p : delivered) {
        packetsOnWire.remove(p);
        distOnWire.remove(p);
        toPort.setOccupied(false);

        if (!p.isLost()) {
            if (tracker != null) tracker.addDelivered(p);
            try {
                toPort.getParentNode().receivePacket(p);
            } catch (Throwable ex) {
                System.out.println("[Wire] ERROR on deliver to node: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

    }
}
    private static String buildId(Port f, Port t) {
        String a = (f != null ? f.getId() : "null");
        String b = (t != null ? t.getId() : "null");
        return a + "->" + b;
    }

    public String getId() { return id; }
}
