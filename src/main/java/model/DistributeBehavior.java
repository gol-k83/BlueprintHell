package model;

import model.behavior.NodeBehavior;

import java.util.ArrayList;
import java.util.List;

/** Distributor: پکت حجیم را به بیت‌پکت‌ها می‌شکند؛ بقیه مثل نود عادی.کار */
public class DistributeBehavior implements NodeBehavior {
    @Override
    public void processPacket(SystemNode node, Packet packet) {
        if (packet.getSpec().family() != PacketSpec.PacketFamily.MASSIVE) {
            node.sendPacketNormal(packet);
            return;
        }
        int n = packet.getSize(); // اندازه حجیم = تعداد بیت‌پکت بعدا تو مارج بزنی
        List<Packet> bits = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Packet bit = PacketFactory.msgUnit(packet.getPosition()); // بیت‌پکت = MSG1 تبدیل کنشوووون
            bit.setVelocity(packet.getVelocity());
            bit.setTag("group", packet.getId()); //  کع یکی بشن همه بیت‌ها را با یک group-id تگ کن
            bits.add(bit);
        }
        // پکت اصلی رو پاک
        packet.applyNoise(packet.getOriginalSize());
        node.incrementLost(); // (حذف منطقی از شبکه)

        // ارسال بیت‌پکت‌ها
        for (Packet b : bits) node.sendPacketNormal(b);
    }
}
