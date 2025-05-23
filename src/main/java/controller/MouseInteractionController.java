package controller;

import model.Port;
import model.Wire;
import util.Constants;
import util.Vector2D;
import view.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MouseInteractionController {
    private final WireBuilder wireBuilder;
    private final GamePanel gamePanel;
    private int nextDropX = 400;
    private int nextDropY = 300;
    private final int DROP_OFFSET = 100;
    private final GameStageView gameStageView;

    private SystemNodeView draggedNode = null;
    private Point dragOffset = null;
    // private SystemNodeView draggedView = null;

    public MouseInteractionController(WireBuilder wireBuilder, GamePanel gamePanel) {
        this.wireBuilder = wireBuilder;
        this.gamePanel = gamePanel;
        this.gameStageView = gamePanel.getGameStageView();
    }

    public void handleMousePressed(MouseEvent e) {
        Point click = e.getPoint();

        // اگر روی یک پورت کلیک شد → wire
        Port clickedPort = gamePanel.findPortAt(click);
      //  Port clickedPort = gamePanel.findPortAt(e.getPoint());
        if (clickedPort != null && clickedPort.getType() == Port.PortType.OUTPUT) {
            wireBuilder.startDraggingFrom(clickedPort);
            dragOffset = click; // نگه داریم برای شروع سیم
         //   e.consume();
            return;
        }

       if (clickedPort != null) {
           System.out.println(" روی پورت کلیک شد: " + clickedPort.getId());
          // wireBuilder.onPortClick(clickedPort);
           gamePanel.repaint();
          return;
       }

        //  اگر کلیک راست بود → حذف وایر
        if (e.getButton() == MouseEvent.BUTTON3) {
            Wire targetWire = gamePanel.findWireNear(click);
            if (targetWire != null) {
                wireBuilder.getWireManager().removeWire(targetWire);
                gamePanel.repaint();
                return;
            }
        }

        //
        GameStageView stage = gamePanel.getGameStageView();
        NodePalettePanel palette = stage.getNodePalettePanel();

        if (palette.getBounds().contains(click)) {
            // تبدیل مختصات کلیک به مختصات محلی پنل palette
            Point localClick = SwingUtilities.convertPoint(gamePanel, click, palette);

            for (SystemNodeView view : palette.getAvailableNodes()) {
                Rectangle bounds = new Rectangle(50, (int) view.getNode().getPosition().getY(),
                        Constants.NODE_WIDTH, Constants.NODE_HEIGHT);

                if (bounds.contains(localClick)) {
                    // حذف از پنل سمت چپ
                    palette.removeNode(view);

                    // اضافه کردن به زمین بازی
                    stage.addNodeToStage(view); //
                    view.setBounds(nextDropX, nextDropY, Constants.NODE_WIDTH, Constants.NODE_HEIGHT);
                    view.getNode().setPosition(new Vector2D(nextDropX, nextDropY)); //

                    // برای درگ شدن بلافاصله
                    draggedNode = view;
                    dragOffset = new Point(click.x - nextDropX, click.y - nextDropY);

                    nextDropX += DROP_OFFSET;
                    nextDropY += DROP_OFFSET;

                    gamePanel.repaint();
                    return;
                }
            }
        }


        cancelWireDrawing();

        for (SystemNodeView nodeView : gamePanel.getSystemNodeViews()) {
            Rectangle bounds = nodeView.getBounds();
            if (bounds.contains(click)) {
                draggedNode = nodeView;
                dragOffset = new Point(click.x - bounds.x, click.y - bounds.y);

                gamePanel.getSystemNodeViews().forEach(n -> n.setSelected(false));
                nodeView.setSelected(true);

                gamePanel.repaint();
                return;
            }
        }
    }


    public void handleMouseDragged(MouseEvent e) {
        if (draggedNode != null && dragOffset != null) {
            int newX = e.getX() - dragOffset.x;
            int newY = e.getY() - dragOffset.y;
            draggedNode.setLocation(newX, newY);
            draggedNode.getNode().setPosition(new Vector2D(newX, newY));
            draggedNode.moveTo(newX, newY);
            gamePanel.repaint();
        }
        if (wireBuilder.isActive()) {//////++++++
            wireBuilder.onMouseMove(new Vector2D(e.getX(), e.getY()));
            gamePanel.repaint();
        }

    }

    public void handleMouseReleased(MouseEvent e) {
        gameStageView.addNodeToStage(draggedNode);
        draggedNode = null;
        dragOffset = null;
        if (wireBuilder.isActive()) {//////+++=
            Port target = gamePanel.findPortAt(e.getPoint());
            if (target != null) {
                wireBuilder.finishDraggingTo(target);
            } else {
                wireBuilder.cancel(); // لغو بشه
            }
            gamePanel.repaint();
        }

    }

    public void handleMouseMoved(MouseEvent e) {
        if (wireBuilder.isActive()) {
            wireBuilder.onMouseMove(new Vector2D(e.getX(), e.getY()));
            gamePanel.repaint();
        }
    }

    public void cancelWireDrawing() {
        if (wireBuilder.isActive()) {
            wireBuilder.cancel();
            gamePanel.repaint();
        }
    }

    public WireBuilder getWireBuilder() {
        return wireBuilder;
    }
}
