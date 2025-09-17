package model;


public class AcceleratingOnIncompatStrategy implements MovementStrategy {
    private final double vCompat, vStartIncompat, aIncompat, vMax;
    private boolean fromCompat = true;

    public AcceleratingOnIncompatStrategy(double vCompat, double vStartIncompat, double aIncompat, double vMax){
        this.vCompat = vCompat;
        this.vStartIncompat = vStartIncompat;
        this.aIncompat = aIncompat;
        this.vMax = vMax;
    }

    @Override
    public void onWireEnter(Wire wire, boolean fromCompatible, boolean toCompatible, Packet p) {
        this.fromCompat = fromCompatible;
        p.setScalarSpeed(fromCompatible ? vCompat : vStartIncompat);
    }

    @Override
    public void update(Packet p, double dt) {
        if (!fromCompat) {
            double v = Math.min(p.getScalarSpeed() + aIncompat * dt, vMax);
            p.setScalarSpeed(v);
        } else {
            p.setScalarSpeed(vCompat);
        }
    }
}
