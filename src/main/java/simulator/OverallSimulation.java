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
import java.util.LinkedList;
import java.util.Map;

/*
 * simulation on double partition graph
 */
public class OverallSimulation {

    private static String GRAPH_FILE = "experimentData/Maps/doublePartition_graph_k=50.ser";
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

        LinkedList<Long> shortestLength = new LinkedList<Long>();
        LinkedList<Long> dwsLength = new LinkedList<Long>();
        LinkedList<Long> awsLength = new LinkedList<Long>();
        LinkedList<Long> aws_maLength = new LinkedList<Long>();
        LinkedList<Long> aws_hoeLength = new LinkedList<Long>();


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
                    long the_weight = p.getWeight();
                    shortestLength.add(the_weight);
                    tempWeight += the_weight;
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
                    long the_weight = p.getWeight();
                    dwsLength.add(the_weight);
                    tempWeight += the_weight;
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
                    long the_weight = p.getWeight();
                    awsLength.add(the_weight);
                    tempWeight += the_weight;
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
                    long the_weight = p.getWeight();
                    aws_maLength.add(the_weight);
                    tempWeight += the_weight;
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
                    long the_weight = p.getWeight();
                    aws_hoeLength.add(the_weight);
                    tempWeight += the_weight;
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
        double[] dwsResult = new double[4];
        double[] awsResult = new double[4];
        double[] aws_maResult = new double[4];
        double[] aws_hoeResult = new double[4];
        boolean isCorrect = minMaxVarianceOfDiff(shortestLength, dwsLength, dwsResult);
        if(!isCorrect){
            System.out.println("the sizes is not equal!");
        }
        isCorrect = minMaxVarianceOfDiff(shortestLength, awsLength, awsResult);
        if(!isCorrect){
            System.out.println("the sizes is not equal!");
        }
        isCorrect = minMaxVarianceOfDiff(shortestLength, aws_maLength, aws_maResult);
        if(!isCorrect){
            System.out.println("the sizes is not equal!");
        }
        isCorrect = minMaxVarianceOfDiff(shortestLength, aws_hoeLength, aws_hoeResult);
        if(!isCorrect){
            System.out.println("the sizes is not equal!");
        }

        System.out.println("Restrained Search Count DWS = "+recorder.getRestrainedSearchCount_DWS());
        System.out.println("Restrained Search Count AWS = "+recorder.getRestrainedSearchCount_AWS());
        System.out.println("Restrained Search Count AWS_HOE = "+recorder.getRestrainedSearchCount_AWS_HOE());
        System.out.println("Restrained Search Count AWS_MA = "+recorder.getRestrainedSearchCount_AWS_MA());
        System.out.println("Shortcuts Use Count AWS_MA = "+recorder.getShortcutUseCount_AWS_MA());

        System.out.println("Diff Of DWS: min = "+dwsResult[0]+", max = "+dwsResult[1]+", variance = "+ dwsResult[2]+", average = " + dwsResult[3]);
        System.out.println("Diff Of AWS: min = "+awsResult[0]+", max = "+awsResult[1]+", variance = "+ awsResult[2]+", average = " + awsResult[3]);
        System.out.println("Diff Of AWS_MA: min = "+aws_maResult[0]+", max = "+aws_maResult[1]+", variance = "+ aws_maResult[2]+", average = " + aws_maResult[3]);
        System.out.println("Diff Of AWS_HOE: min = "+aws_hoeResult[0]+", max = "+aws_hoeResult[1]+", variance = "+ aws_hoeResult[2]+", average = " + aws_hoeResult[3]);

        System.out.println("输出完成！");
    }

    public boolean minMaxVarianceOfDiff(LinkedList<Long> shortestLength, LinkedList<Long> otherLength, double[] result){
        if(shortestLength.size() != otherLength.size()){
            return false;
        }
        result[0] = 99999999;  //min
        result[1] = 0;         //max
        result[2] = 0;         //variance
        LinkedList<Long> diffList = new LinkedList<Long>();
        double sum = 0;
        for(int i=0;i<shortestLength.size();i++){
            long shortest = shortestLength.get(i)/1000;   //ms -> s
            long other = otherLength.get(i)/1000;   //ms -> s
            long diff = other - shortest;
            if(diff<0 && diff>-600){
                diff = 1;
            }
            diffList.add(diff);
            sum += diff;
            if(diff<result[0]) result[0] = diff;
            if(diff>result[1]) result[1] = diff;
        }
        double average = sum/diffList.size();
        for(int i=0;i<diffList.size();i++){
            result[2] += (diffList.get(i)- average) * (diffList.get(i)- average);
        }
        result[2] = result[2]/diffList.size();
        result[2] = Double.valueOf(String.format("%.2f", result[2] ));
        result[3] = Double.valueOf(String.format("%.2f", average ));
        return true;
    }
}
