



package controller;

import util.Constants;
import model.Port;
import model.Wire;

import java.util.ArrayList;
import java.util.List;

public class WireManager {
    private final List<Wire> wires;
    private double totalWireLengthUsed;

    public WireManager() {
        this.wires = new ArrayList<>();
        this.totalWireLengthUsed = 0;
    }


    public boolean tryConnectPorts(Port fromPort, Port toPort) {
        if (fromPort == null || toPort == null) return false;

        // جهت درست
        if (fromPort.getType() != Port.PortType.OUTPUT || toPort.getType() != Port.PortType.INPUT)
            return false;

        // نود مبدأ و مقصد یکی نباشندباید حواست باش بعدا درست کنی
        if (fromPort.getParentNode() == toPort.getParentNode())
            return false;

        // هر دو آزاد باشند (سیمِ یکتا)
        if (fromPort.getConnectedWire() != null || toPort.getConnectedWire() != null) {
            System.out.println(" یکی از پورت‌ها قبلاً متصل شده");
            return false;
        }

        // محدودیت طول
        double wireLength = fromPort.getPosition().distanceTo(toPort.getPosition());
        if (totalWireLengthUsed + wireLength > Constants.TOTAL_AVAILABLE_WIRE_LENGTH) {
            System.out.println(" طول سیم مجاز تمام شده!");
            return false;
        }


        Wire newWire = new Wire(fromPort, toPort);
        fromPort.setConnectedWire(newWire);
        toPort.setConnectedWire(newWire);
        wires.add(newWire);
        totalWireLengthUsed += wireLength;

        System.out.println("FromPort ID: " + fromPort.getId() + " → ToPort ID: " + toPort.getId());
        return true;
    }


    public Wire connectAndReturn(Port fromPort, Port toPort) {
        boolean success = tryConnectPorts(fromPort, toPort);
        System.out.println("connectAndReturn called. Wires size = " + wires.size());
        System.out.println("Total wire length now = " + getTotalWireLengthUsed());
        if (!success) return null;
        return wires.get(wires.size() - 1);
    }

    public void removeWire(Wire wire) {
        if (wire == null) return;
        if (wires.remove(wire)) {
            totalWireLengthUsed -= wire.getLength();
            if (wire.getFromPort() != null){
                wire.getFromPort().setConnectedWire(null);
                wire.getFromPort().setOccupied(false);
            }
            if (wire.getToPort()   != null){
                wire.getToPort().setConnectedWire(null);
                wire.getToPort().setOccupied(false);
            }
            System.out.println("Total wire length now = " + getTotalWireLengthUsed());
        }
    }

    public List<Wire> getWires() { return wires; }

    public double getRemainingWireLength() {
        return Constants.TOTAL_AVAILABLE_WIRE_LENGTH - totalWireLengthUsed;
    }

    public List<Wire> getAll() { return wires; }

    public double getTotalWireLengthUsed() {
        double sum = 0;
        for (Wire w : wires) sum += w.getLength();
        return sum;
    }
}
