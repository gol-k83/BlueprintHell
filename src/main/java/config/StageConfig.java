package config;

import model.*;
import util.Vector2D;

import java.util.*;

public class StageConfig {

    public static List<SystemNode> getStage1InitialNodes() {
        List<SystemNode> nodes = new ArrayList<>();

        //  سیستم مرجع
        SystemNode ref = new SystemNode("S1", new Vector2D(300, 250), true, "Start");
        ref.addOutputPort(new Port("S1_OUT1", Port.PortType.OUTPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), ref));
        ref.addInputPort(new Port("S1_IN1", Port.PortType.INPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), ref));
        ref.addInputPort(new Port("S1_IN2", Port.PortType.INPUT, ShapeType.TRIANGLE, false, new Vector2D(0, 0), ref));
        nodes.add(ref);

        // سیستم ۲
        SystemNode node2 = new SystemNode("S2", new Vector2D(600, 250), false, "Node 2");
        node2.addOutputPort(new Port("S2_OUT1", Port.PortType.OUTPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), node2));
        node2.addOutputPort(new Port("S2_OUT2", Port.PortType.OUTPUT, ShapeType.TRIANGLE, false, new Vector2D(0, 0), node2));
        node2.addInputPort(new Port("S2_IN1", Port.PortType.INPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), node2));
        nodes.add(node2);

        return nodes;
    }

    public static List<SystemNode> getStage1GivenNodes() {
        List<SystemNode> nodes = new ArrayList<>();

        //  سیستم ۳
        SystemNode node3 = new SystemNode("S3", new Vector2D(0, 0), false, "Node 3");
        node3.addInputPort(new Port("S3_IN1", Port.PortType.INPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), node3));
        node3.addOutputPort(new Port("S3_OUT1", Port.PortType.OUTPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), node3));
        nodes.add(node3);

        //  سیستم ۴
        SystemNode node4 = new SystemNode("S4", new Vector2D(0, 0), false, "Node 4");
        node4.addInputPort(new Port("S4_IN1", Port.PortType.INPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), node4));
        node4.addInputPort(new Port("S4_IN2", Port.PortType.INPUT, ShapeType.TRIANGLE, false, new Vector2D(0, 0), node4));
        node4.addOutputPort(new Port("S4_OUT1", Port.PortType.OUTPUT, ShapeType.SQUARE, false, new Vector2D(0, 0), node4));
        node4.addOutputPort(new Port("S4_OUT2", Port.PortType.OUTPUT, ShapeType.TRIANGLE, false, new Vector2D(0, 0), node4));
        nodes.add(node4);

        return nodes;
    }
}
