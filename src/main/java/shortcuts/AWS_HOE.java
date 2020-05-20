package shortcuts;

import astar.AStar;
import astar.AStarInfoNode;
import astar.RestrainedAStar;
import org.jgrapht.Graph;
import roadNetwork.*;
import simulator.SimClock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * AWS-HOP OFF EARLY
 */
public class AWS_HOE {
    /*
     * the start node's incoming core is also the target node's outgoing core then return true
     */
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

    public static Path timeDependentSinglePath(Graph<RoadNode, RoadEdge> g, long time, RoadNode start, RoadNode target){
        if(isSameCore(start, target)){
            return timeDependentRestrainedSearch(g, time, start, target);
        }else{
            return timeDependentCrossPartitionSearch(g, time, start, target);
        }
    }

    public static Path restrainedSearch(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
        RoadNode coreNode = start.getBelongTo_incoming();
        return RestrainedAStar.singlePath(g, start, target, coreNode);
    }

    public static Path timeDependentRestrainedSearch(Graph<RoadNode, RoadEdge> g, long time, RoadNode start, RoadNode target){
        RoadNode coreNode = start.getBelongTo_incoming();
        return RestrainedAStar.timeDependentSinglePath(g, time, start, target, coreNode);
    }

    public static Path crossPartitionSearch(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
        RoadNode startCore = start.getBelongTo_incoming();
        RoadNode targetCore = target.getBelongTo();
        Path path1 = RestrainedAStar.singlePath(g, start, startCore, startCore);
        Path path2 =startCore.getCoreNode().getPath(targetCore.getCoreNode());
        path2 = getLongestPathToRegin(target, path2);
        Path path3 = AStar.singlePath(g, path2.getTargetNode(), target);
        return Path.pathCombine(Path.pathCombine(path1,path2),path3);
    }

    public static Path timeDependentCrossPartitionSearch(Graph<RoadNode, RoadEdge> g, long time, RoadNode start, RoadNode target){
        long starttime = time;
        RoadNode startCore = start.getBelongTo_incoming();
        RoadNode targetCore = target.getBelongTo();
        Path path1 = RestrainedAStar.timeDependentSinglePath(g, starttime, start, startCore, startCore);
        Path path2 =startCore.getCoreNode().getPath(starttime+path1.getWeight(), targetCore.getCoreNode());
        path2 = getLongestPathToRegin(target, path2);
        Path path3 = AStar.timeDependentSinglePath(g, starttime+path1.getWeight()+path2.getWeight(), path2.getTargetNode(), target);
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
