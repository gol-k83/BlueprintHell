package controller;
import model.Port;
import model.Wire;
import util.ImageAssets;
import util.Vector2D;
import view.GamePanel;
import view.MainMenuView;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Main {

    public static void main(String[] args) {
        ImageAssets.preloadAll();
javax.swing.SwingUtilities.invokeLater(()->{

   MainMenuView mainMenu=new MainMenuView();
   new MenuController(mainMenu);
            mainMenu.setVisible(true);
        }

        );

    }
}



