package roadNetwork;

import dijkstra.InfoNode;

import java.io.Serializable;

/*
 * the segment of Path
 */
public class PathSegment implements Serializable {
    private InfoNode startNode;
    private InfoNode endNode;
    private long weight;



    public PathSegment(InfoNode startNode, InfoNode endNode, long weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight = weight;
    }

    public void setStartNode(InfoNode startNode) {
        this.startNode = startNode;
    }

    public InfoNode getStartNode() {
        return this.startNode;
    }

    public void setEndNode(InfoNode endNode) {
        this.endNode = endNode;
    }

    public InfoNode getEndNode() {
        return this.endNode;
    }

    public void setDistance(long weight) {
        this.weight = weight;
    }

    public long getWeight() {
        return this.weight;
    }
}