package model;

import model.behavior.NodeBehavior;

import java.util.List;

/* و پکت حجیم میسازه از خورده پک ها. */
public class MergeBehavior implements NodeBehavior {
    @Override
    public void processPacket(SystemNode node, Packet packet) {
        // اگر بیت‌پکت است و تگ group دارد، اونو را ذخیره کنیم
        if (packet.getSpec().family() == PacketSpec.PacketFamily.MESSAGE
                && packet.getSize() == 1
                && packet.getTag("group") != null) {
            String gid = String.valueOf(packet.getTag("group"));
            SystemNodeRegistry.storeBit(node, gid, packet); // در بافر داخلی ثبت
            // ‌د (وارد بافر
            return;
        }
        // پکت‌های عادی مثل نود معمولی
        node.sendPacketNormal(packet);
    }

    @Override
    public void onUpdate(SystemNode node, double dt) {
        // اگر برای یک group تعداد کافی بیت جمع شد باید پکت حجی بسازیم پس
        List<Packet> ready = SystemNodeRegistry.popCompletedGroup(node);
        if (ready != null && !ready.isEmpty()) {
            int size = ready.size();
            Packet heavy = (size >= 10) ? PacketFactory.mass10(node.getPosition())
                    : PacketFactory.mass8(node.getPosition());
            node.sendPacketNormal(heavy);
        }
    }
}
