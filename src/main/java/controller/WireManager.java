package controller;
import util.Constants;
import model.Port;
import model.Wire;

import java.util.ArrayList;
import java.util.List;

public class WireManager {
    private final List<Wire> wires;
    private double totalWireLengthUsed;
    //private final List<WireView> wires = new ArrayList<>();

    public WireManager() {
        this.wires = new ArrayList<>();
        this.totalWireLengthUsed = 0;

    }

    public boolean tryConnectPorts(Port toPort, Port fromPort) {
        if (toPort.getType() != Port.PortType.OUTPUT || fromPort.getType() != Port.PortType.INPUT)
            return false;
        if (toPort.getParentNode() == fromPort.getParentNode())
            return false;
        if (fromPort.getConnectedWire() != null || toPort.getConnectedWire() != null)
            return false;
        if (!fromPort.isCompatibleWith(toPort.getCompatibleShape()))
            return false;


        double wireLength = fromPort.getPosition().distanceTo(toPort.getPosition());
        if (totalWireLengthUsed + wireLength > Constants.TOTAL_AVAILABLE_WIRE_LENGTH)
            return false;
        System.out.println("❌ طول سیم مجاز تمام شده!");





        Wire newWire = new Wire(fromPort, toPort);
        fromPort.setConnectedWire(newWire);
        toPort.setConnectedWire(newWire);
        wires.add(newWire);
        totalWireLengthUsed += wireLength;
        return true;
    }

    public void removeWire(Wire wire) {
        wires.remove(wire);
        totalWireLengthUsed -= wire.getLength();
        wire.getFromPort().setConnectedWire(null);
        wire.getToPort().setConnectedWire(null);
    }

    public List<Wire> getWires() {
        return wires;
    }

    public double getRemainingWireLength() {
        return Constants.TOTAL_AVAILABLE_WIRE_LENGTH - totalWireLengthUsed;
    }


    public List<Wire> getAll() {
        return wires;
    }
}

