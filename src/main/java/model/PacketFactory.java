
package model;

import util.Vector2D;
import view.Skin;

import java.util.List;

public final class PacketFactory {
    private PacketFactory(){}

    private static final double V_SQ_COMP   = 80.0,  V_SQ_INCOMP = 160.0;
    private static final double V_TR_COMP   = 120.0, V_TR_START  = 80.0, A_TR = 60.0, V_TR_MAX = 220.0;
    private static final double V_UNIT0     = 70.0,  A_UNIT = 60.0, A_UNIT_DEC = 50.0, V_UNIT_MIN = 60.0;
    private static final double V_CONF      = 110.0;
    private static final double V_HEAVY_8   = 90.0,  V_HEAVY_10  = 95.0;
    private static final int    HEAVY_WEAR_MAX_PASSES = 3;

    private static final Vector2D ZERO = new Vector2D(0, 0);


    public static Packet msgSquare(Vector2D pos) {
        PacketSpec spec = new PacketSpec(
                PacketSpec.PacketFamily.MESSAGE, ShapeType.SQUARE, 2, 2,
                Move.constCompat(V_SQ_COMP, V_SQ_INCOMP),
                List.of(Comp.noiseOnImpact(1)),
                null
        );
        return new Packet(spec, Skin.SQUARE_GREEN, pos, ZERO);
    }

    public static Packet msgTriangle(Vector2D pos) {
        PacketSpec spec = new PacketSpec(
                PacketSpec.PacketFamily.MESSAGE, ShapeType.TRIANGLE, 3, 3,
                Move.accelOnIncompat(V_TR_COMP, V_TR_START, A_TR, V_TR_MAX),
                List.of(Comp.noiseOnImpact(1)),
                null
        );
        return new Packet(spec, Skin.TRIANGLE_YELLOW, pos, ZERO);
    }


    public static Packet msgUnit(Vector2D pos) {
        PacketSpec spec = new PacketSpec(
                PacketSpec.PacketFamily.MESSAGE, ShapeType.CIRCLE, 1, 1,
                Move.msg1(V_UNIT0, A_UNIT, A_UNIT_DEC, V_UNIT_MIN),
                List.of(
                        Comp.noiseOnImpact(1),
                        Comp.bounceBackOnCollision()
                ),
                null
        );
        return new Packet(spec, Skin.UNIT_BLACK, pos, ZERO);
    }

    // ===================== Protected =====================

    // محافظت‌شده از ترکیب پیام‌رسان‌ها ساخته می‌شود → ANY (با همه پورت‌ها سازگار)
    public static Packet protFrom(Packet base, Vector2D pos) {
        int sz = Math.max(2, base.getSize() * 2);
        MovementStrategy randomMessageLike = Move.randomMessageLike(
                Move.constCompat(V_SQ_COMP, V_SQ_INCOMP),
                Move.accelOnIncompat(V_TR_COMP, V_TR_START, A_TR, V_TR_MAX),
                Move.msg1(V_UNIT0, A_UNIT, A_UNIT_DEC, V_UNIT_MIN)
        );

        PacketSpec spec = new PacketSpec(
                PacketSpec.PacketFamily.PROTECTED, ShapeType.ANY, sz, 5,
                randomMessageLike,
                List.of(Comp.noiseOnImpact(1)),
                null
        );
        return new Packet(spec, Skin.PROTECTED_LOCK, pos, ZERO);
    }




    public static Packet conf4(Vector2D pos) {
        PacketSpec spec = new PacketSpec(
                PacketSpec.PacketFamily.CONFIDENTIAL, ShapeType.SQUARE, 4, 3,
                Move.constV(V_CONF),
                List.of(
                        Comp.slowNearBusy(0.5, 120),
                        Comp.noiseOnImpact(1)
                ),
                null
        );
        return new Packet(spec, Skin.CONFIDENTIAL_GRAY, pos, ZERO);
    }

    public static Packet conf6(Vector2D pos) {
        PacketSpec spec = new PacketSpec(
                PacketSpec.PacketFamily.CONFIDENTIAL, ShapeType.SQUARE, 6, 4,
                Move.constV(V_CONF),
                List.of(
                        Comp.keepDistance(140, 30),
                        Comp.noiseOnImpact(1)
                ),
                null
        );
        return new Packet(spec, Skin.CONFIDENTIAL_DARK, pos, ZERO);
    }



    public static Packet mass8(Vector2D pos) {
        PacketSpec spec = new PacketSpec(
                PacketSpec.PacketFamily.MASSIVE, ShapeType.CIRCLE, 8, 8,
                Move.curvedAccelOrConst(V_HEAVY_8, 60.0),
                List.of(
                        Comp.wireWear(HEAVY_WEAR_MAX_PASSES),
                        Comp.purgeOnEnter(),
                        Comp.noiseOnImpact(1)
                ),
                null
        );
        return new Packet(spec, Skin.HEAVY_8, pos, ZERO);
    }

    public static Packet mass10(Vector2D pos) {
        PacketSpec spec = new PacketSpec(
                PacketSpec.PacketFamily.MASSIVE, ShapeType.CIRCLE, 10, 10,
                Move.drift(V_HEAVY_10),
                List.of(
                        Comp.wireWear(HEAVY_WEAR_MAX_PASSES),
                        Comp.purgeOnEnter(),
                        Comp.noiseOnImpact(1)
                ),
                null
        );
        return new Packet(spec, Skin.HEAVY_10, pos, ZERO);
    }
}
