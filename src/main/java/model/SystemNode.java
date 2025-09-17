
package model;

import model.behavior.NodeBehavior;
import util.Vector2D;

import java.util.*;
import java.util.function.Predicate;

public class SystemNode {


    public enum NodeStatus { CONNECTED, DISCONNECTED }
    public enum PowerState { ON, OFF }
    public enum NodeType {
        NORMAL, REFERENCE, VPN, SPY, SABOTEUR, ANTITROJAN, DISTRIBUTE, MERGE
    }

    private static final double V_IN_MAX = 300.0;
    private static final double OFF_TIME = 3.0;
    private static final int    BUFFER_CAP = 5;


    private final String id;
    private final String displayName;
    private final List<Port> inputPorts  = new ArrayList<>();
    private final List<Port> outputPorts = new ArrayList<>();
    private final Queue<Packet> buffer   = new LinkedList<>();

    private Vector2D position;
    private NodeStatus status = NodeStatus.DISCONNECTED;

    private PowerState power = PowerState.ON;
    private double offTimer = 0.0;

    private int successfulPackets = 0;
    private int lostPackets = 0;
    private int coins = 0;

    private final NodeType type;
    private NodeBehavior behavior;
    private String spriteKey;
    private int    spriteW = 100;    // سایز رسم
    private int    spriteH = 100;

    private boolean stage1TriangleTriggerEnabled = false;
    private boolean stage1TriangleSpawnedOnce = false;


    public SystemNode(String id, Vector2D position, NodeType type, String displayName) {
        this.id = id;
        this.position = position;
        this.type = type;
        this.displayName = displayName;

    }

    public SystemNode(String id, Vector2D position, boolean isReference, String displayName) {
        this(id, position, isReference ? NodeType.REFERENCE : NodeType.NORMAL, displayName);
    }




    public void addInputPort(Port port)  { inputPorts.add(port); }
    public void addOutputPort(Port port) { outputPorts.add(port); }

    public boolean isFullyconnected() { return isFullyConnected(); }
    public boolean isFullyConnected() {
        return inputPorts.stream().allMatch(p -> p.getConnectedWire() != null)
                && outputPorts.stream().allMatch(p -> p.getConnectedWire() != null);
    }
    public void setStatus() {
        status = isFullyConnected() ? NodeStatus.CONNECTED : NodeStatus.DISCONNECTED;
    }


    public boolean isOn() { return power == PowerState.ON; }

    public void setOn(boolean value) {
        if (value) { power = PowerState.ON; offTimer = 0.0; }
        else       { power = PowerState.OFF; }
    }

    public void offFor(double seconds) {
        power = PowerState.OFF;
        offTimer = Math.max(offTimer, seconds);
    }


//    public void enableStage1TriangleTrigger(boolean enable){
//        this.stage1TriangleTriggerEnabled = enable;
//    }
//    public boolean isStage1TriangleTriggerEnabled(){ return stage1TriangleTriggerEnabled; }

//    public void setBehavior(NodeBehavior newBehavior) {
//        this.behavior = newBehavior;
//    }
public void setBehavior(NodeBehavior newBehavior) {
    this.behavior = newBehavior;
    util.Dbg.log("NODE", "setBehavior node=%s -> %s", id,
            newBehavior != null ? newBehavior.getClass().getSimpleName() : "null");
}

    public void receivePacket(Packet packet) {
        // هوک ورود
        util.Dbg.log("NODE", "receivePacket node=%s on=%s pkt=%s", id, isOn(), packet.getShape());
        packet.onEnterSystem(this, null);


        coins += packet.getCoins();

        // ی شهورودی خیلی تند OFF موقت
        double vin = (packet.getVelocity() != null) ? packet.getVelocity().length() : 0.0;
        if (vin > V_IN_MAX) offFor(OFF_TIME);

        // نود مرجع: برای
        if (type == NodeType.REFERENCE) {
            if (!packet.isLost()) successfulPackets++; else lostPackets++;
            return;
        }

        // //اسن رو برای دیباگ گذاشتم  قانون مخصوص مرحله ۱ (اختیاری و یک‌بار)
        if (stage1TriangleTriggerEnabled
                && !stage1TriangleSpawnedOnce
                && hasOutputOfShape(ShapeType.TRIANGLE)) {
            Packet tri = PacketFactory.msgTriangle(getPosition());
            Port triOut = pickBestPortFor(tri, /*strictCompat=*/true);
            if (triOut != null) {
                sendThroughPort(triOut, tri);
                stage1TriangleSpawnedOnce = true;
            }
        }


        if (!isOn()) { enqueueOrDrop(packet); return; }


        if (behavior != null) {
            behavior.processPacket(this, packet);
        } else {

            System.err.println("[WARN] Node " + id + " has NO behavior. Using legacy routing fallback.");
            sendPacketNormal(packet);
        }
    }


