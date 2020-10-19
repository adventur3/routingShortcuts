package recorder;

import roadNetwork.RoadNode;

public class AlgorithmDetail {
    private RoadNode start;
    private RoadNode target;
    private RoadNode startCore;
    private RoadNode targetCore;
    private String searchType;
    private long realDistance;
    private long estimateDistance;
    private  long difference;

    public AlgorithmDetail(RoadNode start, RoadNode target){
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

    public RoadNode getStartCore() {
        return startCore;
    }

    public void setStartCore(RoadNode startCore) {
        this.startCore = startCore;
    }

    public RoadNode getTargetCore() {
        return targetCore;
    }

    public void setTargetCore(RoadNode targetCore) {
        this.targetCore = targetCore;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public long getRealDistance() {
        return realDistance;
    }

    public void setRealDistance(long realDistance) {
        this.realDistance = realDistance;
    }

    public long getEstimateDistance() {
        return estimateDistance;
    }

    public void setEstimateDistance(long estimateDistance) {
        this.estimateDistance = estimateDistance;
    }

    public long getDifference() {
        return difference;
    }

    public void setDifference(long difference) {
        this.difference = difference;
    }
}
