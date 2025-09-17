
package model.behavior;

import model.policy.ArrivalPolicy;
import model.routing.PortRouter;

import java.util.List;

public class NormalBehavior extends StandardBehavior {
    public NormalBehavior(PortRouter r, List<ArrivalPolicy> ps, BehaviorContext c) {
        super(r, ps, c);
    }
}
