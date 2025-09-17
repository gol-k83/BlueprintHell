package model.routing;

import model.*;
import model.behavior.BehaviorContext;

public class CompatibleFirstRouter implements PortRouter {
    @Override

    public Port decide(SystemNode node, Packet pkt, BehaviorContext ctx) {
        for (Port p : node.getOutputPorts()) {
            if (!p.isOccupied()
                    && p.isCompatibleWith(pkt.getShape())
                    && ctx.leadsToActiveSystem(p)
                    && ctx.isWireIdle(p)) {
                return p;
            }
        }
        for (Port p : node.getOutputPorts()) {
            if (!p.isOccupied()
                    && ctx.leadsToActiveSystem(p)
                    && ctx.isWireIdle(p)) {
                return p;
            }
        }
        return null;
    }

    }