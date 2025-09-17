package model;

public class ConstantSpeedStrategy implements MovementStrategy {
    private final double vCompat, vIncompat;

    public ConstantSpeedStrategy(double vCompat, double vIncompat){
        this.vCompat = vCompat;
        this.vIncompat = vIncompat;
    }

    @Override
    public void onWireEnter(Wire wire, boolean fromCompatible, boolean toCompatible, Packet p) {
        p.setScalarSpeed(fromCompatible ? vCompat : vIncompat);
    }

    @Override
    public void update(Packet p, double dt) {
        // سرعت ثابت؛ کاری لازم نیست.
    }
}
