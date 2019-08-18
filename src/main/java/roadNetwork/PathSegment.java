package roadNetwork;

import java.io.Serializable;

/*
 * the segment of Path
 */
public class PathSegment implements Serializable {
    private RoadNode startNode;
    private RoadNode endNode;
    private long weight;



    public PathSegment(RoadNode startNode, RoadNode endNode, long weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight = weight;
    }

    public void setStartNode(RoadNode startNode) {
        this.startNode = startNode;
    }

    public RoadNode getStartNode() {
        return this.startNode;
    }

    public void setEndNode(RoadNode endNode) {
        this.endNode = endNode;
    }

    public RoadNode getEndNode() {
        return this.endNode;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public long getWeight() {
        return this.weight;
    }
}