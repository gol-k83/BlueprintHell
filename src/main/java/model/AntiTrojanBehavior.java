package model;

import model.behavior.NodeBehavior;

import java.util.List;


public class AntiTrojanBehavior implements NodeBehavior {
    private static final double RADIUS = 180;   // px
    private static final double COOLDOWN = 2.5; // s

    private double cd = 0;

    @Override
    public void processPacket(SystemNode node, Packet packet) {

        node.sendPacketNormal(packet);
    }

    @Override
    public void onUpdate(SystemNode node, double dt) {
        if (cd > 0) { cd -= dt; return; }

        // جستجوی پکت‌های نزدیک با رجیستری پکت‌ها سیم‌ها) هولولووو
        List<Packet> nearby = SystemNodeRegistry.findPacketsInRadius(node.getPosition(), RADIUS);
        boolean acted = false;
        for (Packet p : nearby) {
            if (Boolean.TRUE.equals(p.getTag("trojan"))) {
                // تبدیل به پیام‌رسان ساده )
                Packet converted = PacketFactory.msgSquare(p.getPosition());
                converted.setVelocity(p.getVelocity());
                // جابجایی در همان Wire/Context
                SystemNodeRegistry.replacePacketInPlace(p, converted);
                acted = true;
            }
        }
        if (acted) cd = COOLDOWN; //بعدا بیام ببینم بعد از هر عملکرد موفق، مدتی از کار می‌افتد
    }
}
