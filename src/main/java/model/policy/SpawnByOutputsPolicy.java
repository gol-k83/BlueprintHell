
package model.policy;

import model.Packet;
import model.Port;
import model.ShapeType;
import model.SystemNode;
import model.behavior.BehaviorContext;

import java.util.*;


public final class SpawnByOutputsPolicy implements ArrivalPolicy {

    private final boolean onlyWhenDifferentShape;
    private final boolean requireIdleWire;
    private final boolean requireDestOn;
    private final int     maxPerPort;

    private final boolean blockReversePair;
    private final boolean oncePerPairGlobal;

    // شمارندهٔ اسپاون برای هر پورت ( برای کل مرحلهکوکب )
    private final Map<Port, Integer> spawnedPerPort = new IdentityHashMap<>();


    private final Set<String> firedPairs = new HashSet<>();

    public SpawnByOutputsPolicy(boolean onlyWhenDifferentShape,
                                boolean requireIdleWire,
                                boolean requireDestOn,
                                int maxPerPort,
                                boolean blockReversePair,
                                boolean oncePerPairGlobal) {
        this.onlyWhenDifferentShape = onlyWhenDifferentShape;
        this.requireIdleWire        = requireIdleWire;
        this.requireDestOn          = requireDestOn;
        this.maxPerPort             = Math.max(0, maxPerPort);
        this.blockReversePair       = blockReversePair;
        this.oncePerPairGlobal      = oncePerPairGlobal;
    }

    @Override
    public void onArrive(SystemNode node, Packet pkt, BehaviorContext ctx) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(pkt);
        if (maxPerPort == 0) return;

        final ShapeType inShape = pkt.getShape();
        util.Dbg.log("POLICY", "onArrive node=%s in=%s", node.getId(), inShape);

        for (Port out : node.getOutputPorts()) {
            if (out == null) continue;

            // شکل خروجی معتبر
            ShapeType outShape = out.getCompatibleShape();
            if (outShape == null || outShape.isWildcard()) continue;

            int used = spawnedPerPort.getOrDefault(out, 0);
            boolean hasWire = (out.getConnectedWire() != null);

            boolean destOn = !requireDestOn || ctx.leadsToActiveSystem(out);         // also implies hasWire
            boolean idle   = !requireIdleWire || (hasWire && ctx.isWireIdle(out));
            boolean diff   = !onlyWhenDifferentShape || (outShape != inShape);

            util.Dbg.log("POLICY",
                    "check out=%s shape=%s used=%d hasWire=%s destOn=%s idle=%s diff=%s",
                    out.getId(), outShape, used, hasWire, destOn, idle, diff);

            if (used >= maxPerPort)                  { util.Dbg.log("POLICY","skip cap per-port"); continue; }
            if (requireDestOn && !destOn)            { util.Dbg.log("POLICY","skip destOff");      continue; }
            if (requireIdleWire && (!hasWire || !idle)) { util.Dbg.log("POLICY","skip not idle");  continue; }
            if (onlyWhenDifferentShape && !diff)     { util.Dbg.log("POLICY","skip same shape");   continue; }

            String pair    = inShape.name() + "->" + outShape.name();
            String reverse = outShape.name() + "->" + inShape.name();
            if (oncePerPairGlobal && firedPairs.contains(pair))    { util.Dbg.log("POLICY","skip pair fired %s", pair);    continue; }
            if (blockReversePair   && firedPairs.contains(reverse)) { util.Dbg.log("POLICY","skip reverse %s", reverse);   continue; }


            if (hasWire) {
                util.Dbg.log("POLICY", "SPAWN_THROUGH out=%s as=%s", out.getId(), outShape);
                ctx.spawnThrough(out, outShape);
            } else {
                util.Dbg.log("POLICY", "SPAWN_AT node=%s as=%s (no wire)", node.getId(), outShape);
                ctx.spawnAt(node, outShape);
            }

            spawnedPerPort.put(out, used + 1);
            firedPairs.add(pair);
        }
    }

    @Override
    public void resetForNewRun() {
        spawnedPerPort.clear();
        firedPairs.clear();
        util.Dbg.log("POLICY", "resetForNewRun(): state cleared");
    }
}
