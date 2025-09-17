
//package model;
//import util.Vector2D;
//import model.ShapeType;
//import java.awt.*;
//
//public class Port{
//    public enum PortType{
//        INPUT,OUTPUT
//    }
////    public enum ShapeType{
////        SQUARED,TRIANGLE
////    }
//
//    private final String id;
//    private final PortType type;
//    private final ShapeType compatibleShape;
//    private boolean occupied;
//    private Vector2D position;
//    private Wire connectedWire;
//     private SystemNode parentNode;
//public Port(String id,PortType type,ShapeType compatibleShape,boolean occupied,Vector2D position,SystemNode parentNode){
//
//    this.id = id;
//    this.type = type;
//    this.compatibleShape = compatibleShape;
//    this.occupied = occupied;
//    this.position = position;
//    this.occupied = false;
//    this.parentNode= parentNode;
//}
//public String getId(){
//    return id;
//}
//
//public PortType getType(){
//    return type;
//}
//public ShapeType getCompatibleShape(){
//    return compatibleShape;
//}
//public boolean isOccupied(){
//    return occupied;
//
//}
//
//
//    public Vector2D getPosition() {
//        return position;
//    }
//
//    public Wire getConnectedWire() {
//        return connectedWire;
//    }
//    //setters
//
//    public void setOccupied(boolean occuiped){
//    this.occupied=occuiped;
//    }
//
//    public void setPosition(Vector2D position) {
//        this.position = position;
//    }
//
//    public void setConnectedWire(Wire connectedWire) {
//        this.connectedWire = connectedWire;
//    }
//
//    // سازگاری
////    public boolean isCompatibleWith(model.ShapeType packetShape) {
////        return this.compatibleShape == packetShape;
////    }
//
//    public boolean isCompatibleWith(ShapeType packetShape) {
//        return this.compatibleShape == packetShape
//                || this.compatibleShape == ShapeType.ANY
//                || packetShape == ShapeType.ANY;
//    }
//    public SystemNode getParentNode() {
//        return parentNode;
//    }
//    public void setParentNode(SystemNode parentNode) {
//        this.parentNode = parentNode;
//    }
//    @Override
//    public String toString() {
//        return "Port{" +
//                "id='" + id + '\'' +
//                ", type=" + type +
//                ", compatibleShape=" + compatibleShape +
//                ", occupied=" + occupied +
//                ", position=" + position +
//                '}';
//    }
//
//




package model;

import util.Vector2D;


public class Port {

    public enum PortType { INPUT, OUTPUT }

    private final String id;
    private final PortType type;
    private final ShapeType compatibleShape;

    private boolean occupied;
    private Vector2D position;         // مختصات جهانی
    private Wire connectedWire;
    private SystemNode parentNode;

    public Port(String id,
                PortType type,
                ShapeType compatibleShape,
                boolean occupied,
                Vector2D position,
                SystemNode parentNode) {

        this.id = id;
        this.type = type;
        this.compatibleShape = compatibleShape;


        this.occupied = occupied;


        this.position = (position != null) ? position : new Vector2D(0, 0);

        this.connectedWire = null;
        this.parentNode = parentNode;
    }

    // ---------------- Getters ----------------
    public String getId() { return id; }
    public PortType getType() { return type; }
    public ShapeType getCompatibleShape() { return compatibleShape; }
    public boolean isOccupied() { return occupied; }
    public Vector2D getPosition() { return position; }
    public Wire getConnectedWire() { return connectedWire; }
    public SystemNode getParentNode() { return parentNode; }

    // ---------------- Setters ----------------
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
    public void setPosition(Vector2D position) { this.position = (position != null) ? position : this.position; }
    public void setConnectedWire(Wire connectedWire) { this.connectedWire = connectedWire; }
    public void setParentNode(SystemNode parentNode) { this.parentNode = parentNode; }

    // ---------------- Helpers ----------------
    public boolean isInput()  { return type == PortType.INPUT; }
    public boolean isOutput() { return type == PortType.OUTPUT; }

    public boolean isCompatibleWith(model.ShapeType packetShape) {
        if (this.compatibleShape == null || packetShape == null) return false;
        return this.compatibleShape == model.ShapeType.ANY
                || packetShape == model.ShapeType.ANY
                || this.compatibleShape == packetShape;
    }


    public void attachWire(Wire wire) { this.connectedWire = wire; }
    public void clearWire()           { this.connectedWire = null; }

    public Vector2D getAbsolutePosition() {
        Vector2D nodePos = (parentNode != null) ? parentNode.getPosition() : new Vector2D(0,0);
        Vector2D local   = getPosition(); // همین الان داری
        return new Vector2D(nodePos.getX() + local.getX(), nodePos.getY() + local.getY());
    }

    @Override
    public String toString() {
        return "Port{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", compatibleShape=" + compatibleShape +
                ", occupied=" + occupied +
                ", position=" + position +
                '}';
    }
}














