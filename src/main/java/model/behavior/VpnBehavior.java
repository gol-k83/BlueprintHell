
package model.behavior;

import model.Packet;
import model.SystemNode;
import model.policy.ArrivalPolicy;
import model.routing.PortRouter;

import java.util.List;


public class VpnBehavior extends StandardBehavior {
    public VpnBehavior(PortRouter r, List<ArrivalPolicy> ps, BehaviorContext c) {
        super(r, ps, c);
    }

    @Override
    public void processPacket(SystemNode node, Packet pkt) {

        Packet prot = ctx.isProtected(pkt) ? pkt : ctx.toProtected(pkt);
        super.processPacket(node, prot);
    }

    @Override
    public void onPowerStateChanged(SystemNode n, boolean on) {
        if (!on) {

            ctx.vpnDown(n);
        }
    }
}
