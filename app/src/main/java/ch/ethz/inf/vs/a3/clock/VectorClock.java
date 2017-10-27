package ch.ethz.inf.vs.a3.clock;

import java.util.HashMap;
import java.util.Map;

public class VectorClock implements Clock {

    public VectorClock() {
        vector = new HashMap<>();
    }

    @Override
    public void update(Clock other) {
        // needs fixing
        VectorClock other_clock = ((VectorClock) other);
        String other_clock_string;
        for (Integer t_local : vector.keySet()) {
            for (Integer t_other : other_clock.vector.keySet()) {
                int other_time = other_clock.getTime(t_other);
                if (t_local == t_other && vector.get(t_local) < other_time){
                    vector.put(t_local, other_time);
                } else if (t_local != t_other){
                    vector.put(t_other, other_time);
                }
            }
        }
    }

    @Override
    public void setClock(Clock other) {
        VectorClock other_clock = ((VectorClock) other);
        for (Integer t : vector.keySet()) {
            vector.put(t, other_clock.getTime(t));
        }
    }

    @Override
    public void tick(Integer pid) {
        int t = vector.get(pid) + 1;
        vector.put(pid, t);
    }

    @Override
    public boolean happenedBefore(Clock other) {
        VectorClock other_clock = ((VectorClock) other);
        for (Integer t : vector.keySet()) {
            if (vector.get(t) > other_clock.getTime(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String res = "{";
        for (Integer t : vector.keySet()) {
            res = res + "\"" + t + "\":" + vector.get(t) + ",";
        }
        if (res.endsWith(",")){
            res = res.substring(0, res.length() - 1);
        }
        return res + "}";
    }

    @Override
    public void setClockFromString(String clock) {
        if (clock.startsWith("{") && clock.endsWith("}")) {
            String temp = clock.substring(1, clock.length() - 1);
            if (temp.isEmpty()) {
                vector.clear();
            }
        }
    }

    /**
     * getTime
     *
     * @param pid
     * @return time if vector has an entry with pid, -1 otherwise
     */
    public int getTime(Integer pid) {
        Integer t = vector.get(pid);
        if (t != null) {
            return t;
        } else {
            return -1;
        }

    }

    public void addProcess(Integer pid, int time) {
        vector.put(pid, time);
    }

    private Map<Integer, Integer> vector;
}
