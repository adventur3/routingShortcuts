package simulator;


import java.io.Serializable;


/*
    measurement：millisecond
 */
public class SimClock implements Serializable {
    private static int breakPoint=24;
    private long starttime;  //measurement：millisecond
    private long now;       //measurement：millisecond
    private int ut;         //measurement：millisecond

    public SimClock(long starttime, int ut) {
        this.starttime = starttime;
        this.now = starttime;
        this.ut = ut;
    }

    public SimClock(long starttime) {
        this.starttime = starttime;
        this.now = starttime;
        this.ut = 1000;
    }

    public void add() {
        now += this.ut;
    }

    public void add(int ut) {
        now += ut;
    }

    public long getNow() {
        return now;
    }

    public long getStarttime() {
        return starttime;
    }

    /*
     return 0 ~ breakPoint-1
     */
    public int getMinuteId() {
        long segTime = 86400 / breakPoint;
        if (now >= 57600000) {
            long thetime = this.now - 57600000;
            int minute_id = (int) (((thetime / 1000) % 86400) / segTime);
            return minute_id;
        }
        else{
            int minute_id = (int) ((this.now / 1000) / segTime) + 8;
            return minute_id;
        }

    }


    /*
        return 0 ~ breakPoint-1
    */
    public static int getMinuteId(long time) {
        long segTime = 86400 / breakPoint;
        if (time >= 57600000) {
            long thetime = time - 57600000;
            int minute_id = (int) (((thetime / 1000) % 86400) / segTime);
            return minute_id;
        }
        else{
            int minute_id = (int) ((time / 1000) / segTime) + 8;
            return minute_id;
        }

    }

    public int getUt() {
        return ut;
    }
}