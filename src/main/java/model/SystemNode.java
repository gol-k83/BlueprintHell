package model;

import util.Vector2D;

import java.util.*;

public class SystemNode {

    public void setPosition(Vector2D pos) {
        this.position=pos;
    }

    public enum NodeStatus {
        CONNECTED, DISCONNECTED

    }


    private final String id;                      // شناسه یکتا برای سیستم
    private final List<Port> inputPorts;           // لیست پورت‌های ورودی
    private final List<Port> outputPorts;          // لیست پورت‌های خروجی
    private final Queue<Packet> buffer;            // صف پکت‌های منتظر
    private final boolean isReference;             // آیا نود مرجع است؟
    private Vector2D position;                     // موقعیت گرافیکی
    private NodeStatus status;                     // وضعیت اتصال
    private int successfulPackets;                 // تعداد پکت‌های سالم برگشتی
    private int lostPackets;
    private final String displayName;
    // تعداد پکت‌های از دست رفته


    public SystemNode(String id, Vector2D position, boolean isReference,String displayName) {
        this.id = id;
        this.position = position;
        this.buffer = new LinkedList<>();
        this.inputPorts = new ArrayList<>();
        this.outputPorts = new ArrayList<>();
        this.isReference = isReference;
        this.status = NodeStatus.DISCONNECTED.DISCONNECTED;
        this.displayName = displayName;
    }


    public void addInputPort(Port port) {
        inputPorts.add(port);

    }

    public void addOutputPort(Port port) {

        outputPorts.add(port);
    }

    public boolean isFullyconnected() {
        return inputPorts.stream().allMatch(port -> port.getConnectedWire() != null)
                && outputPorts.stream().allMatch(port -> port.getConnectedWire() != null);
    }
    public boolean isFullyConnected() {
        return inputPorts.stream().allMatch(port -> port.getConnectedWire() != null)
                && outputPorts.stream().allMatch(port -> port.getConnectedWire() != null);
    }

    public void setStatus() {
        status = isFullyconnected() ? NodeStatus.CONNECTED : NodeStatus.DISCONNECTED;

    }

    public void receivePacket(Packet packet) {
        if (isReference) {
            if (!packet.isLost()) {
                successfulPackets++;
            } else {
                lostPackets++;
            }
        } else {
            sendPacket(packet);

        }

    }

    private void sendPacket(Packet packet) {
        Optional<Port> compatiblePort = outputPorts.stream()
                .filter(port -> !port.isOccupied() && port.isCompatibleWith(packet.getShape()))
                .findFirst();
        if (compatiblePort.isPresent()) {
            sendThroughPort(compatiblePort.get(), packet);
        } else {
            List<Port> freePorts = outputPorts.stream()
                    .filter(port -> !port.isOccupied())
                    .toList();
            if (!freePorts.isEmpty()) {
                Port randomPort = freePorts.get(new Random().nextInt(freePorts.size()));
                sendThroughPort(randomPort, packet);

            } else {
                if (buffer.size() < 5) {
                    buffer.add(packet);

                } else {
                    packet.applyNoise(packet.getOriginalSize());
                }
            }
        }
    }

    private void sendThroughPort(Port port, Packet packet) {
        port.setOccupied(true); // پورت مشغول می‌شود
        if (port.getConnectedWire() != null) {
            port.getConnectedWire().addPacket(packet); // ارسال به سیم متصل به پورت
        }
    }


    private void update() {
        if (buffer.isEmpty()) {
            sendPacket(buffer.poll());
        }
    }

    public String getId() {
        return id;
    }

    public List<Port> getInputPorts() {
        return inputPorts;
    }


    public List<Port> getOutputPorts() {
        return outputPorts;
    }

    public Vector2D getPosition() {
        return position;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public int getSuccessfulPackets() {
        return successfulPackets;
    }

    public int getLostPackets() {
        return lostPackets;
    }

    public boolean isReference() {
        return isReference;
    }
    public String getDisplayName() {
        return displayName;
    }
    @Override
    public String toString() {
        return "SystemNode{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", successfulPackets=" + successfulPackets +
                ", lostPackets=" + lostPackets +
                '}';
    }

}






















