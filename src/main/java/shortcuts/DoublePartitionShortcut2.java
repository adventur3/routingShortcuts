package shortcuts;

import astar.AStar;
import astar.RestrainedAStar;
import org.jgrapht.Graph;
import roadNetwork.Path;
import roadNetwork.PathSegment;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import simulator.SimClock;

import java.util.List;

/*
 * the start node's incoming core is also the target node's outgoing core then return true
 */
public class DoublePartitionShortcut2 {
    public static Boolean isSameCore(RoadNode start, RoadNode target) {
        if(start.getBelongTo_incoming() == target.getBelongTo()){
            return true;
        }
        else{
            return false;
        }
    }

    public static Path singlePath(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
        if(isSameCore(start, target)){
            return restrainedSearch(g, start, target);
        }else{
            return crossPartitionSearch(g, start, target);
        }
    }

    public static Path restrainedSearch(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
        RoadNode coreNode = start.getBelongTo_incoming();
        return RestrainedAStar.singlePath(g, start, target, coreNode);
    }

    public static Path crossPartitionSearch(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
        RoadNode startCore = start.getBelongTo_incoming();
        RoadNode targetCore = target.getBelongTo();
        Path path1 = RestrainedAStar.singlePath(g, start, startCore, startCore);
        Path path2 =startCore.getCoreNode().getPath(targetCore.getCoreNode());
        path2 = getLongestPathToRegin(target,path2);
        Path path3 = RestrainedAStar.singlePath(g, path2.getTargetNode(), target, targetCore);
        return Path.pathCombine(Path.pathCombine(path1,path2),path3);
    }

    public static Path getLongestPathToRegin(RoadNode regionNode, Path path){
        List<PathSegment> segmentList = path.getSegmentList();
        Path thePath = new Path();
        for(int i=0;i<segmentList.size();i++){
            PathSegment ps = segmentList.get(i);
            RoadNode rn = ps.getEndNode();
            if(rn.getBelongTo()!=regionNode.getBelongTo() && rn.getBelongTo_incoming()!=regionNode.getBelongTo_incoming()){
                thePath.addPathSegment(ps);
            }else{
                thePath.addPathSegment(ps);
                break;
            }
        }
        return thePath;
    }
}
