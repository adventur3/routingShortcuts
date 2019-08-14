package roadNetwork;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/*
 * path of the road net
 */
public class Path implements Serializable {

    private static final long serialVersionUID = 7780355356501363789L;

    private LinkedList<PathSegment> segmentList;
    private long weight;

    public Path() {
        segmentList = new LinkedList<PathSegment>();
        this.weight = 0;
    }

    public void addPathSegmentFirst(PathSegment pathSegment) {
        segmentList.addFirst(pathSegment);
        this.weight += pathSegment.getWeight();
    }

    public PathSegment pollPathSegment() {
        PathSegment pathSegment = segmentList.poll();
        this.weight -= pathSegment.getWeight();
        return pathSegment;
    }

    public boolean isEmpty() {
        return this.segmentList.isEmpty();
    }

    public long getWeight() {
        return this.weight;
    }

    public List<PathSegment> getSegmentList(){
        return this.segmentList;
    }

}


