package view;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {
    private JLabel wireLengthLabel;

    public HUDPanel() {
        setBackground(Color.DARK_GRAY);
        // موقت
    }
    public void updateWireLength(double length) {
        wireLengthLabel.setText("Wire Left: " + String.format("%.1f", length));
    }

}
