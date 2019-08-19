package shortcuts;

import dijkstra.Dijkstra;
import dijkstra.InfoNode;
import org.jgrapht.Graph;
import roadNetwork.Path;
import roadNetwork.PathSegment;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import simulator.SimClock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ShortcutWithDijkstra {

    /*
     * get the dijkstra shortest path from start to target
     */
    public static Path singlePath(Graph<RoadNode, RoadEdge> g, SimClock simClock, RoadNode start, RoadNode target) {
        RoadNode startBelong = start.getBelongTo();
        RoadNode targetBelong = target.getBelongTo();
        long starttime = simClock.getNow();
        Path p1 = Dijkstra.singlePath(g, starttime, start, startBelong);
        Path p2 = startBelong.getCoreNode().getPath(starttime+p1.getWeight(), targetBelong.getCoreNode());
        //System.out.println("star id="+start.getOsmId()+" startBelong id="+startBelong.getOsmId()+" targetBelong id="+ targetBelong.getOsmId()+" target id="+target.getOsmId());
        Path p3 = null;
        if(p1==null&&p2==null){
            p3 = Dijkstra.singlePath(g, starttime, targetBelong, target);
        }else if(p1==null){
            p3 = Dijkstra.singlePath(g, starttime+p2.getWeight(), targetBelong, target);
        }else if(p2==null){
            p3 = Dijkstra.singlePath(g, starttime+p1.getWeight(), targetBelong, target);
        }else{
            p3 = Dijkstra.singlePath(g, starttime+p1.getWeight()+p2.getWeight(), targetBelong, target);
        }

        Path temp_p = Path.pathCombine(p1,p2);

//        while(!p1.isEmpty()) {
//            PathSegment pathSegment = p1.pollPathSegment();
//            InfoNode inode = pathSegment.getEndNode();
//            System.out.println("-p1-"+inode.getRoadNode().getOsmId()+"-p1-");
//        }
//
//        while(!p2.isEmpty()) {
//            PathSegment pathSegment = p2.pollPathSegment();
//            InfoNode inode = pathSegment.getEndNode();
//            System.out.println("-p2-"+inode.getRoadNode().getOsmId()+"-p2-");
//        }
//
//        while(!p3.isEmpty()) {
//            PathSegment pathSegment = p3.pollPathSegment();
//            InfoNode inode = pathSegment.getEndNode();
//            System.out.println("-p3-"+inode.getRoadNode().getOsmId()+"-p3-");
//        }


        return Path.pathCombine(temp_p, p3);
    }

}
