package ch.ethz.inf.vs.a3.clock;

public class LamportClock implements Clock {

    @Override
    public void update(Clock other) {
        int other_time = ((LamportClock) other).getTime();
        time = (time >= other_time) ? time : other_time;
    }

    @Override
    public void setClock(Clock other) {
        time = ((LamportClock) other).getTime();
    }

    @Override
    public void tick(Integer pid) {
        time++;
    }

    @Override
    public boolean happenedBefore(Clock other) {
        return time < ((LamportClock) other).getTime();
    }

    @Override
    public String toString() {
        return String.valueOf(time);
    }

    @Override
    public void setClockFromString(String clock) {
        try {
            time = Integer.parseInt(clock);
        } catch (NumberFormatException nfe) {
            //nfe.printStackTrace();
        }
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    private int time;
}
