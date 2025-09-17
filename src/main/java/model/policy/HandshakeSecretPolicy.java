
package model.policy;

import model.Packet;
import model.ShapeType;
import model.SystemNode;
import model.behavior.BehaviorContext;
import model.PacketFactory;
import util.Dbg;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public final class HandshakeSecretPolicy implements ArrivalPolicy {
    private final long windowMillis;    // مثلا 10000 (10s)
    private final long cooldownMillis;  // مثلا 3000

    private static final class Times {
        long lastSquare = Long.MIN_VALUE;
        long lastTriangle = Long.MIN_VALUE;
        long lastSpawn = Long.MIN_VALUE;
    }

    private final Map<SystemNode, Times> times = new IdentityHashMap<>();

    public HandshakeSecretPolicy(long windowMillis, long cooldownMillis) {
        this.windowMillis  = Math.max(0, windowMillis);
        this.cooldownMillis = Math.max(0, cooldownMillis);
    }

    @Override
    public void onArrive(SystemNode node, model.Packet pkt, BehaviorContext ctx) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(pkt);

        ShapeType s = pkt.getShape();
        if (s != ShapeType.SQUARE && s != ShapeType.TRIANGLE) return;

        long now = System.currentTimeMillis();
        Times t = times.computeIfAbsent(node, n -> new Times());

        if (s == ShapeType.SQUARE) t.lastSquare = now; else t.lastTriangle = now;

        long other = (s == ShapeType.SQUARE) ? t.lastTriangle : t.lastSquare;
        boolean inWindow = (now - other) <= windowMillis;
        boolean cooled   = (now - t.lastSpawn) >= cooldownMillis;

        if (inWindow && cooled) {
            // اسپاون Secret(4)
            Packet secret = PacketFactory.conf4(node.getPosition());
            ctx.enqueueOrDrop(node, secret);
            t.lastSpawn = now;
            Dbg.log("POLICY", "HandshakeSecret: spawn CONF4 at node=%s", node.getId());
        }
    }

    @Override
    public void resetForNewRun() {
        times.clear();
        Dbg.log("POLICY", "HandshakeSecret: resetForNewRun()");
    }
}
