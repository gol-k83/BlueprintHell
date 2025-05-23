package view;

import util.ImageAssets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class GameTipBarView extends JPanel {
    private final JLabel dayLabel;
    private final JLabel tipImageLabel;
    private final JButton infoButton;

    public GameTipBarView(ImageIcon tipImage) {
        setLayout(null);
        setPreferredSize(new Dimension(1000, 60));
        setBackground(new Color(60, 60, 60));

        dayLabel = new JLabel("DAY1");
        dayLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        dayLabel.setForeground(Color.WHITE);
        dayLabel.setBounds(10, 10, 80, 40);
        add(dayLabel);


        tipImageLabel = new JLabel(tipImage);
        tipImageLabel.setBounds(100, 10, 800, 40); // جایگاه تصویر راهنما
        add(tipImageLabel);

        infoButton = new JButton("i");
        infoButton.setBounds(870, 10, 72, 40);
        infoButton.setFocusPainted(false);
        add(infoButton);



        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // دریافت عکس از کلاس ImageAssets
                Image tipImage = ImageAssets.get("information.png");

                // اگر تصویر null نبود، آن را نمایش بده
                if (tipImage != null) {
                    ImageIcon tipIcon = new ImageIcon(tipImage);
                    JLabel imageLabel = new JLabel(tipIcon);

                    JOptionPane.showMessageDialog(
                            GameTipBarView.this,
                            imageLabel,
                            "information",
                            JOptionPane.PLAIN_MESSAGE
                    );
                } else {
                    // اگر بارگذاری تصویر شکست خورد
                    JOptionPane.showMessageDialog(
                            GameTipBarView.this,
                            "تصویر راهنما یافت نشد!",
                            "خطا",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    public void setDayLabel(String text) {
        dayLabel.setText(text);
    }

    public void setDayNumber(int dayNumber) {
        dayLabel.setText("DAY" + dayNumber);
    }
}




























