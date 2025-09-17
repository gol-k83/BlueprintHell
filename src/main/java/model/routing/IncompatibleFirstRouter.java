package model.routing;

import model.*;
import model.behavior.BehaviorContext;

public class IncompatibleFirstRouter implements PortRouter {
    @Override
    public Port decide(SystemNode node, Packet pkt, BehaviorContext ctx) {

        for (Port p : node.getOutputPorts()) {
            if (!p.isOccupied()
                    && !p.isCompatibleWith(pkt.getShape())
                    && ctx.leadsToActiveSystem(p)) {
                return p;
            }
        }
        // سازگار + خالی + مقصد فعال
        for (Port p : node.getOutputPorts()) {
            if (!p.isOccupied()
                    && p.isCompatibleWith(pkt.getShape())
                    && ctx.leadsToActiveSystem(p)) {
                return p;
            }
        }
        // 3 هر خالی + مقصد فعال
        for (Port p : node.getOutputPorts()) {
            if (!p.isOccupied() && ctx.leadsToActiveSystem(p)) {
                return p;
            }
        }
        return null;
    }
}
