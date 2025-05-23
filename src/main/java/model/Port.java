
package model;
import util.Vector2D;
import model.ShapeType;
import java.awt.*;

public class Port{
    public enum PortType{
        INPUT,OUTPUT
    }
//    public enum ShapeType{
//        SQUARED,TRIANGLE
//    }
    private final String id;               // شناسه یکتا
    private final PortType type;            // ورودی یا خروجی
    private final ShapeType compatibleShape; // شکل پذیرش (مربع یا مثلث)
    private boolean occupied;               // آیا در حال حاضر اشغال شده؟
    private Vector2D position;              // موقعیت گرافیکی روی صفحه
    private Wire connectedWire;             // سیم متصل به این پورت (nullable)
     private SystemNode parentNode;
public Port(String id,PortType type,ShapeType compatibleShape,boolean occupied,Vector2D position,SystemNode parentNode){

    this.id = id;
    this.type = type;
    this.compatibleShape = compatibleShape;
    this.position = position;
    this.occupied = false;
    this.parentNode= parentNode;
}
public String getId(){
    return id;
}

public PortType getType(){
    return type;
}
public ShapeType getCompatibleShape(){
    return compatibleShape;
}
public boolean isOccupied(){
    return occupied;

}


    public Vector2D getPosition() {
        return position;
    }

    public Wire getConnectedWire() {
        return connectedWire;
    }
    //setters

    public void setOccupied(boolean occuiped){
    this.occupied=occuiped;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setConnectedWire(Wire connectedWire) {
        this.connectedWire = connectedWire;
    }

    // آیا این پورت با یک پکت مشخص سازگار است؟
    public boolean isCompatibleWith(model.ShapeType packetShape) {
        return this.compatibleShape == packetShape;
    }

    public SystemNode getParentNode() {
        return parentNode;
    }
    public void setParentNode(SystemNode parentNode) {
        this.parentNode = parentNode;
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