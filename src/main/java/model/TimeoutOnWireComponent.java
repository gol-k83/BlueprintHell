package model;

///اگر مدت حضور پکت روی یک سیم از حد استانه بگذره، پکت را از بین میرهود.
public class TimeoutOnWireComponent implements PacketComponent {
    private final double maxSecondsOnWire;
    private double t = 0;

    public TimeoutOnWireComponent(double maxSecondsOnWire){
        this.maxSecondsOnWire = maxSecondsOnWire;
    }

    @Override
    public void onWireEnter(Wire wire, Packet p) { t = 0; }

    @Override
    public void onUpdate(Packet p, double dt) {
        t += dt;
        if (t > maxSecondsOnWire) {
            p.applyNoise(p.getSize()); // مساوی drop شدن
        }
    }
}
