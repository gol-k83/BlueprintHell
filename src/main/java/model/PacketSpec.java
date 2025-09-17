//package model;
//
///** مشخصات پایهٔ هر پکت: خانواده، شکل منطقی (برای سازگاری پورت)، اندازه و سکهٔ ورود به سیستم. */
//public final class PacketSpec {
//
//    public enum PacketFamily { MESSAGE, PROTECTED, CONFIDENTIAL, MASSIVE }
//
//    private final PacketFamily family;
//    private final ShapeType shape;     // SQUARE, TRIANGLE, ANY
//    private final int sizeUnits;       // 1,2,3,4,6,8,10...
//    private final int coinsOnEnter;    // تعداد سکه هنگام ورود به سیستم
//
//    public PacketSpec(PacketFamily family, ShapeType shape, int sizeUnits, int coinsOnEnter) {
//        this.family = family;
//        this.shape = shape;
//        this.sizeUnits = sizeUnits;
//        this.coinsOnEnter = coinsOnEnter;
//    }
//
//    public PacketFamily family()     { return family; }
//    public ShapeType   shape()       { return shape; }
//    public int         sizeUnits()   { return sizeUnits; }
//    public int         coinsOnEnter(){ return coinsOnEnter; }
//}
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class PacketSpec {

    public enum PacketFamily { MESSAGE, PROTECTED, CONFIDENTIAL, MASSIVE }

    private final PacketFamily family;
    private final ShapeType shape;
    private final int sizeUnits;
    private final int coinsOnEnter;

    private final MovementStrategy movementStrategy;
    private final List<PacketComponent> components;
    private final LossPolicy lossPolicy;

    public PacketSpec(PacketFamily family,
                      ShapeType shape,
                      int sizeUnits,
                      int coinsOnEnter,
                      MovementStrategy movementStrategy,
                      List<PacketComponent> components,
                      LossPolicy lossPolicy) {
        this.family = family;
        this.shape = shape;
        this.sizeUnits = sizeUnits;
        this.coinsOnEnter = coinsOnEnter;
        this.movementStrategy = movementStrategy;
        this.components = (components != null) ? new ArrayList<>(components) : new ArrayList<>();
        this.lossPolicy = lossPolicy;
    }

    public PacketFamily family()        { return family; }
    public ShapeType   shape()          { return shape; }
    public int         sizeUnits()      { return sizeUnits; }
    public int         coinsOnEnter()   { return coinsOnEnter; }

    public MovementStrategy movement()  { return movementStrategy; }
    public List<PacketComponent> components() { return Collections.unmodifiableList(components); }
    public LossPolicy lossPolicy()      { return lossPolicy; }

    @Override
    public String toString() {
        return "PacketSpec{" +
                "family=" + family +
                ", shape=" + shape +
                ", sizeUnits=" + sizeUnits +
                ", coinsOnEnter=" + coinsOnEnter +
                ", movement=" + (movementStrategy != null ? movementStrategy.getClass().getSimpleName() : "null") +
                ", components=" + components.size() +
                ", lossPolicy=" + (lossPolicy != null ? lossPolicy.getClass().getSimpleName() : "null") +
                '}';
    }
}
