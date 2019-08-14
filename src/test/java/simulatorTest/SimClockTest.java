package simulatorTest;

import simulator.SimClock;

public class SimClockTest {

    public static void main(String[] args){
        SimClock simClock = new SimClock(1561982460000L);
        int minuteId = simClock.getMinuteId();
        System.out.println(minuteId);
    }
}
