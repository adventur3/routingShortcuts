package simulator;

import roadNetwork.RoadNode;

public class Request {

    private RoadNode start;
    private RoadNode target;

    public Request(RoadNode start, RoadNode target){
        this.start = start;
        this.target = target;
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
}
