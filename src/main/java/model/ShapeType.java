// model/ShapeType.java
package model;

import java.awt.*;

public enum ShapeType {
    SQUARE(2, Color.GREEN),
    TRIANGLE(3, Color.YELLOW);

    private final int size;
    private final Color color;

    ShapeType(int size, Color color) {
        this.size = size;
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }
}