    boolean leadsToOnNode(Port out) {
        Wire w = out.getConnectedWire();
        if (w == null) return false;
        Port to = w.getToPort();
        SystemNode dst = (to != null) ? to.getParentNode() : null;
        return (dst != null) && dst.isOn();
    }
///شرایط رو بنویس یادت نره
    void sendPacketNormal(Packet packet) {
        //  سازگار + مقصد ON + سیم خالی
        Optional<Port> p1 = outputPorts.stream()
                .filter(p -> !p.isOccupied()
                        && p.isCompatibleWith(packet.getShape())
                        && leadsToOnNode(p)
                        && isWireIdle(p))
                .findFirst();
        if (p1.isPresent()) { sendThroughPort(p1.get(), packet); return; }

        // ) هر خروجی خالی به مقصد ON
        Optional<Port> p2 = outputPorts.stream()
                .filter(p -> !p.isOccupied() && leadsToOnNode(p) && isWireIdle(p))
                .findFirst();
        if (p2.isPresent()) { sendThroughPort(p2.get(), packet); return; }

        //  بافر
        enqueueOrDrop(packet);
    }

    boolean sendPacketSaboteur(Packet packet) {
        Optional<Port> bad = outputPorts.stream()
                .filter(p -> !p.isOccupied()
                        && !p.isCompatibleWith(packet.getShape())
                        && leadsToOnNode(p)
                        && isWireIdle(p))
                .findFirst();
        if (bad.isPresent()) { sendThroughPort(bad.get(), packet); return true; }
        return false;
    }

    private boolean isWireIdle(Port out){
        Wire w = out.getConnectedWire();
        return (w != null) && w.getPacketsOnWire().isEmpty();
    }

   // / انتخاب «بهترین» خروجی برای یک پکت
    Port pickBestPortFor(Packet p, boolean strictCompat){
        Predicate<Port> okDst = this::leadsToOnNode;
        Predicate<Port> idle  = po -> po.getConnectedWire()!=null &&
                po.getConnectedWire().getPacketsOnWire().isEmpty();

        for (Port out : outputPorts) {
            if (out == null) continue;
            if (!okDst.test(out) || !idle.test(out)) continue;
            boolean compat = out.isCompatibleWith(p.getShape());
            if (compat) return out;
        }
        if (!strictCompat) {
            for (Port out : outputPorts) {
                if (out == null) continue;
                if (!okDst.test(out) || !idle.test(out)) continue;
                return out;
            }
        }
        return null;
    }

    void sendThroughPort(Port port, Packet packet) {
        port.setOccupied(true);
        packet.onLeaveSystem(this, port);
        Wire w = port.getConnectedWire();
        if (w != null) {
            w.addPacket(packet);
        } else {
            port.setOccupied(false);
            enqueueOrDrop(packet);
        }
    }

    void enqueueOrDrop(Packet packet) {
        if (buffer.size() < BUFFER_CAP) buffer.add(packet);
        else { packet.applyNoise(packet.getOriginalSize()); lostPackets++; }
    }

    void incrementLost()    { lostPackets++; }
    void incrementSuccess() { successfulPackets++; }


    public void update(double dt) {
        if (power == PowerState.OFF) {
            if (offTimer > 0) {
                offTimer -= Math.max(0, dt);
                if (offTimer <= 0) { power = PowerState.ON; offTimer = 0; }
            }
            return;
        }

        if (!buffer.isEmpty()) {
            Packet head = buffer.peek();
            Port out = pickBestPortFor(head, /*strictCompat=*/true);
            if (out != null) {
                buffer.poll();
                sendThroughPort(out, head);
            }
        }
        if (behavior != null) behavior.onUpdate(this, dt);
    }


    public String getId() { return id; }
    public NodeType getType() { return type; }
    public List<Port> getInputPorts() { return inputPorts; }
    public List<Port> getOutputPorts() { return outputPorts; }
    public Vector2D getPosition() { return position; }
    public void setPosition(Vector2D pos) { this.position = pos; }
    public NodeStatus getStatus() { return status; }
    public boolean isReference() { return type == NodeType.REFERENCE; }
    public int getSuccessfulPackets() { return successfulPackets; }
    public int getLostPackets() { return lostPackets; }
    public int getCoinsTotal() { return coins; }
    public String getDisplayName() { return displayName; }
    public void purgeBuffer() { buffer.clear(); }

    public NodeBehavior getBehavior() {
        return behavior;
    }
    @Override public String toString() {
        return "SystemNode{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", power=" + power +
                ", successfulPackets=" + successfulPackets +
                ", lostPackets=" + lostPackets +
                ", coins=" + coins +
                '}';
    }


    private boolean hasOutputOfShape(ShapeType shape){
        for (Port p : outputPorts) {
            if (p != null && p.getCompatibleShape() == shape) return true;
        }
        return false;
    }
    public void setSprite(String key, int w, int h) { this.spriteKey = key; this.spriteW = w; this.spriteH = h; }
    public String getSpriteKey(){ return spriteKey; }
    public int getSpriteW(){ return spriteW; }
    public int getSpriteH(){ return spriteH; }
}
