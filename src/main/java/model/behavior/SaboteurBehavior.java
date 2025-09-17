//package model.behavior;
//
//import model.Packet;
//import model.PacketSpec;
//import model.SystemNode;
//
//public class SaboteurBehavior implements NodeBehavior {
//    private static final double TROJAN_PROB = 0.15; // احتمال تروجان (قابل تنظیم)
//
//    @Override
//    public void processPacket(SystemNode node, Packet packet) {
//        // روی Protected اثری ندارد
//        if (packet.getSpec().family() == PacketSpec.PacketFamily.PROTECTED) {
//            node.sendPacketNormal(packet);
//            return;
//        }
//        // اگر نویز صفر بود، +1 نویز
//        if (packet.getNoise() == 0) packet.applyNoise(1);
//
//        // احتمال تبدیل به تروجان (نیاز به پرچم ساده روی Packet یا Component)
//        if (Math.random() < TROJAN_PROB) {
//            packet.setTag("trojan", true); // <-- متد سبک برای تگ‌گذاری؛ اگر نداری اضافه کن
//        }
//
//        // ترجیح: پورت ناسازگارِ خالی با مقصد ON
//        if (!node.sendPacketSaboteur(packet)) {
//            node.sendPacketNormal(packet);
//        }
//    }
//}
//
package model.behavior;

import model.Packet;
import model.Port;
import model.SystemNode;
import model.policy.ArrivalPolicy;
import model.routing.IncompatibleFirstRouter;
import model.routing.PortRouter;

import java.util.List;

public class SaboteurBehavior extends StandardBehavior {
    private final PortRouter incompatibleFirst = new IncompatibleFirstRouter();

    public SaboteurBehavior(PortRouter fallbackRouter, List<ArrivalPolicy> ps, BehaviorContext c) {
        super(fallbackRouter, ps, c);
    }

    @Override
    public void processPacket(SystemNode node, Packet pkt) {
        // روی Protected
        if (ctx.isProtected(pkt)) {
            super.processPacket(node, pkt);
            return;
        }


        if (/* noise صفر؟ */ true) {////////////////////////////////////////////////////////////////////////////
            ctx.addNoise(pkt, 1);
        }
        ctx.tryTrojanize(pkt, /*prob*/ 0.15); ////////////////

        // اولویت ناسازگار
        Port out = incompatibleFirst.decide(node, pkt, ctx);
        if (out != null) ctx.sendThrough(out, pkt);
        else super.processPacket(node, pkt); // اگر نشد، روال نرمال
    }
}
