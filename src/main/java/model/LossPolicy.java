package model;

/**

 */
public interface LossPolicy {
    boolean isLost(Packet p, Ctx c);


    static boolean checkAndMark(Packet p, LossPolicy policy, Ctx c){
        if (policy != null && policy.isLost(p, c)) { p.markLost(); return true; }
        return false;
    }


    class Ctx {
        public final double offTrack;
        public final double timeOnWire;
        public final boolean atEntrance;       // در آستانه‌ی ورود به سیستم؟
        public final double speed;             // سرعت فعلی
        public final double maxOffTrack;       // آستانه انحراف مجاز
        public final double maxTimeOnWire;     // آستانه زمان مجاز
        public final double maxSpeedAtEntrance;// آستانه سرعت ورودی سیستم

        public Ctx(double offTrack, double timeOnWire, boolean atEntrance,
                   double speed, double maxOffTrack, double maxTimeOnWire, double maxSpeedAtEntrance) {
            this.offTrack = offTrack;
            this.timeOnWire = timeOnWire;
            this.atEntrance = atEntrance;
            this.speed = speed;
            this.maxOffTrack = maxOffTrack;
            this.maxTimeOnWire = maxTimeOnWire;
            this.maxSpeedAtEntrance = maxSpeedAtEntrance;
        }
    }


    /** خروج از مسیر */
    class OffTrack implements LossPolicy {
        @Override public boolean isLost(Packet p, Ctx c) {
            return c != null && c.offTrack > c.maxOffTrack;
        }
    }

    /** نویز >= اندازه */
    class NoiseOverflow implements LossPolicy {
        @Override public boolean isLost(Packet p, Ctx c) {
            return p != null && p.getNoise() >= p.getSize();
        }
    }

    /** ماندن بیش از حد روی سیم */
    class Timeout implements LossPolicy {
        @Override public boolean isLost(Packet p, Ctx c) {
            return c != null && c.timeOnWire > c.maxTimeOnWire;
        }
    }


    class OverSpeedEntrance implements LossPolicy {
        @Override public boolean isLost(Packet p, Ctx c) { return false; }
    }

    /** ترکیب چند Policy */
    class Chain implements LossPolicy {
        private final LossPolicy[] list;
        public Chain(LossPolicy... list){ this.list = list; }
        @Override public boolean isLost(Packet p, Ctx c) {
            if (list == null) return false;
            for (LossPolicy lp : list) if (lp != null && lp.isLost(p, c)) return true;
            return false;
        }
    }
}
