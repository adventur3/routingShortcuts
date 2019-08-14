package dijkstra;

import org.jgrapht.Graph;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import simulator.SimClock;

import java.io.Serializable;

public class InfoNode implements Serializable {

    private RoadNode roadNode;
    private InfoNode parent;
    private boolean settedFlag;
    private boolean exploredFlag;
    private long weightFromParent;	//the distance(time) from parent node
    private long weight;				//the distance(time) from start node
    private long arrivalTime;			//the time when arrive at this node

    public InfoNode(long startTime, RoadNode roadNode){
        this.roadNode = roadNode;
        this.parent = null;
        this.settedFlag = false;
        this.exploredFlag = false;
        this.weight = 0;
        this.weightFromParent = 0;
        this.arrivalTime = startTime;
    }

    public InfoNode(RoadNode roadNode, InfoNode parent, long weightFromParent){
        this.roadNode = roadNode;
        this.parent = parent;
        this.settedFlag = false;
        this.exploredFlag = false;
        this.weightFromParent = weightFromParent;
        this.weight = parent.getWeight() + weightFromParent;
        this.arrivalTime = parent.getArrivalTime() + weightFromParent;
    }

    public RoadNode getRoadNode() {
        return roadNode;
    }

    public void setRoadNode(RoadNode roadNode) {
        this.roadNode = roadNode;
    }

    public InfoNode getParent() {
        return parent;
    }

    public void setParent(InfoNode parent) {
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

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public long getWeightFromParent() {
        return weightFromParent;
    }

    public void setWeightFromParent(long weightFromParent) {
        this.weightFromParent = weightFromParent;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
