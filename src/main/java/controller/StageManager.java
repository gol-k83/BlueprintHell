//package controller;
//
//import model.GameBehaviorContext;
//import model.SystemNode;
//import model.Packet;
//import model.Wire;
//import model.behavior.BehaviorContext;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StageManager {
//
//    private static final StageManager instance = new StageManager();
//
//    private final List<SystemNode> systemNodes = new ArrayList<>();
//    private final List<Wire> wires = new ArrayList<>();
//    private final PacketTracker tracker = new PacketTracker();
//
//    private int currentStageNumber = 1;
//
//    private StageManager() {
//    }
//
//    public static StageManager getInstance() {
//        return instance;
//    }
//
//    public void loadStage(int stageNumber) {
//        systemNodes.clear();
//        wires.clear();
//        tracker.reset();
//        currentStageNumber = stageNumber;
//        final BehaviorContext behaviorContext = new GameBehaviorContext();
//        // اینجا مرحله رو با توجه به stageNumber باید لود کنم
//
//    }
//
//    public void addSystemNode(SystemNode node) {
//        systemNodes.add(node);
//    }
//
//    public void addWire(Wire wire) {
//        wires.add(wire);
//    }
//
//
//    WireManagerV2 wireManagerV2=new WireManagerV2(wires);
//    double totalLength = wireManagerV2.getTotalWireLength();
//
//
//    public List<SystemNode> getSystemNodes() {
//        return systemNodes;
//    }
//
//    public List<Wire> getWires() {
//        return wires;
//    }
//
//    public PacketTracker getTracker() {
//        return tracker;
//    }
//
//    public int getCurrentStageNumber() {
//        return currentStageNumber;
//    }
//
//    public void resetStage() {
//        systemNodes.clear();
//        wires.clear();
//        tracker.reset();
//    }
//
//    public double getPacketLossRatio() {
//        return tracker.getPacketLossRatio();
//    }
//
//    public boolean isStageFailed() {
//        return getPacketLossRatio() > 0.5;
//    }
//
//}
// file: src/main/java/controller/StageManager.java
//package controller;
//
//import config.StageConfig;
//import model.SystemNode;
//import model.Wire;
//import model.Packet;
//import controller.PacketTracker;
//
//import model.BehaviorFactory;
//import model.GameBehaviorContext;
//
//import model.behavior.BehaviorContext;
//import model.policy.ArrivalPolicy;
//import model.policy.SpawnByOutputsPolicy;
//import model.routing.PortRouter;
//import model.routing.CompatibleFirstRouter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StageManager {
//
//    private static final StageManager instance = new StageManager();
//
//    private final List<SystemNode> systemNodes = new ArrayList<>();
//    private final List<Wire> wires = new ArrayList<>();
//    private final PacketTracker tracker = new PacketTracker();
//
//    private int currentStageNumber = 1;
//
//    // ⬅️ کانتکست مشترک برای همهٔ Behaviorها (در سطح کلاس، نه محلی)
//    private final BehaviorContext behaviorContext = new GameBehaviorContext();
//
//    private StageManager() { }
//
//    public static StageManager getInstance() {
//        return instance;
//    }
//
//    /** در صورت نیاز در جای دیگر به Context دسترسی بده */
//    public BehaviorContext getBehaviorContext() {
//        return behaviorContext;
//    }
//
//    /**
//     * به همهٔ نودهای داده‌شده Behavior تزریق می‌کند.
//     * قبل از شروع شبیه‌سازی/ارسال پکت‌ها این را صدا بزن.
//     */
//    public void attachBehaviorsTo(List<SystemNode> nodes) {
//        attachBehaviorsTo(nodes, new CompatibleFirstRouter(), List.of());
//    }
//
//    /**
//     * نسخهٔ کامل با Router/Policies سفارشی.
//     */
//
//    public void attachBehaviorsTo(List<SystemNode> nodes,
//                                  PortRouter router,
//                                  List<ArrivalPolicy> policies) {
//        util.Dbg.log("ATTACH", "router=%s policies=%d", router.getClass().getSimpleName(), 1);
//        PortRouter r = (router != null) ? router : new CompatibleFirstRouter();
//        List<ArrivalPolicy> ps = (policies != null) ? policies : List.of();
//        for (SystemNode n : nodes) {
//
//            n.setBehavior(BehaviorFactory.of(n.getType(), behaviorContext, ps, r));
//
//        }
//    }
//
//    /**
//     * اگر چند مرحله داری، این‌جا بساز/لود کن.
//     * بعد از ساخت نودها و سیم‌ها، attachBehaviorsTo(systemNodes) را فراموش نکن.
//     */
////    public void loadStage(int stageNumber) {
////        systemNodes.clear();
////        wires.clear();
////        tracker.reset();
////        currentStageNumber = stageNumber;
////
////        // 1) نودها را از StageConfig بگیر
////        List<SystemNode> initial = StageConfig.getStage1InitialNodes();
////        List<SystemNode> given   = StageConfig.getStage1GivenNodes();
////
////        // 2) یک لیست کل بساز
////        List<SystemNode> all = new ArrayList<>();
////        all.addAll(initial);
////        all.addAll(given);
////
////        // 3) اگر وایرهای پیش‌فرضی داری، اینجا بساز/اضافه کن (اختیاری)
////        // buildDefaultWiresForStage1(initial, given);  // اگر داری
////        // wires.add(new Wire(...));
////
////        // 4) رفتارها را به نودها تزریق کن (خیلی مهم!)
////        attachBehaviorsTo(all); // از متدی که قبلاً گذاشتم استفاده می‌کنه
////
////        // 5) نودها را در StageManager ثبت کن
////        for (SystemNode n : all) {
////            addSystemNode(n);
////        }
////    }
//
//    public void loadStage(int stageNumber) {
//        util.Dbg.log("LOAD", "loadStage(%d) begin", stageNumber);
//        systemNodes.clear();
//        wires.clear();
//        tracker.reset();
//        currentStageNumber = stageNumber;
//
//        // 1) نودها را از StageConfig بگیر
//        List<SystemNode> initial = config.StageConfig.getStage1InitialNodes();
//        List<SystemNode> given   = config.StageConfig.getStage1GivenNodes();
//
//        // 2) لیست کل
//        List<SystemNode> all = new ArrayList<>();
//        all.addAll(initial);
//        all.addAll(given);
//
//        // 3) (اختیاری) اینجا وایرها را بساز و addWire(...) کن
//
//        // 4) رفتار را تزریق کن — اینجا پالیسی را می‌چسبانیم
//        PortRouter router = new CompatibleFirstRouter();
//
//        ArrivalPolicy spawnPolicy = new SpawnByOutputsPolicy(
//                /*onlyWhenDifferentShape=*/ true,   // ورودی مربع → خروجی مربع اسپاون نشود؛ مثلث/… بشود
//                /*requireIdleWire=*/       true,    // قانون: وایر باید خالی باشد
//                /*requireDestOn=*/         true,    // قانون: مقصد باید ON باشد
//                /*maxPerPort=*/            1,       // هر پورت در کل مرحله فقط یک‌بار اسپاون کند
//                /*blockReversePair=*/      true,    // اگر A->B رخ داد، B->A بلاک شود
//                /*oncePerPairGlobal=*/     false    // اگر خواستی هر زوج فقط یک‌بار کل مرحله باشد، true کن
//        );
//
//        attachBehaviorsTo(all, router, java.util.List.of(spawnPolicy)); // ⬅️ مهم: این نسخه
//   //     attachBehaviorsTo(all);
//        // 5) ثبت نودها
//        for (SystemNode n : all) addSystemNode(n);
//
//        util.Dbg.log("LOAD", "nodes initial=%d given=%d total=%d", initial.size(), given.size(), all.size());
//        util.Dbg.log("LOAD", "loadStage(%d) begin", stageNumber);
//    }
//    public void addSystemNode(SystemNode node) {
//        systemNodes.add(node);
//        util.Dbg.log("LOAD", "registered node: %s", node.getId());
//    }
//
//    public void addWire(Wire wire) {
//        wires.add(wire);
//    }
//
//    public List<SystemNode> getSystemNodes() {
//        return systemNodes;
//    }
//
//    public List<Wire> getWires() {
//        return wires;
//    }
//
//    public PacketTracker getTracker() {
//        return tracker;
//    }
//
//    public int getCurrentStageNumber() {
//        return currentStageNumber;
//    }
//
//    public void resetStage() {
//        systemNodes.clear();
//        wires.clear();
//        tracker.reset();
//    }
//
//    public double getPacketLossRatio() {
//        return tracker.getPacketLossRatio();
//    }
//
//    public boolean isStageFailed() {
//        return getPacketLossRatio() > 0.5;
//    }
//    public void registerNode(SystemNode node) {
//        if (node == null) return;
//        attachBehaviorsTo(java.util.List.of(node)); // همان تنظیمات مرحله
//        addSystemNode(node);                         // داخل StageManager ثبتش کن
//    }
//    // اگر واقعاً لازمته، این دو خط رو نگه دار ولی دقت کن قبل از محاسبه، wires پر شده باشد.
//     WireManagerV2 wireManagerV2 = new WireManagerV2(wires);
//     double totalLength = wireManagerV2.getTotalWireLength();
//}
// file: src/main/java/controller/StageManager.java
package controller;

