package astar;

import dijkstra.InfoNode;
import roadNetwork.MillerCoordinate;
import roadNetwork.RoadNode;

public class AStarInfoNode {

    private RoadNode roadNode;
    private AStarInfoNode parent;
    private boolean settedFlag;
    private boolean exploredFlag;
    private long weightFromParent;	//the distance(time) from parent node, measurement: ms
    private long realWeight;				//the distance(time) from start node
    private long estimatedWeight;    // the estimated weight(time) from target node
    private long weight;            //realWeight+estimatedWeight
    private long arrivalTime;			//the time when arrive at this node

//    public AStarInfoNode(long startTime, RoadNode roadNode){
//        this.roadNode = roadNode;
//        this.parent = null;
//        this.settedFlag = false;
//        this.exploredFlag = false;
//        this.weightFromParent = 0;
//        this.realWeight = 0;
//        this.estimatedWeight = 0;
//        this.weight = 0;
//        this.arrivalTime = startTime;
//    }

    public AStarInfoNode(long startTime, RoadNode roadNode, RoadNode target, long estimatedWeight){
        this.roadNode = roadNode;
        this.parent = null;
        this.settedFlag = false;
        this.exploredFlag = false;
        this.weightFromParent = 0;
        this.realWeight = 0;
        this.estimatedWeight = estimatedWeight;
        this.weight = this.realWeight + this.estimatedWeight;
        this.arrivalTime = startTime + weightFromParent;
    }

//    public AStarInfoNode(RoadNode roadNode, RoadNode target){
//        this.roadNode = roadNode;
//        this.parent = null;
//        this.settedFlag = false;
//        this.exploredFlag = false;
//        this.weightFromParent = 0;
//        this.realWeight = 0;
//        this.estimatedWeight = (long) MillerCoordinate.distance(roadNode, target);
//        this.weight = this.realWeight + this.estimatedWeight;
//        this.arrivalTime = 0;
//    }

    public AStarInfoNode(RoadNode roadNode, AStarInfoNode parent, RoadNode target, long weightFromParent, long estimatedWeight){
        this.roadNode = roadNode;
        this.parent = parent;
        this.settedFlag = false;
        this.exploredFlag = false;
        this.weightFromParent = weightFromParent;
        if(parent==null){
            this.realWeight = weightFromParent;
        }else{
            this.realWeight = parent.getRealWeight() + weightFromParent;
        }
        this.estimatedWeight = estimatedWeight;
        this.weight = this.realWeight + this.estimatedWeight;
        if(parent==null){
            this.arrivalTime = 0;
        }else{
            this.arrivalTime = parent.getArrivalTime() + weightFromParent;
        }

    }

    public RoadNode getRoadNode() {
        return roadNode;
    }

    public void setRoadNode(RoadNode roadNode) {
        this.roadNode = roadNode;
    }

    public AStarInfoNode getParent() {
        return parent;
    }

    public void setParent(AStarInfoNode parent) {
        this.parent = parent;
    }

    public boolean isSetted() {
        return settedFlag;
    }

    public void setSetted() {
        this.settedFlag = true;
    }

    public boolean isExplored() {
        return exploredFlag;
    }

    public void setExplored() {
        this.exploredFlag = true;
    }


    public long getWeightFromParent() {
        return weightFromParent;
    }

    public void setWeightFromParent(long weightFromParent) {
        this.weightFromParent = weightFromParent;
    }

    public long getRealWeight() {
        return realWeight;
    }

    public void setRealWeight(long weight) {
        this.realWeight = weight;
    }

    public long getEstimatedWeight() {
        return estimatedWeight;
    }

    public void setEstimatedWeight(long estimatedWeight) {
        this.estimatedWeight = estimatedWeight;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

}
