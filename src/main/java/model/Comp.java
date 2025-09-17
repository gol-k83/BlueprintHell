package model;

public final class Comp {
    private Comp(){}

    /** نویز روی برخورد */
    public static PacketComponent noiseOnImpact(int amount){
        return new PacketComponent() {
            @Override public void onCollision(Packet self, Packet other) {
                if (self != null) self.applyNoise(amount);
                if (other != null) other.applyNoise(amount);
            }
        };
    }

    /** محرمانه: کند شدن نزدیک مقصد شلوغ – نسخهٔ ش ساده */
    public static PacketComponent slowNearBusy(double slowFactor, double radiusPx){
        return new PacketComponent() {
            @Override public void onUpdate(Packet p, double dt) {
                p.setScalarSpeed(p.getScalarSpeed() * (1 - slowFactor * dt));
            }
        };
    }

    /** محرمانه6 حفظ فاصله */
    public static PacketComponent keepDistance(double radiusPx, double push){
        return new PacketComponent() {
            @Override public void onUpdate(Packet p, double dt) {
                // نسخهٔ سبک: کمی محدودیت سرعت برای جلوگیری از چسبیدن
                p.setScalarSpeed(Math.max(40, p.getScalarSpeed() - 10*dt));
            }
        };
    }

    /** حجیم: استهلاک سیم */
    public static PacketComponent wireWear(int maxPasses){
        return new PacketComponent() {
            // TODO: بعداً بیا شمارنده عبور را در Wire پیاده کن و این‌جا استفاده کن
        };
    }

    /** حجیم: پاک‌کردن بافر سیستم در ورود */
    public static PacketComponent purgeOnEnter(){
        return new PacketComponent() {
            @Override public void onEnterSystem(Packet p, SystemNode node, Port in) {
                if (node != null) node.purgeBuffer();
            }
        };

    }


    public static PacketComponent bounceBackOnCollision() {
        return new PacketComponent() {
            @Override
            public void onCollision(Packet self, Packet other) {
                if (self == null) return;
                // برعکس کردن سرعت
                if (self.getVelocity() != null) {
                    self.setVelocity(self.getVelocity().multiply(-1));
                }
                //  اسکالر را هم منفی کند
                self.setScalarSpeed(-self.getScalarSpeed());


                if (self.getPosition() != null && self.getVelocity() != null) {
                    // 2 پیکسل در جهت جدید
                    self.setPosition(self.getPosition().add(self.getVelocity().normalize().multiply(2)));
                }
            }
        };
    }
}
