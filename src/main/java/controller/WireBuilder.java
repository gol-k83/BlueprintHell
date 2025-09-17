

package controller;

import model.Port;
import model.Wire;
import util.Constants;
import util.Vector2D;
import view.*;

import java.awt.*;

public class WireBuilder {
    private final WireManager wireManager;
    public final GameStageView stage;

    private Port fromPort = null;
    private WirePreview preview = null;
    private boolean active = false;
    private Point startPointScreen;

    public WireBuilder(WireManager wireManager, GameStageView stage) {
        this.wireManager = wireManager;
        this.stage = stage;
    }


    public void onPortClick(Port port) {
        if (port == null) return;

        if (!active) {
            if (port.getType() == Port.PortType.OUTPUT) {
                fromPort = port;
                preview = new WirePreview(fromPort);
                active = true;
                System.out.println("سیم‌کشی شروع شد از پورت: " + fromPort.getId());
            }
        } else {
            if (port.getType() == Port.PortType.INPUT && isValidConnection(fromPort, port)) {
                Wire newWire = wireManager.connectAndReturn(fromPort, port);
                if (newWire != null) {
                    stage.addWire(newWire);
                    System.out.println(" سیم متصل شد بین " + fromPort.getId() + " و " + port.getId());
                } else {
                    System.out.println("اتصال نامعتبر یا محدودیت سیم");
                }
            }
            cancel();
        }
    }


    public void onPortClick(PortView portView) {
        if (portView == null) return;
        Port port = portView.getPort();

        if (!active) {
            if (port.getType() == Port.PortType.OUTPUT) {
                fromPort = port;
                Vector2D startPoint = new Vector2D(
                        portView.getBounds().x + Constants.PORT_SIZE / 2.0,
                        portView.getBounds().y + Constants.PORT_SIZE / 2.0
                );
                preview = new WirePreview(fromPort, startPoint);
                active = true;
            }
        } else {
            if (port.getType() == Port.PortType.INPUT && isValidConnection(fromPort, port)) {
                Wire newWire = wireManager.connectAndReturn(fromPort, port);
                if (newWire != null) {
                    stage.addWire(newWire);
                    System.out.println(" سیم متصل شد بین " + fromPort.getId() + " و " + port.getId());
                } else {
                    System.out.println("اتصال نامعتبر یا محدودیت سیم");
                }
            }
            cancel();
        }
    }

    public void onMouseMove(Vector2D mousePos) {
        if (preview != null) {
            preview.updateMousePosition(mousePos);
            Port underPort = stage.findPortAt(mousePos.toPoint());
            boolean valid = (underPort != null && isValidConnection(fromPort, underPort));
            preview.updateValidity(valid);
        }
    }

    public void cancel() {
        fromPort = null;
        preview = null;
        active = false;
    }

    public boolean isActive() { return active; }
    public WirePreview getPreview() { return preview; }
    public WireManager getWireManager() { return wireManager; }


    private boolean isValidConnection(Port from, Port to) {
        if (from == null || to == null) return false;
        if (from.getParentNode() == to.getParentNode()) return false;
        if (from.getType() != Port.PortType.OUTPUT || to.getType() != Port.PortType.INPUT) return false;
       // if (from.isOccupied() || to.isOccupied()) return false;

        double distance = from.getPosition().distanceTo(to.getPosition());
        return distance <= Constants.TOTAL_AVAILABLE_WIRE_LENGTH;
    }

    public void drawPreview(Graphics2D g, GameStageView stage) {
        if (preview != null) preview.draw(g, stage);
    }

    public void startDraggingFrom(Port from) {
        this.fromPort = from;
        this.preview = new WirePreview(from);
        this.active = true;
        PortView portView = stage.findPortView(from);
        SystemNodeView nodeView = stage.findViewFor(from.getParentNode());
        if (portView != null && nodeView != null) {
            startPointScreen = portView.getScreenPosition(nodeView);
        }
    }

    public void finishDraggingTo(Port toPort) {
        if (fromPort == null || !isValidConnection(fromPort, toPort)) {
            cancel();
            return;
        }

        Wire wire = wireManager.connectAndReturn(fromPort, toPort);
        if (wire == null) {
            System.out.println(" اتصال نامعتبر یا طول مجاز تمام شده");
            cancel();
            return;
        }

        PortView toView = stage.findPortView(toPort);
        SystemNodeView toNodeView = stage.findViewFor(toPort.getParentNode());


        if (toView != null && toNodeView != null) {
            stage.addWire(wire);
            System.out.println("سیم متصل شد و رسم شد بین " + fromPort.getId() + " و " + toPort.getId());
        }

        System.out.println("fromPort = " + fromPort);
        System.out.println("toPort   = " + toPort);
        System.out.println("wire     = " + wire);
        System.out.println("toView   = " + toView);
        System.out.println("toNodeView = " + toNodeView);

        cancel();
    }
}
