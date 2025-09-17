
package model.policy;

import model.Packet;
import model.Port;
import model.SystemNode;
import model.behavior.BehaviorContext;


public interface ArrivalPolicy {


    void onArrive(SystemNode node, Packet pkt, BehaviorContext ctx);


    default void onRouted(SystemNode node, Packet pkt, Port chosen, BehaviorContext ctx) {}


    default void onTick(SystemNode node, double dt) {}


    default void resetForNewRun() {}
}
