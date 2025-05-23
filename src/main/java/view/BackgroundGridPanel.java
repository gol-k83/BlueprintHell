package view;

import javax.swing.*;
import java.awt.*;
import util.ImageAssets;

public class BackgroundGridPanel extends JPanel {
    private final Image backgroundImage;

    public BackgroundGridPanel() {
        this.backgroundImage = ImageAssets.get("grid_background.png");
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}