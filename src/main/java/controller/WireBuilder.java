package controller;

import model.Port;
import model.Wire;
import util.Constants;
import util.Vector2D;
import view.GameStageView;
import view.PortView;
import view.WirePreview;

import java.awt.*;

public class WireBuilder {
    private final WireManager wireManager;
    private final GameStageView stage;

    private Port fromPort = null;
    private WirePreview preview = null;
    private boolean active = false;

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
                if (success) {
                    stage.addWire(new Wire(fromPort, port)); // اضافه به ویو
                    System.out.println(" سیم متصل شد بین " + fromPort.getId() + " و " + port.getId());
                } else {
                    System.out.println(" اتصال نامعتبر بین پورت‌ها");
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
        // ۱. از یک سیستم به خودش نباشه
        if (from.getParentNode() == to.getParentNode()) return false;

        // ۲. یکیش ورودی باشه یکی خروجی (چک شده در بالا)
        // ۳. شکل‌ها سازگار باشن
        if (!to.isCompatibleWith(from.getCompatibleShape())) return false;

        // ۴. قبلاً سیم نداشته باشن
        if (from.isOccupied() || to.isOccupied()) return false;

        // ۵. فاصله سیم از موجودی بیشتر نباشه
        double distance = from.getPosition().distanceTo(to.getPosition());
        return distance <= Constants.TOTAL_AVAILABLE_WIRE_LENGTH;
    }

    public void drawPreview(Graphics2D g) {
        if (preview != null) {
            preview.draw(g);  // کلاس WirePreview باید این متد را داشته باشد
        }
    }

}
