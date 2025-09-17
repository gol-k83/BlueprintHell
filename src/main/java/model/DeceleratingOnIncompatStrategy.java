package model;

/** اگر مبدا ناسازگار باشد، سرعت با شتاب منفی تا vMin کاهش می‌یابد؛ وگرنه vCompat. */
public class DeceleratingOnIncompatStrategy implements MovementStrategy {
    private final double vCompat, vStart, decel, vMin;
    private boolean fromCompat = true;

    public DeceleratingOnIncompatStrategy(double vCompat, double vStart, double decel, double vMin){
        this.vCompat = vCompat;
        this.vStart = vStart;
        this.decel = decel;
        this.vMin = vMin;
    }

    @Override
    public void onWireEnter(Wire wire, boolean fromCompatible, boolean toCompatible, Packet p) {
        fromCompat = fromCompatible;
        p.setScalarSpeed(fromCompatible ? vCompat : vStart);
    }

    @Override
    public void update(Packet p, double dt) {
        if (!fromCompat) {
            double v = Math.max(p.getScalarSpeed() - decel * dt, vMin);
            p.setScalarSpeed(v);
        } else {
            p.setScalarSpeed(vCompat);
        }
    }
}
