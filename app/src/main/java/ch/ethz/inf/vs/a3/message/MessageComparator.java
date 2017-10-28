package ch.ethz.inf.vs.a3.message;

import java.util.Comparator;

import ch.ethz.inf.vs.a3.clock.VectorClock;
import ch.ethz.inf.vs.a3.clock.VectorClockComparator;
import ch.ethz.inf.vs.a3.message.Message;

/**
 * Message comparator class. Use with PriorityQueue.
 */


public class MessageComparator implements Comparator<Message> {

    @Override
    public int compare(Message lhs, Message rhs) {
        // TODO: implement
        VectorClockComparator comparator = new VectorClockComparator();

        VectorClock lClock = lhs.timestamp;
        VectorClock rClock = rhs.timestamp;

        int comp;
        comp = comparator.compare(lClock, rClock);
        return comp;
    }

}
