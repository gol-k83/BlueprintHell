
package view;
//


/** Packet skins aligned with PacketFactory. */
public enum Skin {
    // Message packets
    SQUARE_GREEN    ("Pmessage2", 28),
    TRIANGLE_YELLOW ("Pmessage3", 32),
    UNIT_BLACK      ("Pmessage1", 64),


    PROTECTED_LOCK     ("packet_protected", 64),
    CONFIDENTIAL_GRAY  ("packet_conf1",     64),
    CONFIDENTIAL_DARK  ("packet_conf2",     64),


    HEAVY_8   ("packet_heavy1", 72),
    HEAVY_10  ("packet_heavy2", 72);

    private final String key;   // resource key in ImageAssets
    private final double basePx;

    Skin(String key, double basePx) {
        this.key = key;
        this.basePx = basePx;
    }

    public String key()    { return key; }
    public double basePx() { return basePx; }
}
