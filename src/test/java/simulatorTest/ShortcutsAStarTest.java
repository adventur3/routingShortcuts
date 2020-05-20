package simulatorTest;

import org.dom4j.DocumentException;
import org.jgrapht.Graph;
import roadNetwork.LoadMap;
import roadNetwork.Path;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import shortcuts.AWS_MA;
import simulator.Request;
import simulator.RequestLoader;
import simulator.SimClock;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

public class ShortcutsAStarTest {
    private static String GRAPH_FILE = "experimentData/core_choose_nums=4000_core_nums=50_graph.ser";
    private static String REQUEST_FILE = "experimentData/trajectoryRequests.txt";

    public static void main(String[] args) throws IOException, DocumentException, java.lang.Exception {

        SimClock simClock = new SimClock(1553951724000L,1000);
        //create the road net
        Graph<RoadNode, RoadEdge> g = LoadMap.getMap(GRAPH_FILE);
        System.out.println("graph ok");
        RequestLoader requestLoader = new RequestLoader();
        requestLoader.loadRequest(REQUEST_FILE, g);

        long count1=0;


//        Instant inst1 = Instant.now();
//        Iterator<Request> it = requestLoader.getRequestList().iterator();
//        while(it.hasNext()){
//            Request r = it.next();
//            Path p1 = AWS_MA.timeDependentSinglePath(g, simClock, r.getStart(), r.getTarget());
//            if(p1 == null || p1.getSegmentList().isEmpty()){
//                System.out.println("path null");
//                System.out.println(r.getStart().getOsmId()+"#"+r.getTarget().getOsmId());
//            }else if(p1.getStartNode()!= r.getStart()){
//                System.out.println("start wrong");
//                System.out.println(r.getStart().getOsmId()+"#"+r.getTarget().getOsmId());
//            }else if(p1.getTargetNode()!= r.getTarget()){
//                System.out.println("target wrong");
//                System.out.println(r.getStart().getOsmId()+"#"+r.getTarget().getOsmId());
//            }
//            if(p1!=null) {
//                count1 += p1.getWeight();
//            }
//        }
//        Instant inst2 = Instant.now();
//        System.out.println("shortcutwithastar timecost:"+ Duration.between(inst1, inst2).toMillis());
//
//        System.out.println("shortcutwithastar weight:" + count1);
//        System.out.println("输出完成！");

    }
}
