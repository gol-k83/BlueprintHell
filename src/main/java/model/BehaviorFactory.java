
package model;

import model.behavior.*;
import model.behavior.SpyBehavior;

import model.behavior.VpnBehavior;

import model.policy.ArrivalPolicy;
import model.routing.CompatibleFirstRouter;
import model.routing.PortRouter;

import java.util.List;
import java.util.Objects;


public final class BehaviorFactory {
    private BehaviorFactory(){}


    public static NodeBehavior of(SystemNode.NodeType type,
                                  BehaviorContext ctx,
                                  List<ArrivalPolicy> policies) {
        return of(type, ctx, policies, new CompatibleFirstRouter());
    }


    public static NodeBehavior of(SystemNode.NodeType type,
                                  BehaviorContext ctx,
                                  List<ArrivalPolicy> policies,
                                  PortRouter router) {
        Objects.requireNonNull(type, "node type");
        Objects.requireNonNull(ctx,  "behavior context");

        PortRouter r       = (router   != null) ? router   : new CompatibleFirstRouter();
        List<ArrivalPolicy> ps = (policies != null) ? policies : List.of();

        return switch (type) {
            case REFERENCE, NORMAL -> new NormalBehavior(r, ps, ctx);
            case SABOTEUR          -> new SaboteurBehavior(r, ps, ctx);
            case SPY               -> new SpyBehavior(r, ps, ctx);
            case VPN               -> new VpnBehavior(r, ps, ctx);


            ///بعدا درست کنم
            case ANTITROJAN        ->   new NormalBehavior(r, ps, ctx);
            case DISTRIBUTE        ->    new NormalBehavior(r, ps, ctx);
            case MERGE             ->        new NormalBehavior(r, ps, ctx);
        };
    }
}