import config.StageConfig;
import model.*;
import model.behavior.BehaviorContext;
import model.policy.ArrivalPolicy;
import model.policy.SpawnByOutputsPolicy;
import model.routing.CompatibleFirstRouter;
import model.routing.PortRouter;
import util.Dbg;

import java.util.ArrayList;
import java.util.List;

public class StageManager {

    private static final StageManager instance = new StageManager();
    public static StageManager getInstance() { return instance; }

    private final List<SystemNode> systemNodes = new ArrayList<>();
    private final List<Wire> wires = new ArrayList<>();
    private final PacketTracker tracker = new PacketTracker();
    private int currentStageNumber = 1;

    // --- Context/Router/Policy مشترک مرحله ---
    private final BehaviorContext behaviorContext = new GameBehaviorContext();
    private final PortRouter     defaultRouter    = new CompatibleFirstRouter();
    // NOTE: این instance باید shared باشد تا شمارنده‌های per-port/anti-loop درست کار کنند
    private final ArrivalPolicy  sharedSpawnPolicy = new SpawnByOutputsPolicy(
            true,   // onlyWhenDifferentShape
            true,   // requireIdleWire
            true,   // requireDestOn
            1,      // maxPerPort (هر پورت در کل مرحله فقط یک‌بار)
            true,   // blockReversePair
            false   // oncePerPairGlobal
    );

