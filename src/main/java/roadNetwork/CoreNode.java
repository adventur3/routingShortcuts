package roadNetwork;

import java.io.Serializable;
import java.util.Set;

public class CoreNode implements Serializable {
    private RoadNode roadNode;
    private Set<CoreEdge> edgeSet;
    //private long distanceEstimation;

    public CoreNode(RoadNode roadNode, Set<CoreEdge> edgeSet) {
        this.roadNode = roadNode;
        this.edgeSet = edgeSet;
    }

    public Set<CoreEdge> getEdgeSet() {
        return this.edgeSet;
    }

    public void addEdge(CoreEdge coreEdge) {
        this.edgeSet.add(coreEdge);
    }

    public RoadNode getRoadNode() {
        return this.roadNode;
    }
}