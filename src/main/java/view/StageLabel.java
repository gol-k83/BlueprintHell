package view;

import javax.swing.*;
import java.awt.*;

public class StageLabel extends JLabel {

    public StageLabel(String stageText) {
        super(stageText, SwingConstants.CENTER);
        setFont(new Font("Arial", Font.BOLD, 18));
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setOpaque(true);
        setBounds(0, 0, 100, 50);
    }

    public void setStageNumber(int number) {
        setText("DAY " + number);
    }
}
