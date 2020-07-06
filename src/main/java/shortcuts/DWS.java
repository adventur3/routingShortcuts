package shortcuts;

import dijkstra.Dijkstra;
import org.jgrapht.Graph;
import roadNetwork.Path;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import recorder.ShortcutHitRecorder;

/*
 * DIJKSTRA WITH SHORTCUTS
 */
public class DWS {
    /*
     * get the dijkstra shortest path from start to target
     */
    public static Path singlePath(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target) {
        RoadNode startBelong = start.getBelongTo();
        RoadNode targetBelong = target.getBelongTo();
        Path p1 = Dijkstra.singlePath(g, start, startBelong);
        Path p2 = null;
        if(p1!=null){
           p2 = startBelong.getCoreNode().getPath( targetBelong.getCoreNode());
        }else{
           p2 = startBelong.getCoreNode().getPath( targetBelong.getCoreNode());
        }
        //System.out.println("star id="+start.getOsmId()+" startBelong id="+startBelong.getOsmId()+" targetBelong id="+ targetBelong.getOsmId()+" target id="+target.getOsmId());
        Path p3 = null;
        if(p1==null&&p2==null){
            p3 = Dijkstra.singlePath(g, targetBelong, target);
        }else if(p1==null){
            p3 = Dijkstra.singlePath(g, targetBelong, target);
        }else if(p2==null){
            p3 = Dijkstra.singlePath(g, targetBelong, target);
        }else{
            p3 = Dijkstra.singlePath(g, targetBelong, target);
        }
        Path temp_p = Path.pathCombine(p1,p2);

        return Path.pathCombine(temp_p, p3);
    }

    public static Path timeDependentSinglePath(Graph<RoadNode, RoadEdge> g, long startTime, RoadNode start, RoadNode target, ShortcutHitRecorder shortcutHitRecorder) {
        RoadNode startBelong = start.getBelongTo();
        RoadNode targetBelong = target.getBelongTo();
        long starttime = startTime;
        Path p1 = Dijkstra.timeDependentSinglePath(g, starttime, start, startBelong);
        Path p2 = null;
        if(p1!=null){
            p2 = startBelong.getCoreNode().getPath(starttime+p1.getWeight(), targetBelong.getCoreNode());
        }else{
            p2 = startBelong.getCoreNode().getPath(starttime, targetBelong.getCoreNode());
        }
        //System.out.println("star id="+start.getOsmId()+" startBelong id="+startBelong.getOsmId()+" targetBelong id="+ targetBelong.getOsmId()+" target id="+target.getOsmId());
        Path p3 = null;
        if(p1==null&&p2==null){
            p3 = Dijkstra.timeDependentSinglePath(g, starttime, targetBelong, target);
            shortcutHitRecorder.restrainedSearchCount_DWS_AddOne();
        }else if(p1==null){
            p3 = Dijkstra.timeDependentSinglePath(g, starttime+p2.getWeight(), targetBelong, target);
        }else if(p2==null){
            p3 = Dijkstra.timeDependentSinglePath(g, starttime+p1.getWeight(), targetBelong, target);
            shortcutHitRecorder.restrainedSearchCount_DWS_AddOne();
        }else{
            p3 = Dijkstra.timeDependentSinglePath(g, starttime+p1.getWeight()+p2.getWeight(), targetBelong, target);
        }
        Path temp_p = Path.pathCombine(p1,p2);
        return Path.pathCombine(temp_p, p3);
    }
}
