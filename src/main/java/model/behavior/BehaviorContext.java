// file: src/main/java/model/behavior/BehaviorContext.java
//package model.behavior;
//
//import model.Packet;
//import model.Port;
//import model.SystemNode;
//import model.ShapeType;
//
//import java.util.List;
//
///**
// * BehaviorContext پلی است بین Behavior/Policy و جهان بازی (Stage/Manager).
// * هر اثر جانبی (ارسال، بافر، تلپورت، تبدیل، اسپاون، جست‌وجو، Split/Merge و …)
// * باید فقط از طریق این کانتکست انجام شود تا معماری تمیز بماند.
// *
// * StageManager (یا کلاسی مشابه) باید این اینترفیس را پیاده‌سازی کند.
// */
//public interface BehaviorContext {
//
//    // ---------- وضعیت سیستم/مقصد ----------
//    /** آیا سیستم روشن/Active است (نه خاموش/Stun)? */
//    boolean isSystemActive(SystemNode n);
//
//    /** آیا این پورت به سیستمی می‌رود که فعلاً Active است؟ */
//    boolean leadsToActiveSystem(Port p);
//
//    // ---------- ارسال و صف ----------
//    /** ارسال پکت از همین نود از طریق پورت خروجی مشخص. */
//    void sendThrough(Port out, Packet p);
//
//    /** اگر خروجی نداریم: تلاش برای بافر یا حذف (Loss). */
//    void enqueueOrDrop(SystemNode n, Packet p);
//
//    // ---------- رفتارهای خاص سیستم‌ها ----------
//    /** برای Spy: انتخاب یک Spy دیگر (در صورت وجود) غیر از نود جاری. */
//    SystemNode pickOtherSpy(SystemNode self);
//
//    /** تلپورت منطقی پکت به یک سیستم هدف (بدون عبور از سیم‌ها). */
//    void teleport(Packet p, SystemNode target);
//
//    /** برای VPN: ایجاد یک پکت Protected بر اساس پکت ورودی. */
//    Packet toProtected(Packet base);
//
//    /** حذف کامل یک پکت از بازی (مثلاً Secret در Spy). */
//    void destroy(Packet p);
//
//    // ---------- نوع‌شناسی پکت‌ها ----------
//    boolean isProtected(Packet p);
//    boolean isSecret(Packet p);
//    boolean isBulk(Packet p);
//
//    // ---------- افکت‌ها/نویز/تروجان ----------
//    /** افزودن نویز به پکت (برای Saboteur و Impact). */
//    void addNoise(Packet p, int amount);
//
//    /**
//     * تلاش برای تروجان‌کردن پکت با احتمال داده‌شده؛
//     * true اگر تبدیل انجام شد.
//     */
//    boolean tryTrojanize(Packet p, double probability);
//
//    // ---------- اسپاون ----------
//    /**
//     * اسپاون فوری یک پکت Messenger با شکل خواسته‌شده در همین نود.
//     * برای Policyهای «اسپاون بر اساس شکل پورت انتخاب‌شده» استفاده می‌شود.
//     */
//    void spawnAt(SystemNode n, ShapeType shape);
//
//    // ---------- ضدتروجان/پویش شعاعی (اختیاری؛ پیاده‌سازی در StageManager) ----------
//    /** پکت‌های داخل شعاعِ داده‌شده حول نود (برای Anti-Trojan). */
//    default List<Packet> findPacketsInRadius(SystemNode center, double radius) { return List.of(); }
//
//    /** جایگزینی درجا: پکت قدیمی با پکت جدید روی همان سیم/موقعیت. */
//    default void replaceInPlace(Packet oldPkt, Packet newPkt) { /* no-op */ }
//
//    // ---------- Bulk Split/Merge (اختیاری؛ برای Distribute/Merge) ----------
//    /** شکستن پکت حجیم به بیت‌پکت‌ها و برگرداندن لیست آن‌ها. */
//    default List<Packet> splitToBits(Packet bulk) { return List.of(); }
//
//    /** تلاش برای ادغام بیت‌ها در این نود؛ در صورت موفقیت پکت Bulk برمی‌گردد. */
//    default Packet tryMergeBits(SystemNode atNode) { return null; }
//
//    // ---------- رویداد VPN (اختیاری) ----------
//    /** وقتی یک VPN خاموش شد: Protectedهای وابسته را برگردان. */
//    default void vpnDown(SystemNode vpnNode) { /* no-op */ }
//    // BehaviorContext.java
//    default boolean isWireIdle(Port p) {
//        return !p.isOccupied(); // فعلاً همین کافیه
//    }
//
//}
package model.behavior;

import model.Packet;
import model.Port;
import model.ShapeType;
import model.SystemNode;

import java.util.List;

public interface BehaviorContext {


    boolean isSystemActive(SystemNode n);
    boolean leadsToActiveSystem(Port p);


    void sendThrough(Port out, Packet p);
    void enqueueOrDrop(SystemNode n, Packet p);


    SystemNode pickOtherSpy(SystemNode self);
    void teleport(Packet p, SystemNode target);
    Packet toProtected(Packet base);
    void destroy(Packet p);


    boolean isProtected(Packet p);
    boolean isSecret(Packet p);
    boolean isBulk(Packet p);


    void addNoise(Packet p, int amount);
    boolean tryTrojanize(Packet p, double probability);


    void spawnAt(SystemNode n, ShapeType shape);
    void spawnThrough(Port out, ShapeType shape);

    default List<Packet> findPacketsInRadius(SystemNode center, double radius) { return List.of(); }
    default void replaceInPlace(Packet oldPkt, Packet newPkt) { }
    default List<Packet> splitToBits(Packet bulk) { return List.of(); }
    default Packet tryMergeBits(SystemNode atNode) { return null; }
    default void vpnDown(SystemNode vpnNode) { }
    default boolean isWireIdle(Port p) { return p != null && !p.isOccupied(); }
}
