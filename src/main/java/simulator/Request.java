package simulator;

import roadNetwork.RoadNode;

public class Request {

    private RoadNode start;
    private RoadNode target;
    private long starttime;

    public Request(RoadNode start, RoadNode target, long starttime){
        this.start = start;
        this.target = target;
        this.starttime = starttime;
    }

    public RoadNode getStart() {
        return start;
    }

    public void setStart(RoadNode start) {
        this.start = start;
    }

    public RoadNode getTarget() {
        return target;
    }

    public void setTarget(RoadNode target) {
        this.target = target;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }
}
