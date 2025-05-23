package model;

import controller.StageManager;
import util.Vector2D;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Wire {
    private final Port fromPort;
    private final Port toPort;
    private final double length;
    private final List<Packet> packetsOnWire;
    private final Vector2D direction;

    public Wire(Port fromPort, Port toPort) {
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.length = fromPort.getPosition().distanceTo(toPort.getPosition());
        this.packetsOnWire = new LinkedList<>();
        this.direction = toPort.getPosition().subtract(fromPort.getPosition().normalize());
    }

    public Port getFromPort() {
        return fromPort;
    }

    public Port getToPort() {
        return toPort;
    }

    public double getLength() {
        return length;
    }

    public void addPacket(Packet packet) {
        if (!packetsOnWire.isEmpty()) return;
        packet.setPosition(toPort.getPosition());
        packet.setVelocity(direction.multiply(packet.getVelocity().length()));
        packetsOnWire.add(packet);

    }


    public void update(double deltaTime) {
        // ثبت پکت‌های گم‌شده
        for (Packet packet : packetsOnWire) {
            if (packet.isLost()) {
                StageManager.getInstance().getTracker().addLost(packet);
            }
        }
        packetsOnWire.removeIf(Packet::isLost);

        // پردازش تحویل پکت‌ها
        List<Packet> delivered = new LinkedList<>();
        for (Packet packet : packetsOnWire) {
            packet.updateMovement(deltaTime);
            double traveled = packet.getPosition().distanceTo(toPort.getPosition());
            if (traveled >= length) {
                delivered.add(packet);
                StageManager.getInstance().getTracker().addDelivered(packet);
            }
        }

        for (Packet packet : delivered) {
            packetsOnWire.remove(packet);
            toPort.setOccupied(false);
            toPort.getParentNode().receivePacket(packet);
        }
    }


}
