package shortcuts;

import dijkstra.Dijkstra;
import dijkstra.InfoNode;
import org.jgrapht.Graph;
import roadNetwork.Path;
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
        RoadNode targeBelong = target.getBelongTo();
        long starttime = simClock.getNow();
        Path p1 = Dijkstra.singlePath(g, starttime, start, startBelong);
        Path p2 = startBelong.getCoreNode().getPath(starttime+p1.getWeight(), targeBelong.getCoreNode());
        Path p3 = Dijkstra.singlePath(g, starttime+p1.getWeight()+p2.getWeight(), targeBelong, target);
        Path temp_p = Path.pathCombine(p1,p2);
        return Path.pathCombine(temp_p, p3);
    }

}
