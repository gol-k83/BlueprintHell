
package model.behavior;

import model.Packet;
import model.Port;
import model.SystemNode;
import model.policy.ArrivalPolicy;
import model.routing.PortRouter;
import util.Dbg;

import java.util.List;
import java.util.Objects;

public class StandardBehavior implements NodeBehavior {
    private final PortRouter router;
    private final List<ArrivalPolicy> policies;
    final BehaviorContext ctx;

    public StandardBehavior(PortRouter router, List<ArrivalPolicy> policies, BehaviorContext ctx) {
        this.router   = Objects.requireNonNull(router, "router");
        this.policies = (policies != null) ? policies : List.of();
        this.ctx      = Objects.requireNonNull(ctx, "context");
    }

    @Override
    public void processPacket(SystemNode node, Packet pkt) {
        Dbg.log("BEHAV", "process node=%s pkt=%s policies=%d router=%s",
                node.getId(), pkt.getShape(), policies.size(), router.getClass().getSimpleName());

        for (var p : policies) p.onArrive(node, pkt, ctx);
        Dbg.log("BEHAV", "after onArrive");

        Port out = router.decide(node, pkt, ctx);
        Dbg.log("BEHAV", "router.decide -> %s",
                out != null ? (out.getId()+"/"+out.getCompatibleShape()) : "null");

        for (var p : policies) p.onRouted(node, pkt, out, ctx);
        Dbg.log("BEHAV", "after onRouted");

        if (out != null) ctx.sendThrough(out, pkt); else ctx.enqueueOrDrop(node, pkt);
    }

    @Override public void onUpdate(SystemNode n, double dt) {
        for (var p : policies) p.onTick(n, dt);
    }
}
