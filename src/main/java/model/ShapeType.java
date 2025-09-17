
package model;

import java.awt.Color;

public enum ShapeType {
    ANY(0, new Color(0, 0, 0, 0)),     // وایلددکارت
    SQUARE(2, Color.GREEN),
    TRIANGLE(3, Color.YELLOW),         // پورت مثلثی زرد
    CIRCLE(1, new Color(255, 105, 180));

    private final int size;
    private final Color color;

    ShapeType(int size, Color color) {
        this.size = size;
        this.color = color;
    }

    public int getSize()  { return size; }
    public Color getColor(){ return color; }

    public boolean isWildcard() { return this == ANY; }
}

