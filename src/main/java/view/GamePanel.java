//package view;
//
//import controller.MouseInteractionController;
//import controller.WireManager;
//import model.Port;
//import model.Wire;
////import SystemNodeView;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseMotionAdapter;
//import java.util.List;
//
//import controller.WireBuilder;
//
//public class GamePanel extends JPanel {
//    private final GameStageView gameStageView;
//    private MouseInteractionController mouseController;
//    private final WireManager wireManager = new WireManager();
//    private WireBuilder wireBuilder;
//
//    // WireBuilder wireBuilder= new WireBuilder(this.getWireManager());
//    public GamePanel() {
//        setLayout(null);
//        setPreferredSize(new Dimension(1200, 800));
//
//        gameStageView = new GameStageView();
//        add(gameStageView);
//        wireBuilder = new WireBuilder(wireManager,gameStageView);
//        gameStageView.setWireBuilder(wireBuilder);
//        mouseController = new MouseInteractionController(wireBuilder, this);
//
//
//       gameStageView.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                mouseController.handleMousePressed(e);
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                mouseController.handleMouseReleased(e);
//            }
//        });
//
//        gameStageView.addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                mouseController.handleMouseDragged(e);
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                mouseController.handleMouseMoved(e);
//            }
//        });
//
//
//    }
////    public List<View.SystemNodeView> getSystemNodeViews() {
////        return gameStageView.getSystemNodeViews();
////    }
//
//
//    // private final WireManager wireManager = new WireManager();
//
//    public WireManager getWireManager() {
//        return wireManager;
//    }
//
//    public GameStageView getGameStageView() {
//        return gameStageView;
//    }
//
//    public Port findPortAt(Point p) {
//        return gameStageView.findPortAt(p);
//    }
//
//
//    public List<SystemNodeView> getSystemNodeViews() {
//        return gameStageView.getSystemNodeViews();
//    }
//
//    public Wire findWireNear(Point p) {
//        WireView view = gameStageView.findWireNear(p);
//        if (view != null) {
//            return view.getWire();
//        }
//        return null;
//    }
//
//
//
//}
package view;

import controller.MouseInteractionController;
import controller.PacketSpawner;
import controller.WireManager;
import model.Port;
import model.Wire;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Method;
import java.util.List;
import controller.WireBuilder;
import util.ImageAssets;

public class GamePanel extends JPanel {
    private final GameStageView gameStageView;
    private MouseInteractionController mouseController;
    private final WireManager wireManager = new WireManager();
    private WireBuilder wireBuilder;


    private Timer gameTimer;
    private long lastNano = -1L;
    private Method updateMethod = null; // اگر GameStageView.update(dt) وجود داشت

    public GamePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(1200, 800));


        ImageAssets.preloadAll();
////////////////////////
        gameStageView = new GameStageView();
        PacketSpawner spawner = new PacketSpawner();
        gameStageView.setSpawner(spawner);
        add(gameStageView);
//////////////////////
        wireBuilder = new WireBuilder(wireManager, gameStageView);
        gameStageView.setWireBuilder(wireBuilder);
        mouseController = new MouseInteractionController(wireBuilder, this);

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

        try {
            updateMethod = gameStageView.getClass().getMethod("update", double.class);
        } catch (NoSuchMethodException ignored) {
            updateMethod = null;
        }


        gameTimer = new Timer(16, e -> {
            long now = System.nanoTime();
            if (lastNano < 0L) lastNano = now;
            double dt = (now - lastNano) / 1_000_000_000.0;
            lastNano = now;


            if (updateMethod != null) {
                try {
                    updateMethod.invoke(gameStageView, dt);
                } catch (Exception ex) {
                    // اگر خطایی بود، جلو بازی رو نگیر؛رندر کن
                }
            }
            gameStageView.repaint();
        });
        gameTimer.setCoalesce(true);
        gameTimer.start();
    }

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
        WireView view = gameStageView.findWireNear(p);
        if (view != null) {
            return view.getWire();
        }
        return null;
    }


}
