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
import shortcuts.DoublePartitionShortcut2;
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
    private static String REQUEST_FILE = "experimentData/newGpxTrajRequests.txt";

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

        long dijkstraWeight = 0;
        long astarWeight = 0;
        long dijkstraWithShortcutWeight = 0;
        long astarWithShortcutWeight = 0;
        long awsWeight = 0;
        long aws_hoeWeight = 0;


        Iterator<Request> it = requestLoader.getRequestList().iterator();
        Instant inst1 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
//            Path p1 = Dijkstra.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p1 = Dijkstra.singlePath(g, r.getStart(), r.getTarget());
            dijkstraWeight += p1.getWeight();
        }
        Instant inst2 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
//            Path p2 = AStar.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p2 = AStar.singlePath(g, r.getStart(), r.getTarget());
            astarWeight += p2.getWeight();
        }
        Instant inst3 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
//            Path p3 = ShortcutWithDijkstra.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p3 = ShortcutWithDijkstra.singlePath(g, r.getStart(), r.getTarget());
            dijkstraWithShortcutWeight += p3.getWeight();
        }
        Instant inst4 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
            //Path p4 = ShortcutsWithAStar.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p4 = ShortcutsWithAStar.singlePath(g, r.getStart(), r.getTarget());
            astarWithShortcutWeight += p4.getWeight();
        }
        Instant inst5 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
            //Path p4 = ShortcutsWithAStar.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p5 = DoublePartitionShortcut.singlePath(g, r.getStart(), r.getTarget());
            awsWeight += p5.getWeight();
        }
        Instant inst6 = Instant.now();
        it = requestLoader.getRequestList().iterator();
        while(it.hasNext()){
            Request r = it.next();
            //Path p4 = ShortcutsWithAStar.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
            Path p6 =  DoublePartitionShortcut2.singlePath(g, r.getStart(), r.getTarget());
            aws_hoeWeight += p6.getWeight();
        }
        Instant inst7 = Instant.now();
        System.out.println("dijkstra timecost:" + Duration.between(inst1, inst2).toMillis());
        System.out.println("astar timecost:" + Duration.between(inst2, inst3).toMillis());
        System.out.println("shortcutwithdijkstra timecost:" + Duration.between(inst3, inst4).toMillis());
        System.out.println("shortcutwithastar timecost:" + Duration.between(inst4, inst5).toMillis());
        System.out.println("aws timecost:" + Duration.between(inst5, inst6).toMillis());
        System.out.println("aws-hoe timecost:" + Duration.between(inst6, inst7).toMillis());
        System.out.println("dijkstra weight:" + dijkstraWeight);
        System.out.println("astar weight:" + astarWeight);
        System.out.println("shortcutwithdijkstra weight:" + dijkstraWithShortcutWeight);
        System.out.println("shortcutwithastar weight:" + astarWithShortcutWeight);
        System.out.println("aws weight:" + awsWeight);
        System.out.println("aws-hoe weight:" + aws_hoeWeight);
        System.out.println("输出完成！");
    }
}
