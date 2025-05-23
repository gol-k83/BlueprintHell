package view;

import controller.MouseInteractionController;
import controller.WireManager;
import model.Port;
import model.Wire;
//import SystemNodeView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import controller.WireBuilder;

public class GamePanel extends JPanel {
    private final GameStageView gameStageView;
    private MouseInteractionController mouseController;
    private final WireManager wireManager = new WireManager();
    private WireBuilder wireBuilder;

    // WireBuilder wireBuilder= new WireBuilder(this.getWireManager());
    public GamePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(1200, 800));

        gameStageView = new GameStageView();
        add(gameStageView);
        wireBuilder = new WireBuilder(wireManager,gameStageView);
        gameStageView.setWireBuilder(wireBuilder);
        mouseController = new MouseInteractionController(wireBuilder, this);

        // اتصال کنترلر به رویدادهای ماوس
       gameStageView.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseController.handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseController.handleMouseReleased(e);
            }
        });

        gameStageView.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseController.handleMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseController.handleMouseMoved(e);
            }
        });


    }
//    public List<View.SystemNodeView> getSystemNodeViews() {
//        return gameStageView.getSystemNodeViews();
//    }


    // private final WireManager wireManager = new WireManager();

    public WireManager getWireManager() {
        return wireManager;
    }

    public GameStageView getGameStageView() {
        return gameStageView;
    }

    public Port findPortAt(Point p) {
        return gameStageView.findPortAt(p);
    }


    public List<SystemNodeView> getSystemNodeViews() {
        return gameStageView.getSystemNodeViews();
    }

    public Wire findWireNear(Point p) {
        return gameStageView.findWireNear(p).getWire();
    }


}
// wireBuilder = new WireBuilder(this.getWireManager()); // اگر از WireBuilder استفاده می‌کنی