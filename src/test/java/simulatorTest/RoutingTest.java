package simulatorTest;

import astar.AStar;
import dijkstra.Dijkstra;
import roadNetwork.Path;
import org.dom4j.DocumentException;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import roadNetwork.*;
import shortcuts.ShortcutWithDijkstra;
import simulator.Request;
import simulator.RequestLoader;
import simulator.SimClock;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Set;

public class RoutingTest {

    private static String GRAPH_FILE = "experimentData/core_choose_nums=4000_core_nums=50_graph.ser";
    private static String REQUEST_FILE = "experimentData/requests1.txt";

    public static void main(String[] args) throws IOException, DocumentException, java.lang.Exception {

        SimClock simClock = new SimClock(1553951724000L,1000);
        //create the road net
        Graph<RoadNode, RoadEdge> g = LoadMap.getMap(GRAPH_FILE);
        System.out.println("graph ok");
        RequestLoader requestLoader = new RequestLoader();
        requestLoader.loadRequest(REQUEST_FILE, g);

        RoadNode e1 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("1881181356")).findAny().get();
        RoadNode e2 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("2592412682")).findAny().get();

        long count1=0;
        long count2=0;
        long count3=0;
        long count4=0;

        Instant inst1 = Instant.now();
        Iterator<Request> it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            count1++;
            Request r = it.next();
            testJgraphtDijkstra(g,r.getStart(),r.getTarget());
        }
        Instant inst2 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
            testDijkstra(g,simClock,r.getStart(),r.getTarget());
//            Path p1 = Dijkstra.singlePath(g, simClock, r.getStart(), r.getTarget());
//            if(p1.getWeight()<0){
//                System.out.println("count = "+count1+" weight="+p1.getWeight());
//            }
//            count2 += p1.getWeight();
        }
        Instant inst3 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
            testAStar(g,simClock,r.getStart(),r.getTarget());
//            Path p2 = AStar.singlePath(g, simClock, r.getStart(), r.getTarget());
//            count3 += p2.getWeight();
        }
        Instant inst4 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
            testShortcutRouting(g,simClock,r.getStart(),r.getTarget());
//            Path p3 = ShortcutWithDijkstra.singlePath(g, simClock, r.getStart(), r.getTarget());
//            count4 += p3.getWeight();
        }
        Instant inst5 = Instant.now();
        System.out.println("T1="+ Duration.between(inst1, inst2).toMillis()+",T2="+Duration.between(inst2, inst3).toMillis()+",T3="+Duration.between(inst3,inst4).toMillis()+",T4="+Duration.between(inst4,inst5).toMillis());
        System.out.println(count1+","+count2+","+count3+","+count4);
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
        Iterator it = thepath.getVertexList().iterator();
        while(it.hasNext()) {
            RoadNode rnode = (RoadNode) it.next();
            System.out.println("jgra:"+rnode.getOsmId());
        }
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
