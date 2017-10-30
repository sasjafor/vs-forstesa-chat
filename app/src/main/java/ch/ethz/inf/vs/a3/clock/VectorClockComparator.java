package ch.ethz.inf.vs.a3.clock;

import java.util.Comparator;

import ch.ethz.inf.vs.a3.clock.VectorClock;

public class VectorClockComparator implements Comparator<VectorClock> {

    @Override
    public int compare(VectorClock lhs, VectorClock rhs) {
        if(lhs.happenedBefore(rhs)) {
            return -1;
        }
        else {
            return 1;
        }
    }
}
