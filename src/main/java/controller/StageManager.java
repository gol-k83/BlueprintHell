package controller;

import model.SystemNode;
import model.Packet;
import model.Wire;

import java.util.ArrayList;
import java.util.List;

public class StageManager {

    private static final StageManager instance = new StageManager();

    private final List<SystemNode> systemNodes = new ArrayList<>();
    private final List<Wire> wires = new ArrayList<>();
    private final PacketTracker tracker = new PacketTracker();

    private int currentStageNumber = 1;

    private StageManager() {
    }

    public static StageManager getInstance() {
        return instance;
    }

    public void loadStage(int stageNumber) {
        systemNodes.clear();
        wires.clear();
        tracker.reset();
        currentStageNumber = stageNumber;

        // اینجا مرحله رو با توجه به stageNumber لود می‌کنی
        // مثلاً: StageConfig.loadStage(stageNumber, systemNodes, wires);
    }

    public void addSystemNode(SystemNode node) {
        systemNodes.add(node);
    }

    public void addWire(Wire wire) {
        wires.add(wire);
    }

    public List<SystemNode> getSystemNodes() {
        return systemNodes;
    }

    public List<Wire> getWires() {
        return wires;
    }

    public PacketTracker getTracker() {
        return tracker;
    }

    public int getCurrentStageNumber() {
        return currentStageNumber;
    }

    public void resetStage() {
        systemNodes.clear();
        wires.clear();
        tracker.reset();
    }

    public double getPacketLossRatio() {
        return tracker.getPacketLossRatio();
    }

    public boolean isStageFailed() {
        return getPacketLossRatio() > 0.5;
    }
}
