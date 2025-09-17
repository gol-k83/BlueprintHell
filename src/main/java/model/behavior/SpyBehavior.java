
package model.behavior;

import model.Packet;
import model.SystemNode;
import model.policy.ArrivalPolicy;
import model.routing.PortRouter;

import java.util.List;


public class SpyBehavior extends StandardBehavior {
    public SpyBehavior(PortRouter r, List<ArrivalPolicy> ps, BehaviorContext c) {
        super(r, ps, c);
    }

    @Override
    public void processPacket(SystemNode node, Packet pkt) {
        if (ctx.isProtected(pkt)) {
            super.processPacket(node, pkt);
            return;
        }
        if (ctx.isSecret(pkt)) {                   // Secret = حذف
            ctx.destroy(pkt);
            return;
        }
        // پیام‌های عادی: تله‌پورت به یکی از Spyهای دیگ
        SystemNode other = ctx.pickOtherSpy(node);
        if (other != null && other != node) {
            ctx.teleport(pkt, other);
            return;
        }
        super.processPacket(node, pkt);
    }
}
