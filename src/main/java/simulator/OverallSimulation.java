package simulator;

import astar.AStar;
import dijkstra.Dijkstra;
import exporter.Exporter;
import org.jgrapht.Graph;
import recorder.PerformanceRecorder;
import recorder.ShortcutHitRecorder;
import roadNetwork.*;
import shortcuts.*;
import recorder.AlgorithmType;

import java.lang.reflect.Method;
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
    private static String REQUEST_FILE = "experimentData/request/2014-07-01-16_request.txt";
    private static String PERFORMANCE_FILE_PATH = "experimentData/results/performance";
    private static String PERFORMANCE_FILE_SUFFIX = ".xls";

    public void simulate() throws Exception{
        SimClock simClock = new SimClock(1553951724000L,1000);
        //long starttime = 1553951724000L;
        //create the road net
        Graph<RoadNode, RoadEdge> g = LoadMap2.getMap(GRAPH_FILE);
        System.out.println("graph ok");
        RequestLoader requestLoader = new RequestLoader();
        requestLoader.loadRequest(REQUEST_FILE, g);
        //RoadNode e1 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("1881181356")).findAny().get();
        //RoadNode e2 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("2592412682")).findAny().get();

        ShortcutHitRecorder shortcutHitRecorder = new ShortcutHitRecorder();
        PerformanceRecorder performanceRecorder = new PerformanceRecorder(requestLoader.getRequestList().size());

        //String[] algArray = new String[]{"TDA", "AWS", "AWS-HOD", "AWS-HOE", "AWS-MA"};

        //for(int i=0;i<algArray.length;i++){
        for(AlgorithmType algorithmType : AlgorithmType.values()){
            Class c = null;
            switch(algorithmType){
                case TDA: c = Class.forName("astar.AStar"); break;
                case AWS: c = Class.forName("shortcuts.AWS"); break;
                case AWS_HOD: c = Class.forName("shortcuts.AWS_HOD"); break;
                case AWS_HOE: c = Class.forName("shortcuts.AWS_HOE"); break;
                case AWS_MA: c = Class.forName("shortcuts.AWS_MA"); break;
            }
            Method method = c.getDeclaredMethod("timeDependentSinglePath", org.jgrapht.Graph.class, long.class, roadNetwork.RoadNode.class, roadNetwork.RoadNode.class, recorder.ShortcutHitRecorder.class);
            Iterator<Request> it = requestLoader.getRequestList().iterator();
            Instant inst1 = Instant.now();
            while(it.hasNext()){
                Request r = it.next();
                Path p = (Path)method.invoke(null, g, r.getStarttime(), r.getStart(), r.getTarget(), shortcutHitRecorder);
                performanceRecorder.addLength(algorithmType, p.getWeight());
            }
            Instant inst2 = Instant.now();
            performanceRecorder.addSearchTime(algorithmType, Duration.between(inst1, inst2).toMillis());


//            if(algorithmType == AlgorithmType.TDA){
//                Iterator<Request> it = requestLoader.getRequestList().iterator();
//                Instant inst1 = Instant.now();
//                while(it.hasNext()){
//                    Request r = it.next();
//                    Path p = AStar.timeDependentSinglePath(g, r.getStarttime(), r.getStart(), r.getTarget());
//                    performanceRecorder.addLength(algorithmType, p.getWeight());
//                }
//                Instant inst2 = Instant.now();
//                performanceRecorder.addSearchTime(AlgorithmType.TDA, Duration.between(inst1, inst2).toMillis());
//            }else if(algorithmType == AlgorithmType.AWS) {
//                Iterator<Request> it = requestLoader.getRequestList().iterator();
//                Instant inst1 = Instant.now();
//                while(it.hasNext()){
//                    Request r = it.next();
//                    Path p = AWS.timeDependentSinglePath(g, r.getStarttime(), r.getStart(), r.getTarget(), shortcutHitRecorder);
//                    performanceRecorder.addLength(algorithmType, p.getWeight());
//                }
//                Instant inst2 = Instant.now();
//                performanceRecorder.addSearchTime(AlgorithmType.AWS, Duration.between(inst1, inst2).toMillis());
//            }else if(algorithmType == AlgorithmType.AWS_HOD){
//                Iterator<Request> it = requestLoader.getRequestList().iterator();
//                Instant inst1 = Instant.now();
//                while(it.hasNext()){
//                    Request r = it.next();
//                    Path p = AWS_HOD.timeDependentSinglePath(g, r.getStarttime(), r.getStart(), r.getTarget(), shortcutHitRecorder);
//                    performanceRecorder.addLength(algorithmType, p.getWeight());
//                }
//                Instant inst2 = Instant.now();
//                performanceRecorder.addSearchTime(algorithmType, Duration.between(inst1, inst2).toMillis());
//            }else if(algorithmType == AlgorithmType.AWS_HOE){
//                Iterator<Request> it = requestLoader.getRequestList().iterator();
//                Instant inst1 = Instant.now();
//                while(it.hasNext()){
//                    Request r = it.next();
//                    Path p = AWS_HOE.timeDependentSinglePath(g, r.getStarttime(), r.getStart(), r.getTarget(), shortcutHitRecorder);
//                    performanceRecorder.addLength(algorithmType, p.getWeight());
//                }
//                Instant inst2 = Instant.now();
//                performanceRecorder.addSearchTime(algorithmType, Duration.between(inst1, inst2).toMillis());
//            }else if(algorithmType == AlgorithmType.AWS_MA){
//                Iterator<Request> it = requestLoader.getRequestList().iterator();
//                Instant inst1 = Instant.now();
//                while(it.hasNext()){
//                    Request r = it.next();
//                    Path p = AWS_MA.timeDependentSinglePath(g, r.getStarttime(), r.getStart(), r.getTarget(), shortcutHitRecorder);
//                    performanceRecorder.addLength(algorithmType, p.getWeight());
//                }
//                Instant inst2 = Instant.now();
//                performanceRecorder.addSearchTime(algorithmType, Duration.between(inst1, inst2).toMillis());
//            }


        }

        Exporter exporter = new Exporter();
        exporter.exportPerformance(PERFORMANCE_FILE_PATH+String.valueOf(System.currentTimeMillis()/1000)+PERFORMANCE_FILE_SUFFIX, performanceRecorder,shortcutHitRecorder);

//        System.out.println("Restrained Search Count DWS = "+ shortcutHitRecorder.getRestrainedSearchCount_DWS());
//        System.out.println("Restrained Search Count AWS = "+ shortcutHitRecorder.getRestrainedSearchCount_AWS());
//        System.out.println("Restrained Search Count AWS_HOE = "+ shortcutHitRecorder.getRestrainedSearchCount_AWS_HOE());
//        System.out.println("Restrained Search Count AWS_MA = "+ shortcutHitRecorder.getRestrainedSearchCount_AWS_MA());
//        System.out.println("Shortcuts Use Count AWS_MA = "+ shortcutHitRecorder.getShortcutUseCount_AWS_MA());

        System.out.println("输出完成！");
    }
}
