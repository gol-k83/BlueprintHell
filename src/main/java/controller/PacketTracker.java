package controller;

import model.Packet;

import java.util.ArrayList;
import java.util.List;

public class PacketTracker {
    private final List<Packet> lostPackets = new ArrayList<>();
    private final List<Packet> deliveredPackets = new ArrayList<>();

    public void addLost(Packet packet) {
        lostPackets.add(packet);
    }

    public void addDelivered(Packet packet) {
        deliveredPackets.add(packet);
    }

    public int getTotalLost() {
        return lostPackets.size();
    }

    public int getTotalDelivered() {
        return deliveredPackets.size();
    }

    public double getPacketLossRatio() {
        int total = getTotalLost() + getTotalDelivered();
        if (total == 0) return 0;
        return (double) getTotalLost() / total;
    }

    public void reset() {
        lostPackets.clear();
        deliveredPackets.clear();
    }
}
