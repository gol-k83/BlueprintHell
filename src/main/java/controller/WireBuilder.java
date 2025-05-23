package controller;

import model.Port;
import model.Wire;
import util.Constants;
import util.Vector2D;
import view.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

public class WireBuilder {
    private final WireManager wireManager;
    private final GameStageView stage;

    private Port fromPort = null;
    private WirePreview preview = null;
    private boolean active = false;
    private Point startPointScreen;  // نقطه شروع در مختصات صفحه

    public WireBuilder(WireManager wireManager, GameStageView stage) {
        this.wireManager = wireManager;
        this.stage = stage;
    }

    // وقتی روی یک پورت کلیک می‌کنی
    public void onPortClick(Port port) {
        if (!active) {
            if (port.getType() == Port.PortType.OUTPUT) {
                fromPort = port;
                preview = new WirePreview(fromPort); // ← ارسال مستقیم پورت
                active = true;
                System.out.println(" سیم‌کشی شروع شد از پورت: " + fromPort.getId());
            }
        } else {
            if (port.getType() == Port.PortType.INPUT && isValidConnection(fromPort, port)) {
                boolean success = wireManager.tryConnectPorts(fromPort, port);
                Wire newWire = wireManager.connectAndReturn(fromPort, port);
                if (newWire != null) {
                    stage.addWire(newWire); // ؟؟WireView درست اضافه میشه
                    System.out.println("✅ سیم متصل شد بین " + fromPort.getId() + " و " + port.getId());
                } else {
                    System.out.println("اتصال نامعتبر یا محدودیت سیم");
                }

            }

            cancel(); // همیشه بعد از تلاش برای اتصال
        }
    }

    public void onPortClick(PortView portView) {
        Port port = portView.getPort();
        if (!active) {
            if (port.getType() == Port.PortType.OUTPUT) {
                fromPort = port;
                Vector2D startPoint = new Vector2D(
                        portView.getBounds().x + Constants.PORT_SIZE / 2.0,
                        portView.getBounds().y + Constants.PORT_SIZE / 2.0
                );
                preview = new WirePreview(fromPort, startPoint); // ← سازنده جدید
                active = true;
            }
        } else {
            if (port.getType() == Port.PortType.INPUT && isValidConnection(fromPort, port)) {
                boolean success = wireManager.tryConnectPorts(fromPort, port);
                if (success) {
                    stage.addWire(new Wire(fromPort, port));
                }
            }
            cancel();
        }
    }


    // وقتی موس را در حال سیم‌کشی حرکت می‌دهی
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

    public boolean isActive() {
        return active;
    }

    public WirePreview getPreview() {
        return preview;
    }

    public WireManager getWireManager() {
        return wireManager;
    }

    private boolean isValidConnection(Port from, Port to) {
        //  از یک سیستم به خودش نباشه
        if (from.getParentNode() == to.getParentNode()) return false;

        if (!to.isCompatibleWith(from.getCompatibleShape())) return false;


        if (from.isOccupied() || to.isOccupied()) return false;

        // . فاصله سیم از موجودی بیشتر نباشه
        double distance = from.getPosition().distanceTo(to.getPosition());
        return distance <= Constants.TOTAL_AVAILABLE_WIRE_LENGTH;
    }

    public void drawPreview(Graphics2D g, GameStageView stage) {
        if (preview != null) {
            preview.draw(g, stage);
        }
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
        if (fromPort != null && isValidConnection(fromPort, toPort)) {
            Wire wire = wireManager.connectAndReturn(fromPort, toPort);
            if (wire != null) {
                // نقطه پایان موس هنگام رهاسازی
                PortView toView = stage.findPortView(toPort);
                SystemNodeView toNodeView = stage.findViewFor(toPort.getParentNode());

                if (toView != null && toNodeView != null) {
                    Point endPointScreen = toView.getScreenPosition(toNodeView);

                    stage.addWire(wire);
                }
//لاگگ تست
                System.out.println(" سیم متصل شد و رسم شد بین " + fromPort.getId() + " و " + toPort.getId());
            } else {
                System.out.println(" اتصال نامعتبر یا طول مجاز تمام شده");
            }
        }

        cancel();
    }


}

