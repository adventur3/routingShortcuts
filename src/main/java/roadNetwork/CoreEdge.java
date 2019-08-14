package roadNetwork;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CoreEdge implements Serializable {
    private CoreNode start;
    private CoreNode target;
    private LinkedList<Path> timeDependentPathList;
    private long minWeight;

    public CoreEdge(CoreNode start, CoreNode target) {
        this.start = start;
        this.target = target;
        this.timeDependentPathList = new LinkedList();
    }
    public CoreEdge(CoreNode start, CoreNode target, LinkedList<Path> timeDependentPathList, long minWeight) {
        this.start = start;
        this.target = target;
        this.timeDependentPathList = timeDependentPathList;
        this.minWeight=minWeight;
    }

    public List<Path> getTimeDependentPathList(){
        return this.timeDependentPathList;
    }

    public CoreNode getStartNode() {
        return this.start;
    }

    public CoreNode getTargetNode() {
        return this.target;
    }

    public boolean isStartNode(CoreNode start) {
        return start == this.start;
    }

    public boolean isTargetNode(CoreNode target) {
        return target == this.target;
    }

    public long getMinWeight() {
        return this.minWeight;
    }
}
