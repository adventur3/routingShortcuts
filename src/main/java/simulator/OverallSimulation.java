package simulator;

import astar.AStar;
import dijkstra.Dijkstra;
import exporter.Exporter;
import org.jgrapht.Graph;
import recorder.PerformanceRecorder;
import recorder.ShortcutHitRecorder;
import roadNetwork.*;
import shortcuts.AWS;
import shortcuts.AWS_HOE;
import shortcuts.DWS;
import shortcuts.AWS_MA;
import recorder.AlgorithmType;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/*
 * simulation on double partition graph
 */
public class OverallSimulation {

    private static String GRAPH_FILE = "experimentData/Maps/doublePartition_graph_k=50.ser";
    //private static String REQUEST_FILE = "experimentData/trajectoryRequests.txt";
    private static String REQUEST_FILE = "experimentData/newGpxTrajRequests.txt";
    private static String PERFORMANCE_FILE_PATH = "experimentData/results/performance.xls";

    public void simulate() throws Exception{
        SimClock simClock = new SimClock(1553951724000L,1000);
        long starttime = 1553951724000L;
        //create the road net
        Graph<RoadNode, RoadEdge> g = LoadMap2.getMap(GRAPH_FILE);
        System.out.println("graph ok");
        RequestLoader requestLoader = new RequestLoader();
        requestLoader.loadRequest(REQUEST_FILE, g);
        //RoadNode e1 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("1881181356")).findAny().get();
        //RoadNode e2 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("2592412682")).findAny().get();

        ShortcutHitRecorder shortcutHitRecorder = new ShortcutHitRecorder();
        PerformanceRecorder performanceRecorder = new PerformanceRecorder(requestLoader.getRequestList().size());

        String[] algArray = new String[]{"DIJKSTRA", "ASTAR", "DWS", "AWS", "AWS-MA", "AWS-HOE"};

        for(int i=0;i<algArray.length;i++){
            String algName = algArray[i];
            if(algName.equals("DIJKSTRA")){
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = Dijkstra.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget());
                    performanceRecorder.addLength(AlgorithmType.DIJKSTRA, p.getWeight());
                }
                Instant inst2 = Instant.now();
                performanceRecorder.addSearchTime(AlgorithmType.DIJKSTRA, Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("ASTAR")){
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = AStar.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget());
                    performanceRecorder.addLength(AlgorithmType.ASTAR, p.getWeight());
                }
                Instant inst2 = Instant.now();
                performanceRecorder.addSearchTime(AlgorithmType.ASTAR, Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("DWS")){
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = DWS.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget(), shortcutHitRecorder);
                    performanceRecorder.addLength(AlgorithmType.DWS, p.getWeight());
                }
                Instant inst2 = Instant.now();
                performanceRecorder.addSearchTime(AlgorithmType.DWS, Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("AWS")) {
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = AWS.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget(), shortcutHitRecorder);
                    performanceRecorder.addLength(AlgorithmType.AWS, p.getWeight());
                }
                Instant inst2 = Instant.now();
                performanceRecorder.addSearchTime(AlgorithmType.AWS, Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("AWS-MA")){
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = AWS_MA.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget(), shortcutHitRecorder);
                    performanceRecorder.addLength(AlgorithmType.AWS_MA, p.getWeight());
                }
                Instant inst2 = Instant.now();
                performanceRecorder.addSearchTime(AlgorithmType.AWS_MA, Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("AWS-HOE")){
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = AWS_HOE.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget(), shortcutHitRecorder);
                    performanceRecorder.addLength(AlgorithmType.AWS_HOE, p.getWeight());
                }
                Instant inst2 = Instant.now();
                performanceRecorder.addSearchTime(AlgorithmType.AWS_HOE, Duration.between(inst1, inst2).toMillis());
            }
        }

        Exporter exporter = new Exporter();
        exporter.exportPerformance(PERFORMANCE_FILE_PATH, performanceRecorder);

        System.out.println("Restrained Search Count DWS = "+ shortcutHitRecorder.getRestrainedSearchCount_DWS());
        System.out.println("Restrained Search Count AWS = "+ shortcutHitRecorder.getRestrainedSearchCount_AWS());
        System.out.println("Restrained Search Count AWS_HOE = "+ shortcutHitRecorder.getRestrainedSearchCount_AWS_HOE());
        System.out.println("Restrained Search Count AWS_MA = "+ shortcutHitRecorder.getRestrainedSearchCount_AWS_MA());
        System.out.println("Shortcuts Use Count AWS_MA = "+ shortcutHitRecorder.getShortcutUseCount_AWS_MA());

        System.out.println("输出完成！");
    }
}
