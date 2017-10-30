package ch.ethz.inf.vs.a3.clock;

import java.util.HashMap;
import java.util.Map;

public class VectorClock implements Clock {

    private Map<Integer, Integer> vector;

    public VectorClock() {
        vector = new HashMap<>();
    }

    @Override
    public void update(Clock other) {
        VectorClock otherClock = new VectorClock();
        otherClock.setClock(other);
        for (Map.Entry<Integer, Integer> entry : otherClock.vector.entrySet()) {
            if ((entry.getValue()) > getTime(entry.getKey())) {
                addProcess(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void setClock(Clock other) {
        VectorClock otherClock = (VectorClock) other;
        vector = otherClock.vector;
    }

    @Override
    public void tick(Integer pid) {
        addProcess(pid, getTime(pid)+1);
    }

    @Override
    public boolean happenedBefore(Clock other) {
        boolean before = false;
        VectorClock otherClock = (VectorClock) other;
        for (Map.Entry<Integer, Integer> entry : vector.entrySet()) {
            if ((entry.getValue()) > otherClock.getTime(entry.getKey())) {
                return false;
            } else if ((entry.getValue()) < otherClock.getTime(entry.getKey())) {
                before = true;
            }
        }
        return before;
    }

    @Override
    public String toString() {
        String result = "";
        for (Map.Entry<Integer, Integer> entry : vector.entrySet()) {
            result += "\"" + entry.getKey().toString() + "\":" + entry.getValue().toString() + ",";
        }
        if (result.isEmpty()) return "{}";
        result = "{" + result.substring(0, result.length()-1) + "}";
        return result;
    }

    @Override
    public void setClockFromString(String clock) {
        if (clock.startsWith("{") && clock.endsWith("}")) {
            if (clock.length()==2) {
                vector.clear();
            } else {
                String sub = clock.substring(1, clock.length()-1);
                String[] ele = sub.replaceAll(":","").replaceAll(",","").split("\"");

                VectorClock helper = new VectorClock();
                boolean reset = false;

                for (int i=1; i<ele.length; i=i+2) {
                    try {
                        Integer key = Integer.parseInt(ele[i]);
                        int value = Integer.parseInt(ele[i+1]);
                        helper.addProcess(key, value);
                    } catch (NumberFormatException e) {
                        //e.printStackTrace();
                        reset = true;
                        break;
                    }
                }
                if (!reset) vector = helper.vector;
            }
        }
    }

    public int getTime(Integer pid) {
        Integer t = vector.get(pid);
        if (t != null) return t;
        else return -1;
    }

    public void addProcess(Integer pid, int time) {
        vector.put(pid, time);
    }
}
