//package model.routing;
//
//import model.Packet;
//import model.Port;
//import model.SystemNode;
//import model.Wire;
//
//import java.util.Optional;
//
///** انتخاب «بهترین» پورت خروجی برای یک پکت. */
//public interface PortRouter {
//    Port decide(SystemNode node, Packet pkt, BehaviorContext ctx)
//
//    // ---------- پیاده‌سازی پیش‌فرض ----------
//    class DefaultPortRouter implements PortRouter {
//        @Override
//        public Port choosePort(SystemNode node, Packet packet) {
//            if (node == null || packet == null) return null;
//
//            // 1) سازگار + مقصد ON + سیم بیکار
//            Optional<Port> p1 = node.getOutputPorts().stream()
//                    .filter(p -> p != null
//                            && !p.isOccupied()
//                            && p.isCompatibleWith(packet.getShape())
//                            && leadsToOnNode(node, p)
//                            && wireIdle(p))
//                    .findFirst();
//            if (p1.isPresent()) return p1.get();
//
//            // 2) هر پورت خالی با مقصد ON و سیم بیکار (fallback)
//            Optional<Port> p2 = node.getOutputPorts().stream()
//                    .filter(p -> p != null
//                            && !p.isOccupied()
//                            && leadsToOnNode(node, p)
//                            && wireIdle(p))
//                    .findFirst();
//            return p2.orElse(null);
//        }
//
//        private boolean leadsToOnNode(SystemNode node, Port out) {
//            return node != null && out != null && node.leadsToOnNode(out);
//        }
//        private boolean wireIdle(Port out) {
//            Wire w = out.getConnectedWire();
//            return (w != null) && w.getPacketsOnWire().isEmpty();
//        }
//    }
//}
// file: src/main/java/model/routing/PortRouter.java
package model.routing;

import model.Packet;
import model.Port;
import model.SystemNode;
import model.behavior.BehaviorContext;

public interface PortRouter {
    /** انتخاب پورت خروجی برای این پکت در این نود */
    Port decide(SystemNode node, Packet pkt, BehaviorContext ctx);

    /** یک روتر پیش‌فرض (سازگار-اول) برای راحتی */
    final class DefaultPortRouter implements PortRouter {

@Override
public Port decide(SystemNode node, Packet pkt, BehaviorContext ctx) {
    for (Port p : node.getOutputPorts()) {
        boolean ok = !p.isOccupied()
                && p.isCompatibleWith(pkt.getShape())
                && ctx.leadsToActiveSystem(p)
                && ctx.isWireIdle(p);
        util.Dbg.log("ROUTER", "compatTry out=%s compat=%s destOn=%s idle=%s occ=%s -> %s",
                p.getId(), p.isCompatibleWith(pkt.getShape()),
                ctx.leadsToActiveSystem(p), ctx.isWireIdle(p), p.isOccupied(), ok);
        if (ok) return p;
    }
    for (Port p : node.getOutputPorts()) {
        boolean ok = !p.isOccupied()
                && ctx.leadsToActiveSystem(p)
                && ctx.isWireIdle(p);
        util.Dbg.log("ROUTER", "anyTry out=%s destOn=%s idle=%s occ=%s -> %s",
                p.getId(), ctx.leadsToActiveSystem(p), ctx.isWireIdle(p), p.isOccupied(), ok);
        if (ok) return p;
    }
    util.Dbg.log("ROUTER", "no port for node=%s pkt=%s", node.getId(), pkt.getShape());
    return null;
}

    }
}
