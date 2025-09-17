
package config;

import model.*;
import util.Vector2D;

import java.util.*;

public class StageConfig {
    List<SystemNode> initial = StageConfig.getStage1InitialNodes();
    List<SystemNode> given   = StageConfig.getStage1GivenNodes();
    List<SystemNode> all     = new java.util.ArrayList<>();
    public static List<SystemNode> getStage1InitialNodes() {
        List<SystemNode> nodes = new ArrayList<>();

        // سیستم مرجع
        SystemNode ref = new SystemNode("S1", new Vector2D(300, 250), true, "Start");
        ref.addOutputPort(new Port("S1_OUT1", Port.PortType.OUTPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), ref));
        ref.addInputPort (new Port("S1_IN1",  Port.PortType.INPUT,  ShapeType.SQUARE,  false, new Vector2D(0, 0), ref));
        ref.addInputPort (new Port("S1_IN2",  Port.PortType.INPUT,  ShapeType.TRIANGLE,false, new Vector2D(0, 0), ref));
        nodes.add(ref);

        // سیستم ۲
        SystemNode node2 = new SystemNode("S2", new Vector2D(600, 250), false, "Node 2");
        node2.addOutputPort(new Port("S2_OUT1", Port.PortType.OUTPUT, ShapeType.SQUARE,   false, new Vector2D(0, 0), node2));
        node2.addOutputPort(new Port("S2_OUT2", Port.PortType.OUTPUT, ShapeType.TRIANGLE, false, new Vector2D(0, 0), node2));
        node2.addInputPort (new Port("S2_IN1",  Port.PortType.INPUT,  ShapeType.SQUARE,   false, new Vector2D(0, 0), node2));

        nodes.add(node2);

        return nodes;
    }

    public static List<SystemNode> getStage1GivenNodes() {
        List<SystemNode> nodes = new ArrayList<>();

        // سیستم ۳
        SystemNode node3 = new SystemNode("S3", new Vector2D(0, 0), false, "Node 3");
        node3.addInputPort (new Port("S3_IN1",  Port.PortType.INPUT,  ShapeType.SQUARE, false, new Vector2D(0, 0), node3));
        node3.addOutputPort(new Port("S3_OUT1", Port.PortType.OUTPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), node3));
        nodes.add(node3);

        // سیستم ۴
        SystemNode node4 = new SystemNode("S4", new Vector2D(0, 0), false, "Node 4");
        node4.addInputPort (new Port("S4_IN1",  Port.PortType.INPUT,  ShapeType.SQUARE,   false, new Vector2D(0, 0), node4));
        node4.addInputPort (new Port("S4_IN2",  Port.PortType.INPUT,  ShapeType.TRIANGLE, false, new Vector2D(0, 0), node4));
        node4.addOutputPort(new Port("S4_OUT1", Port.PortType.OUTPUT, ShapeType.SQUARE,   false, new Vector2D(0, 0), node4));
        node4.addOutputPort(new Port("S4_OUT2", Port.PortType.OUTPUT, ShapeType.TRIANGLE, false, new Vector2D(0, 0), node4));
        nodes.add(node4);

        return nodes;
    }







    public static List<SystemNode> getStage2NewNodes() {
        List<SystemNode> nodes = new ArrayList<>();

        // VPN
        SystemNode vpn = new SystemNode("V1", new Vector2D(500, 120), SystemNode.NodeType.VPN, "VPN");
        vpn.setSprite("vpn",  100, 100);
        // offsets: (x,y) نسبی از بالا-چپ نود
        vpn.addInputPort (new Port("V1_IN_C",  Port.PortType.INPUT,  ShapeType.CIRCLE,  false, new Vector2D(50,  5),  vpn));  // بالا وسط
        vpn.addInputPort (new Port("V1_IN_SQ", Port.PortType.INPUT,  ShapeType.SQUARE,  false, new Vector2D( 5, 50),  vpn));  // چپ وسط
        vpn.addOutputPort(new Port("V1_OUT_T", Port.PortType.OUTPUT, ShapeType.TRIANGLE,false, new Vector2D(95, 50),  vpn));  // راست وسط
        nodes.add(vpn);

        // Saboteur
        SystemNode sab = new SystemNode("B1", new Vector2D(650, 120), SystemNode.NodeType.SABOTEUR, "Saboteur");
        sab.setSprite("saboteur", 100, 100);
        sab.addInputPort (new Port("B1_IN_C",  Port.PortType.INPUT,  ShapeType.CIRCLE,  false, new Vector2D(50,  5), sab));
        sab.addInputPort (new Port("B1_IN_SQ", Port.PortType.INPUT,  ShapeType.SQUARE,  false, new Vector2D( 5, 50), sab));
        sab.addOutputPort(new Port("B1_OUT_T", Port.PortType.OUTPUT, ShapeType.TRIANGLE,false, new Vector2D(95, 50), sab));
        sab.addOutputPort(new Port("B1_OUT_C", Port.PortType.OUTPUT, ShapeType.CIRCLE,  false, new Vector2D(50, 95), sab));
        nodes.add(sab);

        // Spy 1
        SystemNode spy1 = new SystemNode("SP1", new Vector2D(800, 120), SystemNode.NodeType.SPY, "Spy A");
        spy1.setSprite("spy", 100, 100);
        spy1.addInputPort (new Port("SP1_IN_T",  Port.PortType.INPUT,  ShapeType.TRIANGLE,false, new Vector2D(50,  5), spy1));
        spy1.addOutputPort(new Port("SP1_OUT_SQ", Port.PortType.OUTPUT, ShapeType.SQUARE, false, new Vector2D(95, 50), spy1));
        spy1.addOutputPort(new Port("SP1_OUT_C",  Port.PortType.OUTPUT, ShapeType.CIRCLE, false, new Vector2D(50, 95), spy1));
        nodes.add(spy1);

        // Spy 2
        SystemNode spy2 = new SystemNode("SP2", new Vector2D(950, 120), SystemNode.NodeType.SPY, "Spy B");
        spy2.setSprite("spy", 100, 100);
        spy2.addInputPort (new Port("SP2_IN_T",  Port.PortType.INPUT,  ShapeType.TRIANGLE,false, new Vector2D(50,  5), spy2));
        spy2.addOutputPort(new Port("SP2_OUT_SQ", Port.PortType.OUTPUT, ShapeType.SQUARE, false, new Vector2D(95, 50), spy2));
        nodes.add(spy2);

        // Norma
        SystemNode nC = new SystemNode("N_C1", new Vector2D(1100, 120), SystemNode.NodeType.NORMAL, "Normal-Circle");
       // nC.setSprite(ImageAssets.NORMAL_C, 100, 100);
        nC.addInputPort (new Port("NC1_IN_SQ", Port.PortType.INPUT,  ShapeType.SQUARE,  false, new Vector2D( 5, 50), nC));
        nC.addInputPort (new Port("NC1_IN_T",  Port.PortType.INPUT,  ShapeType.TRIANGLE,false, new Vector2D(50,  5), nC));
        nC.addOutputPort(new Port("NC1_OUT_C", Port.PortType.OUTPUT, ShapeType.CIRCLE,  false, new Vector2D(95, 50), nC));
        nodes.add(nC);

        return nodes;
    }


}

