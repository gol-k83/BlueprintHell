
package model;

import model.behavior.BehaviorContext;
import model.PacketSpec;
import model.ShapeType;
import model.Port;

import java.util.List;

public final class GameBehaviorContext implements BehaviorContext {

    @Override
    public boolean isSystemActive(SystemNode n) {
        return n != null && n.isOn();
    }

    @Override
    public boolean leadsToActiveSystem(Port p) {
        if (p == null) {
            util.Dbg.log("CTX", "leadsToActive out=null -> false");
            return false;
        }
        Wire w = p.getConnectedWire();
        if (w == null) {
            util.Dbg.log("CTX", "leadsToActive out=%s wire=null -> false", p.getId());
            return false;
        }
        Port to = w.getToPort();
        SystemNode dst = (to != null) ? to.getParentNode() : null;
        boolean res = dst != null && dst.isOn();
        util.Dbg.log("CTX", "leadsToActive out=%s -> %s", p.getId(), res);
        return res;
    }

    @Override
    public boolean isWireIdle(Port p) {
        if (p == null) return false;
        Wire w = p.getConnectedWire();
        boolean res = (w != null) && w.getPacketsOnWire().isEmpty();
        util.Dbg.log("CTX", "isWireIdle out=%s -> %s", p.getId(), res);
        return res;
    }

    @Override
    public void spawnThrough(Port out, ShapeType shape) {
        util.Dbg.log("CTX", "spawnThrough out=%s shape=%s", out.getId(), shape);
        SystemNode n = out.getParentNode();
        Packet s = switch (shape) {
            case SQUARE -> PacketFactory.msgSquare(n.getPosition());
            case TRIANGLE -> PacketFactory.msgTriangle(n.getPosition());
            case CIRCLE -> PacketFactory.msgUnit(n.getPosition());
            default -> null;
        };
        if (s != null) n.sendThroughPort(out, s);
    }

    @Override
    public void spawnAt(SystemNode n, ShapeType shape) {
        util.Dbg.log("CTX", "spawnAt node=%s shape=%s", n.getId(), shape);
        Packet s = switch (shape) {
            case SQUARE -> PacketFactory.msgSquare(n.getPosition());
            case TRIANGLE -> PacketFactory.msgTriangle(n.getPosition());
            case CIRCLE -> PacketFactory.msgUnit(n.getPosition());
            default -> null;
        };
        if (s != null) n.enqueueOrDrop(s);
    }

    @Override
    public void sendThrough(Port out, Packet p) {
        util.Dbg.log("CTX", "sendThrough out=%s shape=%s", out.getId(), p.getShape());
        out.getParentNode().sendThroughPort(out, p);
    }

    @Override
    public void enqueueOrDrop(SystemNode n, Packet p) {
        if (n != null && p != null) n.enqueueOrDrop(p);
    }

   //////برای فاز دو
    @Override
    public SystemNode pickOtherSpy(SystemNode self) {
        return null;
    }


    @Override
    public void teleport(Packet p, SystemNode target) {
        if (p == null || target == null) return;
        if (!target.isOn()) {
            util.Dbg.log("CTX", "teleport -> dst OFF: %s", target.getId());
            return;
        }
        util.Dbg.log("CTX", "teleport %s -> %s", p.getShape(), target.getId());


        try { p.setPosition(target.getPosition()); } catch (Throwable ignore) {}

        // تحویل استاندارد: کل منطق ورود
        target.receivePacket(p);
    }


    @Override
    public Packet toProtected(Packet base) {
        return base;
    }

    @Override
    public void destroy(Packet p) {
        if (p != null) p.applyNoise(p.getOriginalSize());
    }

    @Override
    public boolean isProtected(Packet p) {
        return p != null && p.getSpec() != null && p.getSpec().family() == PacketSpec.PacketFamily.PROTECTED;
    }

    @Override
    public boolean isSecret(Packet p) {
        return p != null && p.getSpec() != null && p.getSpec().family() == PacketSpec.PacketFamily.CONFIDENTIAL;
    }

    @Override
    public boolean isBulk(Packet p) {
        return p != null && p.getSpec() != null && p.getSpec().family() == PacketSpec.PacketFamily.MASSIVE;
    }

    @Override
    public void addNoise(Packet p, int amount) {
        if (p != null && amount > 0) p.applyNoise(amount);
    }

    @Override
    public boolean tryTrojanize(Packet p, double probability) {
        return false;
    }

    @Override
    public List<Packet> findPacketsInRadius(SystemNode center, double radius) {
        return List.of();
    }

    @Override
    public List<Packet> splitToBits(Packet bulk) {
        return List.of();
    }

    @Override
    public Packet tryMergeBits(SystemNode atNode) {
        return null;
    }
    ///راهکار
//: این پیاده‌سازی vpnDown فعلاً سراسری همهٔ Protectedها را برمی‌گرداند. اگر بعداً بخایییی فقط Protectedهایی کهخاص یک وی پی ان  ساخته را برگردانی، یک فیلد ساده (مثلاً sourceVpnId) داخل Packet (یا Component) اضافه کن و همین‌جا فیلتر بزن.
    @Override
    public void vpnDown(SystemNode vpnNode) {
        for (Wire w : controller.StageManager.getInstance().getWires()) {
            var it = w.getPacketsOnWire().iterator();
            java.util.List<Packet> toAdd = new java.util.ArrayList<>();
            while (it.hasNext()) {
                Packet p = it.next();
                if (isProtected(p)) {
                    ShapeType s = p.getShape();
                    Packet back = switch (s) {
                        case SQUARE -> PacketFactory.msgSquare(p.getPosition());
                        case TRIANGLE -> PacketFactory.msgTriangle(p.getPosition());
                        case CIRCLE -> PacketFactory.msgUnit(p.getPosition());
                        default -> null;
                    };
                    if (back != null) {
                        it.remove();
                        toAdd.add(back);
                    }
                }
            }
            for (Packet np : toAdd) w.addPacket(np);
        }
        util.Dbg.log("CTX", "vpnDown(%s): protected→message reverted", (vpnNode != null ? vpnNode.getId() : "*"));
    }

    @Override
    public void replaceInPlace(Packet oldPkt, Packet newPkt) {
        if (oldPkt == null || newPkt == null) return;
        for (Wire w : controller.StageManager.getInstance().getWires()) {
            var list = w.getPacketsOnWire();
            if (list.remove(oldPkt)) {

                w.addPacket(newPkt);
                util.Dbg.log("CTX", "replaceInPlace on wire=%s", w.getId());
                return;
            }
        }
    }
}