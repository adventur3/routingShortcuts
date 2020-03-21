package simulator;

import astar.AStar;
import dijkstra.Dijkstra;
import org.dom4j.DocumentException;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import roadNetwork.*;
import shortcuts.DoublePartitionShortcut;
import shortcuts.ShortcutWithDijkstra;
import shortcuts.ShortcutsWithAStar;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

/*
 * simulation on double partition graph
 */
public class Simulator3 {

    private static String GRAPH_FILE = "experimentData/doublePartition_graph.ser";
    //private static String REQUEST_FILE = "experimentData/trajectoryRequests.txt";
    private static String REQUEST_FILE = "experimentData/gpxTrajRequests.txt";

    public static void main(String[] args) throws IOException, DocumentException, java.lang.Exception {

        SimClock simClock = new SimClock(1553951724000L,1000);
        //create the road net
        Graph<RoadNode, RoadEdge> g = LoadMap2.getMap(GRAPH_FILE);
        System.out.println("graph ok");
        RequestLoader requestLoader = new RequestLoader();
        requestLoader.loadRequest(REQUEST_FILE, g);

        //RoadNode e1 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("1881181356")).findAny().get();
        //RoadNode e2 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("2592412682")).findAny().get();

        //System.out.println("e1:outgoing core:"+e1.getBelongTo().getOsmId()+",incoming core:"+e1.getBelongTo_incoming().getOsmId());
        //System.out.println("e2:outgoing core:"+e2.getBelongTo().getOsmId()+",incoming core:"+e2.getBelongTo_incoming().getOsmId());

        long count1=0;
        long count2=0;
        long count3=0;
        long count4=0;
        long count5=0;

        Instant inst1 = Instant.now();
        Iterator<Request> it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
            testJgraphtDijkstra(g,r.getStart(),r.getTarget());
        }
        Instant inst2 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
//            testDijkstra(g,simClock,r.getStart(),r.getTarget());
//            Path p1 = Dijkstra.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p1 = Dijkstra.singlePath(g, r.getStart(), r.getTarget());
            count1 += p1.getWeight();
        }
        Instant inst3 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
//            testAStar(g,simClock,r.getStart(),r.getTarget());
//            Path p2 = AStar.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p2 = AStar.singlePath(g, r.getStart(), r.getTarget());
            count2 += p2.getWeight();
        }
        Instant inst4 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
//            testShortcutRouting(g,simClock,r.getStart(),r.getTarget());
//            Path p3 = ShortcutWithDijkstra.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p3 = ShortcutWithDijkstra.singlePath(g, r.getStart(), r.getTarget());
            count3 += p3.getWeight();
        }
        Instant inst5 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
            //Path p4 = ShortcutsWithAStar.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p4 = ShortcutsWithAStar.singlePath(g, r.getStart(), r.getTarget());
            if(p4!=null) {
                count4 += p4.getWeight();
            }
        }
        Instant inst6 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
            //Path p4 = ShortcutsWithAStar.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p5 = DoublePartitionShortcut.singlePath(g, r.getStart(), r.getTarget());
            if(p5!=null) {
                count5 += p5.getWeight();
            }
        }
        Instant inst7 = Instant.now();
        System.out.println("jgrapt dijkstra timecost:"+ Duration.between(inst1, inst2).toMillis());
        System.out.println("dijkstra timecost:" + Duration.between(inst2, inst3).toMillis());
        System.out.println("astar timecost:" + Duration.between(inst3, inst4).toMillis());
        System.out.println("shortcutwithdijkstra timecost:" + Duration.between(inst4, inst5).toMillis());
        System.out.println("shortcutwithastar timecost:" + Duration.between(inst5, inst6).toMillis());
        System.out.println("doublepartition timecost:" + Duration.between(inst6, inst7).toMillis());
        System.out.println("dijkstra weight:" + count1);
        System.out.println("astar weight:" + count2);
        System.out.println("shortcutwithdijkstra weight:" + count3);
        System.out.println("shortcutwithastar weight:" + count4);
        System.out.println("doublepartition weight:" + count5);
        System.out.println("输出完成！");

//		Instant inst1 = Instant.now();
//		testCoreRouting(g,e1,e2,simClock);
//		Instant inst2 = Instant.now();
//		System.out.println("T="+Duration.between(inst1, inst2).toMillis());

    }


    public static void testJgraphtDijkstra(Graph<RoadNode, RoadEdge> g, RoadNode e1, RoadNode e2){
        DijkstraShortestPath<RoadNode, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath(g);
        GraphPath<RoadNode,DefaultWeightedEdge> thepath = dijkstraAlg.getPath(e1, e2);
        //System.out.println("path:="+thepath);
        //Iterator it = thepath.getVertexList().iterator();
        //while(it.hasNext()) {
            //RoadNode rnode = (RoadNode) it.next();
            //System.out.println("jgra:"+rnode.getOsmId());
        //}
    }

    public static void testDijkstra(Graph<RoadNode, RoadEdge> g, SimClock clock, RoadNode e1, RoadNode e2){
        Path thepath = Dijkstra.timeDependentSinglePath(g, clock, e1, e2);
        System.out.println("path:="+thepath.getWeight());
        while(!thepath.isEmpty()) {
            PathSegment pathSegment = thepath.pollPathSegment();
            RoadNode node = pathSegment.getEndNode();
            System.out.println("dijk:"+node.getOsmId());
        }
    }

    public static void testAStar(Graph<RoadNode, RoadEdge> g, SimClock clock, RoadNode e1, RoadNode e2){
        Path thepath = AStar.timeDependentSinglePath(g, clock, e1, e2);
        System.out.println("path:="+thepath.getWeight());
        while(!thepath.isEmpty()) {
            PathSegment pathSegment = thepath.pollPathSegment();
            RoadNode node = pathSegment.getEndNode();
            System.out.println("asta:"+node.getOsmId());
        }
    }

    public static void testShortcutRouting(Graph<RoadNode, RoadEdge> g, SimClock clock, RoadNode e1, RoadNode e2){
        Path thepath = ShortcutWithDijkstra.timeDependentSinglePath(g, clock, e1, e2);
        //System.out.println("path:="+thepath);
        while(!thepath.isEmpty()) {
            PathSegment pathSegment = thepath.pollPathSegment();
            RoadNode node = pathSegment.getEndNode();
            System.out.println("shor:"+node.getOsmId());
        }
    }
}
