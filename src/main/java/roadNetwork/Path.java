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

    public void addPathSegment(PathSegment pathSegment) {
        segmentList.add(pathSegment);
        this.weight += pathSegment.getWeight();
    }

    public PathSegment pollPathSegment() {
        PathSegment pathSegment = segmentList.poll();
        this.weight -= pathSegment.getWeight();
        return pathSegment;
    }

    public RoadNode getTargetNode(){
        PathSegment ps = segmentList.getLast();
        RoadNode target = ps.getEndNode();
        return target;
    }

    public void setWeight(long weight){
        this.weight = weight;
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

    public static Path pathCombine(Path p1, Path p2){
        Path path = new Path();
        if(p1!=null&&p2!=null){
            path.getSegmentList().addAll(p1.getSegmentList());
            path.getSegmentList().addAll(p2.getSegmentList());
            path.setWeight(p1.getWeight()+p2.getWeight());
        }else if(p1!=null&&p2==null){
            path.getSegmentList().addAll(p1.getSegmentList());
            path.setWeight(p1.getWeight());
        }else if(p1==null&&p2!=null){
            path.getSegmentList().addAll(p2.getSegmentList());
            path.setWeight(p2.getWeight());
        }
        return path;
    }

}


