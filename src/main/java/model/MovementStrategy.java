package model;

public interface MovementStrategy {
    void update(Packet p, double dt);
    default void onWireEnter(Wire wire, boolean fromCompat, boolean toCompat, Packet p) {}
    default void onWireExit(Wire wire, Packet p) {}
}


class MoveConst implements MovementStrategy {
    private final double v;
    public MoveConst(double v){ this.v = v; }
    @Override public void onWireEnter(Wire w, boolean f, boolean t, Packet p){ p.setScalarSpeed(v); }
    @Override public void update(Packet p, double dt){ p.setScalarSpeed(v); }
}


class MoveConstCompat implements MovementStrategy {
    private final double vCompat, vIncompat;
    public MoveConstCompat(double vCompat, double vIncompat){ this.vCompat=vCompat; this.vIncompat=vIncompat; }
    @Override public void onWireEnter(Wire w, boolean fromCompat, boolean toCompat, Packet p){
        p.setScalarSpeed(fromCompat ? vCompat : vIncompat);
    }
    @Override public void update(Packet p, double dt){ /* همان مقدار را حفظ می‌کنیم */ }
}


class MoveAccel implements MovementStrategy {
    private final double a, vMin, vMax;
    public MoveAccel(double a, double vMin, double vMax){ this.a=a; this.vMin=vMin; this.vMax=vMax; }
    @Override public void update(Packet p, double dt){
        double v = Math.max(vMin, Math.min(p.getScalarSpeed()+a*dt, vMax));
        p.setScalarSpeed(v);
    }
}
class MoveDecel implements MovementStrategy {
    private final double a, vMin;
    public MoveDecel(double a, double vMin){ this.a=a; this.vMin=vMin; }
    @Override public void update(Packet p, double dt){
        double v = Math.max(vMin, p.getScalarSpeed()-a*dt);
        p.setScalarSpeed(v);
    }
}

/** مثلث: سازگار=ثابت، ناسازگار=شتاب‌دار تا سقف */
class MoveAccelOnIncompat implements MovementStrategy {
    private final double vCompat, vStartIncompat, a, vMax;
    public MoveAccelOnIncompat(double vCompat, double vStartIncompat, double a, double vMax){
        this.vCompat=vCompat; this.vStartIncompat=vStartIncompat; this.a=a; this.vMax=vMax;
    }
    private boolean incompat = false;
    @Override public void onWireEnter(Wire w, boolean fromCompat, boolean toCompat, Packet p){
        incompat = !fromCompat;
        p.setScalarSpeed(incompat ? vStartIncompat : vCompat);
    }
    @Override public void update(Packet p, double dt){
        if (incompat){
            double v = Math.min(p.getScalarSpeed() + a*dt, vMax);
            p.setScalarSpeed(v);
        } else {
            p.setScalarSpeed(vCompat);
        }
    }
}

/** Message(1): سازگار=شتاب ثابت؛ ناسازگار=کاهش سرعت */
class MoveMsg1 implements MovementStrategy {
    private final double v0Compat, aCompat, aDecelIncompat, vMinIncompat;
    private boolean compat=false;
    public MoveMsg1(double v0Compat, double aCompat, double aDecelIncompat, double vMinIncompat){
        this.v0Compat=v0Compat; this.aCompat=aCompat; this.aDecelIncompat=aDecelIncompat; this.vMinIncompat=vMinIncompat;
    }
    @Override public void onWireEnter(Wire w, boolean fromCompat, boolean toCompat, Packet p){
        compat = fromCompat;
        p.setScalarSpeed(compat ? v0Compat : Math.max(vMinIncompat, p.getScalarSpeed()));
    }
    @Override public void update(Packet p, double dt){
        if (compat){
            p.setScalarSpeed(p.getScalarSpeed() + aCompat*dt);
        } else {
            double v = Math.max(vMinIncompat, p.getScalarSpeed() - aDecelIncompat*dt);
            p.setScalarSpeed(v);
        }
    }
}


class MoveDrift implements MovementStrategy {
    private final double baseV;
    public MoveDrift(double baseV){ this.baseV=baseV; }
    @Override public void update(Packet p, double dt){
        p.setScalarSpeed(Math.max(baseV, p.getScalarSpeed()));
    }
}

