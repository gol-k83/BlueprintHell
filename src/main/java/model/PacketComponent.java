package model;


public interface PacketComponent {
    default void onSpawn(Packet p) {}
    default void onUpdate(Packet p, double dt) {}

    default void onWireEnter(Wire wire, Packet p) {}
    default void onWireExit(Wire wire, Packet p) {}

    default void onCollision(Packet self, Packet other) {}

    default void onEnterSystem(Packet p, SystemNode node, Port inPort) {}
    default void onLeaveSystem(Packet p, SystemNode node, Port outPort) {}
}
