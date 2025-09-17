package model;

import util.Vector2D;
import java.util.*;

public final class SystemNodeRegistry {
    private static final Map<SystemNode.NodeType, List<SystemNode>> nodesByType = new EnumMap<>(SystemNode.NodeType.class);
    private static final Map<SystemNode, Map<String, List<Packet>>> mergeBuffers = new HashMap<>();

    private SystemNodeRegistry(){}




    public static void register(SystemNode node) {
        nodesByType.computeIfAbsent(node.getType(), k -> new ArrayList<>()).add(node);
    }
    public static void unregister(SystemNode node) {
        List<SystemNode> l = nodesByType.get(node.getType());
        if (l != null) l.remove(node);
        mergeBuffers.remove(node);
    }

    public static List<SystemNode> getNodesByType(SystemNode.NodeType t) {
        return nodesByType.getOrDefault(t, Collections.emptyList());
    }

    //  آنتی‌تروجان: TODO) ----
    public static List<Packet> findPacketsInRadius(Vector2D center, double r) {
        // TODO: اینجا باید از لیست پکت‌های روی سیم‌ها استفاده شود.
        return Collections.emptyList();
    }
    public static void replacePacketInPlace(Packet oldP, Packet newP) {
        // TODO: بسته به ساختار Wire، جایگزینی در همان سیم انجام شود.باید برایاز خرابکار کامل کنم
    }

    public static void storeBit(SystemNode node, String groupId, Packet bit) {
        mergeBuffers.computeIfAbsent(node, k -> new HashMap<>())
                .computeIfAbsent(groupId, k -> new ArrayList<>())
                .add(bit);
    }
    public static List<Packet> popCompletedGroup(SystemNode node) {
        Map<String, List<Packet>> map = mergeBuffers.get(node);
        if (map == null) return null;
        for (var e : map.entrySet()) {
            List<Packet> bits = e.getValue();
            if (bits.size() >= 8) { // حداقل ۸ بیت برای ساخت heavy داک رو چککم
                map.remove(e.getKey());
                return bits;
            }
        }
        return null;
    }
}
