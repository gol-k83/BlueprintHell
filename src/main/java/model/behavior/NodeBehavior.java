package model.behavior;

import model.Packet;
import model.SystemNode;


public interface NodeBehavior {

    void processPacket(SystemNode node, Packet packet);


    default void onPowerStateChanged(SystemNode node, boolean isOn) {}


    default void onUpdate(SystemNode node, double dt) {}
}
