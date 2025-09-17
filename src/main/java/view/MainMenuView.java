package view;
import javax.swing.*;
import java.awt.*;
import javax.swing.ImageIcon;

public class MainMenuView extends JFrame {
    public JButton startButton;
    public JButton settingsButton;
    public JButton levelsButton;
    public JButton exitButton;


    public MainMenuView() {
        setTitle("Blueprint Hell - Main Menu");
        setSize(1200, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);



    JPanel mainPanel = new JPanel(new BorderLayout()) {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);

        }
    };

 JLabel logoLabel =new JLabel();
       // new ImageIcon("/resource/mainMenuImage.png.png");
//        URL url = getClass().getResource("/blue.png");
//        System.out.println("Image URL = " + url);
//       //// assert url != null;
      //  new ImageIcon(url);


      ImageIcon logoIcon =new ImageIcon(getClass().getResource("/blue.png"));
        Image scaledImage = logoIcon.getImage().getScaledInstance(900, 50, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledImage));
     // logoLabel.setIcon(logoIcon);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(logoLabel, BorderLayout.NORTH);




JPanel buttonPanel =new JPanel(new GridLayout(4,1,20,20));
buttonPanel.setBackground(Color.BLACK);
buttonPanel.setBorder(BorderFactory.createEmptyBorder(500,400,150,400));


        startButton = createStyledButton("Start Game");
        levelsButton = createStyledButton("Levels");
        settingsButton = createStyledButton("Settings");
        exitButton = createStyledButton("Exit");


        buttonPanel.add(startButton);
        buttonPanel.add(levelsButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel,BorderLayout.CENTER);
        add(mainPanel);
    }
       private JButton createStyledButton(String text ){
        JButton button =new JButton(text) ;
          button.setBackground(Color.GRAY);
           button.setForeground(Color.WHITE);
           button.setFocusPainted(false);
           button.setFont(new Font("Arial",Font.BOLD, 20));

        



           button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
           return button;
       }
}














