
package model.policy;

import model.Packet;
import model.Port;
import model.ShapeType;
import model.SystemNode;
import model.behavior.BehaviorContext;
import model.PacketFactory;
import util.Dbg;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public final class SecretOutputPolicy implements ArrivalPolicy {
    private final int maxPerNode; // تعداد Run

    private final Map<SystemNode, Integer> used = new IdentityHashMap<>();

    public SecretOutputPolicy(int maxPerNode) {
        this.maxPerNode = Math.max(0, maxPerNode);
    }

    @Override
    public void onArrive(SystemNode node, Packet pkt, BehaviorContext ctx) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(pkt);
        if (maxPerNode == 0) return;
        if (pkt.getShape() != ShapeType.SQUARE) return;

        int inSquare  = (int) node.getInputPorts().stream()
                .filter(p -> p.getType() == Port.PortType.INPUT && p.getCompatibleShape() == ShapeType.SQUARE)
                .count();
        int outSquare = (int) node.getOutputPorts().stream()
                .filter(p -> p.getType() == Port.PortType.OUTPUT && p.getCompatibleShape() == ShapeType.SQUARE)
                .count();

        if (inSquare == 1 && outSquare == 1) {
            int u = used.getOrDefault(node, 0);
            if (u >= maxPerNode) return;
            Packet secret = PacketFactory.conf4(node.getPosition());
            ctx.enqueueOrDrop(node, secret);
            used.put(node, u + 1);
            Dbg.log("POLICY", "SecretOutput: spawn CONF4 at node=%s (used=%d)", node.getId(), u + 1);
        }
    }

    @Override
    public void resetForNewRun() {
        used.clear();
        Dbg.log("POLICY", "SecretOutput: resetForNewRun()");
    }
}
