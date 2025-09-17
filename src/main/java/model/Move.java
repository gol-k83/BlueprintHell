
package model;

public final class Move {
    private Move(){}

   //سرعت سابت
    public static MovementStrategy constV(double v){
        return new MovementStrategy() {
            @Override public void onWireEnter(Wire w, boolean f, boolean t, Packet p){ p.setScalarSpeed(v); }
            @Override public void update(Packet p, double dt){ p.setScalarSpeed(v); }
        };
    }

    /** سرعت ثابت بسته به سازگاری پورت شروع */
    public static MovementStrategy constCompat(double vCompat, double vIncompat){
        return new MovementStrategy() {
            private boolean compat;
            @Override public void onWireEnter(Wire w, boolean fromCompat, boolean toCompat, Packet p){
                compat = fromCompat; p.setScalarSpeed(compat ? vCompat : vIncompat);
            }
            @Override public void update(Packet p, double dt){ /* ثابت */ }///////////////////////////////////////
        };
    }

    /** شتاب‌دار عمومی: */
    public static MovementStrategy accel(double a, double vMin, double vMax){
        return (p, dt) -> {
            double v = Math.max(vMin, Math.min(p.getScalarSpeed()+a*dt, vMax));
            p.setScalarSpeed(v);
        };
    }

    /** کاهش سرعت */
    public static MovementStrategy decel(double a, double vMin){
        return (p, dt) -> p.setScalarSpeed(Math.max(vMin, p.getScalarSpeed()-a*dt));
    }

    /** مثلث: سازگار=ثابت، ناسازگار=شتاب‌دار تا سقف */
    public static MovementStrategy accelOnIncompat(double vCompat, double vStartIncompat, double a, double vMax){
        return new MovementStrategy() {
            private boolean incompat;
            @Override public void onWireEnter(Wire w, boolean fromCompat, boolean toCompat, Packet p){
                incompat = !fromCompat;
                p.setScalarSpeed(incompat ? vStartIncompat : vCompat);
            }
            @Override public void update(Packet p, double dt){
                if (incompat) p.setScalarSpeed(Math.min(p.getScalarSpeed()+a*dt, vMax));
                else p.setScalarSpeed(vCompat);
            }
        };
    }


    public static MovementStrategy msg1(double v0Compat, double aCompat, double aDecelIncompat, double vMinIncompat){
        return new MovementStrategy() {
            private boolean compat;
            @Override public void onWireEnter(Wire w, boolean fromCompat, boolean toCompat, Packet p){
                compat = fromCompat;
                p.setScalarSpeed(compat ? v0Compat : Math.max(vMinIncompat, p.getScalarSpeed()));
            }
            @Override public void update(Packet p, double dt){
                if (compat) p.setScalarSpeed(p.getScalarSpeed() + aCompat*dt);
                else p.setScalarSpeed(Math.max(vMinIncompat, p.getScalarSpeed() - aDecelIncompat*dt));
            }
        };
    }

    // Massive(10)
    public static MovementStrategy drift(double baseV){
        return (p, dt) -> p.setScalarSpeed(Math.max(baseV, p.getScalarSpeed()));
    }


    public static MovementStrategy randomMessageLike(MovementStrategy... options){
        if (options == null || options.length == 0) {
            return constV(100); // fallback
        }
        return new MovementStrategy() {
            private MovementStrategy chosen;

            @Override
            public void onWireEnter(Wire w, boolean fromCompat, boolean toCompat, Packet p) {
                int idx = (int)(Math.random() * options.length);
                if (idx < 0 || idx >= options.length) idx = 0;
                chosen = options[idx];
                chosen.onWireEnter(w, fromCompat, toCompat, p);
            }

            @Override
            public void update(Packet p, double dt) {
                if (chosen == null) chosen = options[0];
                chosen.update(p, dt);
            }
        };
    }

    /**
     * Heavy روی خم‌ها شتاب می‌گیرد و روی خطوط صاف ثابت است.


     */
    public static MovementStrategy curvedAccelOrConst(double baseV, double aOnCurve){
        return constV(baseV); // TODO: در آینده با تشخیص سگمنت خم، v += aOnCurve*dt/////////////////////////////////////////////////////////////////اینجارو نگاه
    }
}
