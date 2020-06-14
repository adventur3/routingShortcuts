package simulator;

import astar.AStar;
import dijkstra.Dijkstra;
import org.dom4j.DocumentException;
import org.jgrapht.Graph;
import roadNetwork.*;
import shortcuts.AWS;
import shortcuts.AWS_HOE;
import shortcuts.DWS;
import shortcuts.AWS_MA;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * simulation on double partition graph
 */
public class OverallSimulation {

    private static String GRAPH_FILE = "experimentData/Maps/doublePartition_graph_k=20.ser";
    //private static String REQUEST_FILE = "experimentData/trajectoryRequests.txt";
    private static String REQUEST_FILE = "experimentData/newGpxTrajRequests.txt";

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

        Map<String, Long> weightMap = new HashMap<String, Long>();
        Map<String, Long> searchTimeMap = new HashMap<String, Long>();

        Recorder recorder = new Recorder();

        String[] algArray = new String[]{"DIJKSTRA", "ASTAR", "DWS", "AWS", "AWS-MA", "AWS-HOE"};

        for(int i=0;i<algArray.length;i++){
            String algName = algArray[i];
            if(algName.equals("DIJKSTRA")){
                long tempWeight = 0;
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = Dijkstra.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget());
                    tempWeight += p.getWeight();
                }
                Instant inst2 = Instant.now();
                weightMap.put(algName+"_Weight", tempWeight);
                searchTimeMap.put(algName+"_Time", Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("ASTAR")){
                long tempWeight = 0;
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = AStar.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget());
                    tempWeight += p.getWeight();
                }
                Instant inst2 = Instant.now();
                weightMap.put(algName+"_Weight", tempWeight);
                searchTimeMap.put(algName+"_Time", Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("DWS")){
                long tempWeight = 0;
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = DWS.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget(), recorder);
                    tempWeight += p.getWeight();
                }
                Instant inst2 = Instant.now();
                weightMap.put(algName+"_Weight", tempWeight);
                searchTimeMap.put(algName+"_Time", Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("AWS")) {
                long tempWeight = 0;
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = AWS.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget(), recorder);
                    tempWeight += p.getWeight();
                }
                Instant inst2 = Instant.now();
                weightMap.put(algName+"_Weight", tempWeight);
                searchTimeMap.put(algName+"_Time", Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("AWS-MA")){
                long tempWeight = 0;
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = AWS_MA.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget(), recorder);
                    tempWeight += p.getWeight();
                }
                Instant inst2 = Instant.now();
                weightMap.put(algName+"_Weight", tempWeight);
                searchTimeMap.put(algName+"_Time", Duration.between(inst1, inst2).toMillis());
            }else if(algName.equals("AWS-HOE")){
                long tempWeight = 0;
                Iterator<Request> it = requestLoader.getRequestList().iterator();
                Instant inst1 = Instant.now();
                while(it.hasNext()){
                    Request r = it.next();
                    Path p = AWS_HOE.timeDependentSinglePath(g, starttime, r.getStart(), r.getTarget(), recorder);
                    tempWeight += p.getWeight();
                }
                Instant inst2 = Instant.now();
                weightMap.put(algName+"_Weight", tempWeight);
                searchTimeMap.put(algName+"_Time", Duration.between(inst1, inst2).toMillis());
            }
        }

        for(int i=0;i<algArray.length;i++){
            System.out.println(algArray[i]+"_Time=" + searchTimeMap.get(algArray[i]+"_Time"));
        }
        for(int i=0;i<algArray.length;i++){
            System.out.println(algArray[i]+"_Weight=" + weightMap.get(algArray[i]+"_Weight"));
        }
        System.out.println("Restrained Search Count DWS = "+recorder.getRestrainedSearchCount_DWS());
        System.out.println("Restrained Search Count AWS = "+recorder.getRestrainedSearchCount_AWS());
        System.out.println("Restrained Search Count AWS_HOE = "+recorder.getRestrainedSearchCount_AWS_HOE());
        System.out.println("Restrained Search Count AWS_MA = "+recorder.getRestrainedSearchCount_AWS_MA());
        System.out.println("Shortcuts Use Count AWS_MA = "+recorder.getShortcutUseCount_AWS_MA());
        System.out.println("输出完成！");
    }
}
