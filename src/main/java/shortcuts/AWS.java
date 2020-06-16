package shortcuts;

import astar.AStar;
import astar.RestrainedAStar;
import org.jgrapht.Graph;
import roadNetwork.Path;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import simulator.Recorder;
import simulator.SimClock;

/*
 * AWS:AStar With Shortcuts
 */
public class AWS {
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

    public static Path timeDependentSinglePath(Graph<RoadNode, RoadEdge> g, long time, RoadNode start, RoadNode target, Recorder recorder){
        if(isSameCore(start, target)){
            recorder.restrainedSearchCount_AWS_AddOne();
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
        //Path path1 = AStar.singlePath(g, start, startCore);
        Path path2 =startCore.getCoreNode().getPath(targetCore.getCoreNode());
        Path path3 = RestrainedAStar.singlePath(g, targetCore, target, targetCore);
        //Path path3 = AStar.singlePath(g, targetCore, target);
        return Path.pathCombine(Path.pathCombine(path1,path2),path3);
    }

    public static Path timeDependentCrossPartitionSearch(Graph<RoadNode, RoadEdge> g, long time, RoadNode start, RoadNode target){
        long starttime = time;
        RoadNode startCore = start.getBelongTo_incoming();
        RoadNode targetCore = target.getBelongTo();
        Path path1 = RestrainedAStar.timeDependentSinglePath(g, starttime, start, startCore, startCore);
        Path path2 =startCore.getCoreNode().getPath(starttime+path1.getWeight(), targetCore.getCoreNode());
        Path path3 = RestrainedAStar.timeDependentSinglePath(g, starttime+path1.getWeight()+path2.getWeight(), targetCore, target, targetCore);
        //Path path3 = AStar.singlePath(g, targetCore, target);
        return Path.pathCombine(Path.pathCombine(path1,path2),path3);
    }
}