package util;

public class Constants {
  // --- سیم‌کشی ---
  public static final double TOTAL_AVAILABLE_WIRE_LENGTH = 900.0; // تقریباً 3 اتصال بلند

  // --- برخورد ---
  public static final double IMPACT_RADIUS = 100.0; // با توجه به فاصله‌ی پکت‌ها
  public static final double COLLISION_OFFSET = 5.0;
  public static final int IMPACT_WAVE_MAX_NOISE = 3;

  // --- اندازه‌ها ---
  public static final int PORT_SIZE = 10; // در PortView استفاده شود
  public static final int NODE_WIDTH = 100;
  public static final int NODE_HEIGHT = 100;
  public static final int PACKET_BASE_SIZE = 20; // مربع 2 = 20، مثلث 3 = 30
  public static final double PACKET_SPEED = 100.0; // px/s
  public static final double SPEED_SCALING_FOR_INCOMPATIBLE_PORT = 0.5;

  // --- سیم ---
  public static final int WIRE_HITBOX_RADIUS = 8; // برای isNear در WireView

  // --- UI ---
  public static final int NODE_SPACING_Y = 120;
  public static final int NODE_SPACING_X = 150;
  public static final double COLISION1_OFFSET =3 ;///////////////////////////////////////////
}
