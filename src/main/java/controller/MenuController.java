package controller;
import view.GamePanel;
import view.MainMenuView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MenuController {
    private final MainMenuView view;

    public MenuController(MainMenuView view) {
        this.view = view;
        attachEventListeners();
    }




   private void attachEventListeners(){

       view.startButton.addActionListener(e -> {

           JFrame gameFrame = new JFrame("Game Stage");
           gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           gameFrame.setContentPane(new GamePanel());
           gameFrame.pack();
           gameFrame.setLocationRelativeTo(null);
           gameFrame.setVisible(true);
           controller.StageManager.getInstance().loadStage(1);
           view.dispose();
       });

             view.levelsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Levels Clicked!");
                    // مرحله ها رو باید اینجا نشون بدم
                }
            });

            view.settingsButton.addActionListener( new ActionListener() {
                @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Settings Clicked!");
    }
});

                 view.exitButton.addActionListener(new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent e) {
                         System.out.println("Exiting...");
                         System.exit(0);
                     }
                 });

    }



}