    private StageManager() {}

    public BehaviorContext getBehaviorContext() { return behaviorContext; }
    public List<SystemNode> getSystemNodes() { return systemNodes; }
    public List<Wire> getWires() { return wires; }
    public PacketTracker getTracker() { return tracker; }
    public int getCurrentStageNumber() { return currentStageNumber; }

    // ---------- لاگ‌دار: attach با Router/Policies ----------
    public void attachBehaviorsTo(List<SystemNode> nodes) {
        attachBehaviorsTo(nodes, defaultRouter, List.of(sharedSpawnPolicy));
    }

    public void attachBehaviorsTo(List<SystemNode> nodes,
                                  PortRouter router,
                                  List<ArrivalPolicy> policies) {
        PortRouter r = (router != null) ? router : defaultRouter;
        List<ArrivalPolicy> ps = (policies != null && !policies.isEmpty())
                ? policies : List.of(sharedSpawnPolicy);

        Dbg.log("ATTACH", "router=%s policies=%d", r.getClass().getSimpleName(), ps.size());
        for (SystemNode n : nodes) {
            n.setBehavior(BehaviorFactory.of(n.getType(), behaviorContext, ps, r));
            Dbg.log("ATTACH", "node=%s type=%s behavior=%s",
                    n.getId(), n.getType(),
                    (n.getBehavior()!=null ? n.getBehavior().getClass().getSimpleName() : "null"));
        }
    }

    // برای Drag&Drop
    public void registerNode(SystemNode node) {
        if (node == null) return;
        Dbg.log("ATTACH", "registerNode id=%s type=%s", node.getId(), node.getType());
        attachBehaviorsTo(List.of(node)); // ⬅️ همین shared policy/router
        addSystemNode(node);
    }

    public void loadStage(int stageNumber) {
        Dbg.log("LOAD", "loadStage(%d) begin", stageNumber);
        systemNodes.clear();
        wires.clear();
        tracker.reset();
        currentStageNumber = stageNumber;

        // 1) StageConfig
        List<SystemNode> initial = StageConfig.getStage1InitialNodes();
        List<SystemNode> given   = StageConfig.getStage1GivenNodes();

        // 2) همه نودها
        List<SystemNode> all = new ArrayList<>();
        all.addAll(initial);
        all.addAll(given);

        // 3) (اختیاری) wires بساز/ثبت کن

        // 4) رفتار/پالیسی را تزریق کن (با shared instance)
        attachBehaviorsTo(all);

        // 5) ثبت در StageManager
        for (SystemNode n : all) addSystemNode(n);

        Dbg.log("LOAD", "nodes initial=%d given=%d total=%d", initial.size(), given.size(), all.size());
        Dbg.log("LOAD", "loadStage(%d) done", stageNumber);
    }

    public void addSystemNode(SystemNode node) {
        systemNodes.add(node);
        Dbg.log("LOAD", "registered node: %s", node.getId());
    }

    public void addWire(Wire wire) { wires.add(wire); }
    public void resetStage() { systemNodes.clear(); wires.clear(); tracker.reset(); }
    public double getPacketLossRatio() { return tracker.getPacketLossRatio(); }
    public boolean isStageFailed() { return getPacketLossRatio() > 0.5; }


//    public void resetPoliciesForNewRun() {
//        if (sharedSpawnPolicy != null) {
//            sharedSpawnPolicy.resetForNewRun();
//        }
//    }
public void resetPoliciesForNewRun() {
    util.Dbg.log("RUN", "resetPoliciesForNewRun()");
    // همون instance مشترک پالیسی که به نودها attach کردی:
    if (sharedSpawnPolicy instanceof model.policy.SpawnByOutputsPolicy p) {
        p.resetForNewRun();
    }
}
    // اگر لازم داری نگه‌دار؛ فقط بعد از پرشدن wires معنی دارد
    WireManagerV2 wireManagerV2 = new WireManagerV2(wires);
    double totalLength = wireManagerV2.getTotalWireLength();
}
