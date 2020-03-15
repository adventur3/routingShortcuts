package shortcuts;

import astar.AStar;
import astar.AStarInfoNode;
import org.jgrapht.Graph;
import roadNetwork.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShortcutBorderAstar {

    public static Path singlePath(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
        if(start == target){
            return null;
        }
        RoadNode startBelong = start.getBelongTo();
        RoadNode targetBelong = target.getBelongTo();
        if(startBelong == targetBelong){
            return AStar.singlePath(g, start, target);
        }
        Path p1 = null;
        Path p2 = null;
        Path p3 = null;
        if(!start.isCore()){
            p1 = step1(g, start, target);
            startBelong = p1.getTargetNode();
        }
        if(startBelong == target){
            return p1;
        }else{
            if(startBelong == targetBelong){
                p2 = null;
                p1 = getLongestPathToRegin(target,p1);
                p3 = AStar.singlePath(g,  p1.getTargetNode(), target);
                return Path.pathCombine(p1,p3);
            }else{
                if(p1!=null){
                    p2 = startBelong.getCoreNode().getPath( targetBelong.getCoreNode());
                }else{
                    p2 = startBelong.getCoreNode().getPath(targetBelong.getCoreNode());
                }
                if(p2.getTargetNode() == target){
                    return Path.pathCombine(p1,p2);
                }
                p2 = getLongestPathToRegin(target,p2);
                if(p1!=null){
                    p3 = AStar.singlePath(g, p2.getTargetNode(), target);
                }else{
                    p3 = AStar.singlePath(g,  p2.getTargetNode(), target);
                }
            }
        }
        Path temp_p = Path.pathCombine(p1,p2);
        return Path.pathCombine(temp_p, p3);
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
            if(rn.getBelongTo()!=regionNode.getBelongTo()){
                thePath.addPathSegment(ps);
            }else{
                thePath.addPathSegment(ps);
                break;
            }
        }
        return thePath;
    }
}
