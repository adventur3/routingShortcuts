package simulatorTest;

import dijkstra.Dijkstra;
import dijkstra.InfoNode;
import org.dom4j.DocumentException;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import roadNetwork.*;
import shortcuts.ShortcutWithDijkstra;
import simulator.SimClock;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Set;

public class GraphTest {

    private static String GRAPH_FILE = "experimentData/core_choose_nums=4000_core_nums=50_graph.ser";

    public static void main(String[] args) throws IOException, DocumentException, java.lang.Exception {

        SimClock simClock = new SimClock(1553951724000L,1000);
        //create the road net
        Graph<RoadNode, RoadEdge> g = LoadMap.getMap(GRAPH_FILE);
        System.out.println("graph ok");
//    	RoadNode e1 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("1881181356")).findAny().get();
//    	RoadNode e2 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("2592412682")).findAny().get();

        RoadNode e1 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("4012919283")).findAny().get();
        RoadNode e2 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("1223212190")).findAny().get();

        Set<RoadEdge> edgeSet = g.edgesOf(e1);
//		RoadSegmentEdge edge = null;
//		Iterator itEdge = edgeSet.iterator();
//		while(itEdge.hasNext()) {
//			edge =  (RoadSegmentEdge) itEdge.next();
//			break;
//		}
//		List<Long> list = edge.getDistanceList();
//		Iterator it = list.iterator();
//		while(it.hasNext()) {
//			long speed = (long) it.next();
//			System.out.println(speed);
//		}
        Instant inst1 = Instant.now();
        testJgraphtDijkstra(g,e1,e2);
        Instant inst2 = Instant.now();
        testDijkstra(g,simClock,e1,e2);
        Instant inst3 = Instant.now();
        testShortcutRouting(g,simClock,e1,e2);
        Instant inst4 = Instant.now();
        System.out.println("T1="+ Duration.between(inst1, inst2).toMillis()+",T2="+Duration.between(inst2, inst3).toMillis()+",T3="+Duration.between(inst3,inst4).toMillis());
        System.out.println("输出完成！");



//		Instant inst1 = Instant.now();
//		testCoreRouting(g,e1,e2,simClock);
//		Instant inst2 = Instant.now();
//		System.out.println("T="+Duration.between(inst1, inst2).toMillis());

    }

    public static void testJgraphtDijkstra(Graph<RoadNode, RoadEdge> g,RoadNode e1,RoadNode e2){
        DijkstraShortestPath<RoadNode, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath(g);
        GraphPath<RoadNode,DefaultWeightedEdge> thepath = dijkstraAlg.getPath(e1, e2);
        //System.out.println("path:="+thepath);
        Iterator it = thepath.getVertexList().iterator();
        while(it.hasNext()) {
            RoadNode rnode = (RoadNode) it.next();
            System.out.println("**"+rnode.getOsmId()+"**");
        }
    }

    public static void testDijkstra(Graph<RoadNode, RoadEdge> g, SimClock clock, RoadNode e1, RoadNode e2){
        Path thepath = Dijkstra.singlePath(g, clock, e1, e2);
        //System.out.println("path:="+thepath);
        while(!thepath.isEmpty()) {
            PathSegment pathSegment = thepath.pollPathSegment();
            InfoNode inode = pathSegment.getEndNode();
            System.out.println("--"+inode.getRoadNode().getOsmId()+"--");
        }
    }

    public static void testShortcutRouting(Graph<RoadNode, RoadEdge> g, SimClock clock, RoadNode e1, RoadNode e2){
        Path thepath = ShortcutWithDijkstra.singlePath(g, clock, e1, e2);
        //System.out.println("path:="+thepath);
        while(!thepath.isEmpty()) {
            PathSegment pathSegment = thepath.pollPathSegment();
            InfoNode inode = pathSegment.getEndNode();
            System.out.println("++"+inode.getRoadNode().getOsmId()+"++");
        }
    }

}
