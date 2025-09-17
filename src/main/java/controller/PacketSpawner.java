
package controller;

import model.*;
import util.Vector2D;

import java.util.*;

public class PacketSpawner {
    private static final PacketSpawner INSTANCE = new PacketSpawner();
    public static PacketSpawner getInstance() { return INSTANCE; }


    private boolean runArmed = false;
    private boolean runInitDone = false;


    public enum Kind {
        MSG_SQUARE,
        MSG_TRIANGLE,
        MSG_UNIT,
        PROTECTED,
        CONF4, CONF6,
        MASS8, MASS10
    }


    private static final class Task {
        final SystemNode node;
        final Kind kind;
        final double interval;
        int remaining;
        double timer;

        Task(SystemNode n, Kind k, double interval, int remaining, double startDelay) {
            node = n; kind = k; this.interval = interval; this.remaining = remaining; this.timer = Math.max(0, startDelay);
        }
    }

    private final List<Task> tasks = new ArrayList<>();

    public void clearAll() { tasks.clear(); runArmed = false; }
    public int activeTasks() { return tasks.size(); }

    // یک‌بار با تأخیر
    public void scheduleOnce(SystemNode node, Kind kind, double startDelaySec) {
        ensureRunInit();
        Objects.requireNonNull(node, "node");
        tasks.add(new Task(node, kind, 0.0, 1, startDelaySec));
        System.out.println("[Spawner] scheduleOnce kind=" + kind + " node=" + safe(node));
    }

    // دوره‌ای فک نکنم استفاده بشه
    public void schedulePeriodic(SystemNode node, Kind kind, double intervalSec, int maxCount) {
        ensureRunInit();
        Objects.requireNonNull(node, "node");
        if (intervalSec <= 0) throw new IllegalArgumentException("intervalSec must be > 0");
        tasks.add(new Task(node, kind, intervalSec, maxCount, 0));
        System.out.println("[Spawner] schedulePeriodic kind=" + kind + " every=" + intervalSec + "s node=" + safe(node));
    }


    public void update(double dt) {
        if (dt <= 0 || tasks.isEmpty()) return;

        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.node == null) { it.remove(); continue; }

            t.timer -= dt;
            if (t.timer > 0) continue;

            boolean ok = spawnFromNode(t.node, t.kind);
            if (ok && t.remaining > 0) t.remaining--;

            if (t.interval > 0) {
                if (t.remaining != 0) {
                    t.timer = t.interval;
                } else {
                    System.out.println("[Spawner] task done (periodic): " + t.kind);
                    it.remove();
                }
            } else {
                System.out.println("[Spawner] task done (one-shot): " + t.kind);
                it.remove();
            }
        }
        if (tasks.isEmpty() && runArmed) {
            util.Dbg.log("RUN", "run ended");
            runArmed = false;
        }
    }


    public boolean spawnNow(SystemNode node, Kind kind) {
        ensureRunInit();
        if (node == null) { System.out.println("[Spawner] spawnNow: node==null"); return false; }
        return spawnFromNode(node, kind);
    }


    private boolean spawnFromNode(SystemNode node, Kind kind) {
        if (!node.isOn()) {
            System.out.println("[Spawner] node OFF: " + safe(node));
            return false;
        }
        Packet packet = buildPacket(kind, node.getPosition());
        if (packet == null) { System.out.println("[Spawner] buildPacket==null for " + kind); return false; }

        Port best = null, firstWithWire = null;
        for (Port p : node.getOutputPorts()) {
            if (p == null) continue;
            Wire w = p.getConnectedWire();
            if (w == null) continue;
            if (firstWithWire == null) firstWithWire = p;

            SystemNode dst = (w.getToPort() != null) ? w.getToPort().getParentNode() : null;
            if (dst == null || !dst.isOn()) continue;

            if (w.getPacketsOnWire().isEmpty() && p.isCompatibleWith(packet.getShape())) {
                best = p; break;
            }
            if (best == null && w.getPacketsOnWire().isEmpty()) best = p;
        }
        if (best == null) {
            for (Port p : node.getOutputPorts()) {
                Wire w = (p != null) ? p.getConnectedWire() : null;
                if (w != null && w.getPacketsOnWire().isEmpty()) { best = p; break; }
            }
        }
        if (best == null) { System.out.println("[Spawner] no usable output wire"); return false; }

        Wire wire = best.getConnectedWire();
        if (wire == null || !wire.getPacketsOnWire().isEmpty()) { System.out.println("[Spawner] wire busy/absent"); return false; }

        best.setOccupied(true);
        packet.onLeaveSystem(node, best);
        wire.addPacket(packet);

        System.out.println("[Spawner] SPAWN OK kind=" + kind + " viaPort=" + best.getId()
                + " dst=" + safe(wire.getToPort() != null ? wire.getToPort().getParentNode() : null));
        return true;
    }


    private Packet buildPacket(Kind kind, Vector2D pos) {
        Vector2D p = (pos != null) ? pos : new Vector2D(0, 0);
        return switch (kind) {
            case MSG_SQUARE  -> PacketFactory.msgSquare(p);
            case MSG_TRIANGLE-> PacketFactory.msgTriangle(p);
            case MSG_UNIT    -> PacketFactory.msgUnit(p);
            case PROTECTED   -> PacketFactory.protFrom(PacketFactory.msgSquare(p), p);
            case CONF4       -> PacketFactory.conf4(p);
            case CONF6       -> PacketFactory.conf6(p);
            case MASS8       -> PacketFactory.mass8(p);
            case MASS10      -> PacketFactory.mass10(p);
        };
    }

    private void ensureRunInit() {
        if (!runArmed) {
            util.Dbg.log("RUN", "init run → reset policies");
            controller.StageManager.getInstance().resetPoliciesForNewRun();
            runArmed = true;
        }
    }

    public void armForNextRunReset() { runInitDone = false; }

    private static String safe(SystemNode n) {
        try { return (n != null ? (n.getId() != null ? n.getId() : n.toString()) : "null"); }
        catch (Throwable t) { return "node"; }
    }
}
