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

//    public static Path crossPartitionSearch(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
//        RoadNode startCore = start.getBelongTo_incoming();
//        RoadNode targetCore = target.getBelongTo();
//        Path path1 = RestrainedAStar.singlePath(g, start, startCore, startCore);
//        Path path2 =startCore.getCoreNode().getPath(targetCore.getCoreNode());
//        path2 = getLongestPathToRegin(target,path2);
//        Path path3 = RestrainedAStar.singlePath(g, path2.getTargetNode(), target, targetCore);
//        return Path.pathCombine(Path.pathCombine(path1,path2),path3);
//    }

        public static Path crossPartitionSearch(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
        RoadNode startCore = start.getBelongTo_incoming();
        RoadNode targetCore = target.getBelongTo();
        Path path1 = null;
        if(!start.isCore()){
            path1 = step1(g, start, target);
            if(path1==null){
                return null;
            }
            startCore = path1.getTargetNode();
        }
        if(startCore == target){
            return path1;
        }
        Path path2 =startCore.getCoreNode().getPath(targetCore.getCoreNode());
        if(path2==null){
            Path path3 = RestrainedAStar.singlePath(g, targetCore, target, targetCore);
            return Path.pathCombine(Path.pathCombine(path1,path2),path3);
        }else{
            path2 = getLongestPathToRegin(target,path2);
            Path path3 = RestrainedAStar.singlePath(g, path2.getTargetNode(), target, targetCore);
            return Path.pathCombine(Path.pathCombine(path1,path2),path3);
        }
    }


    public static Path step1(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
        if(start.isCore()){
            return null;
        }else{
            Map<String, AStarInfoNode> infoNodes=new HashMap<String,AStarInfoNode>();  //为了根据roadNode找到infoNode
            LinkedList<AStarInfoNode> priorityQueue = new LinkedList<AStarInfoNode>();
            long estimatedWeight = (long) (MillerCoordinate.distance(start, target));
            AStarInfoNode startInfo = new AStarInfoNode(start, null, target, 0, estimatedWeight);
            infoNodes.put(start.getOsmId(),startInfo);
            priorityQueue.add(startInfo);
            startInfo.setExplored();

            Path path =null;
            while(!priorityQueue.isEmpty()) {
                AStarInfoNode inode = priorityQueue.pollFirst();
                inode.setSetted();
                if (inode.getRoadNode() == target) {
                    path = AStar.outShortestPath(infoNodes, start, target);
                    return path;
                }
                if (inode.getRoadNode().isCore()) {
                    path = AStar.outShortestPath(infoNodes, start, inode.getRoadNode());
                    return path;
                }
                AStar.updateForwardPriorityQueue(g, infoNodes, priorityQueue, inode, target);
            }
        }
        return null;
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
