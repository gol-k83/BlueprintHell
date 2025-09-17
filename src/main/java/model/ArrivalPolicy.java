package model;

import java.util.Collections;
import java.util.List;


public interface ArrivalPolicy {


    final class Result {
        public final boolean forwardOriginal;
        public final List<Packet> spawns;

        private Result(boolean forwardOriginal, List<Packet> spawns) {
            this.forwardOriginal = forwardOriginal;
            this.spawns = (spawns == null) ? Collections.emptyList() : spawns;
        }
        public static Result forward()                       { return new Result(true,  List.of()); }
        public static Result drop()                          { return new Result(false, List.of()); }
        public static Result forwardAndSpawn(Packet... ps)   { return new Result(true,  List.of(ps)); }
        public static Result spawnOnly(Packet... ps)         { return new Result(false, List.of(ps)); }
    }


    Result onArrival(SystemNode node, Packet packet);

    class DefaultArrivalPolicy implements ArrivalPolicy {
        @Override
        public Result onArrival(SystemNode node, Packet packet) {

            try {

            } catch (Throwable ignored) { /*  */ }

            return Result.forward();
        }
    }
}
