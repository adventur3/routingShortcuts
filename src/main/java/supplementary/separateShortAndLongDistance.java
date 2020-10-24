package supplementary;

import dijkstra.Dijkstra;
import exporter.Exporter;
import org.jgrapht.Graph;
import recorder.AlgorithmDetail;
import recorder.AlgorithmType;
import recorder.PerformanceRecorder;
import recorder.ShortcutHitRecorder;
import roadNetwork.LoadMap2;
import roadNetwork.Path;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import simulator.Request;
import simulator.RequestLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class separateShortAndLongDistance {
    private static String GRAPH_FILE = "experimentData/Maps/doublePartition_graph_k=30.ser";
    //private static String REQUEST_FILE = "experimentData/trajectoryRequests.txt";
    private static String REQUEST_FILE = "experimentData/request/2014-07-01_18-19_request.txt";
    private static String SHORT_REQUEST_FILE = "experimentData/shortAndLongRequest/shortRequest";
    private static String LONG_REQUEST_FILE = "experimentData/shortAndLongRequest/longRequest";

    public static void main(String[] args) throws Exception{
        //create the road net
        Graph<RoadNode, RoadEdge> g = LoadMap2.getMap(GRAPH_FILE);
        System.out.println("graph ok");
        RequestLoader requestLoader = new RequestLoader();
        requestLoader.loadRequest(REQUEST_FILE, g);

        Map<String, AlgorithmDetail> detailMap = new HashMap<String, AlgorithmDetail>();
        List<Request> shortList  = new ArrayList<>();
        List<Request> longList = new ArrayList<>();
        //RoadNode e1 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("1881181356")).findAny().get();
        //RoadNode e2 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("2592412682")).findAny().get();
        Iterator<Request> it = requestLoader.getRequestList().iterator();
        Instant inst1 = Instant.now();
        while(it.hasNext()){
            Request r = it.next();
            Path p = (Path) Dijkstra.singlePath(g,r.getStart(),r.getTarget());
            long length = p.getWeight();
            if(length<=3000){
                shortList.add(r);
            }else{
                longList.add(r);
            }
        }
        Instant inst2 = Instant.now();

        //Exporter exporter = new Exporter();
        //exporter.exportPerformance(PERFORMANCE_FILE_PATH+String.valueOf(System.currentTimeMillis()/1000)+PERFORMANCE_FILE_SUFFIX, performanceRecorder,shortcutHitRecorder);

        File outputfile = new File(SHORT_REQUEST_FILE+String.valueOf(System.currentTimeMillis()/1000)+".txt");
        if(!outputfile.exists()) {
            outputfile.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(outputfile);
        String outStr = "";
        Iterator<Request> shortit = shortList.iterator();
        while(shortit.hasNext()) {
            Request r = shortit.next();
            outStr = r.getStart().getOsmId() + "#" + r.getTarget().getOsmId() + "#" + r.getStarttime() +"\r\n";
            out.write(outStr.getBytes());
        }
        File outputfile2 = new File(LONG_REQUEST_FILE+String.valueOf(System.currentTimeMillis()/1000)+".txt");
        if(!outputfile2.exists()) {
            outputfile2.createNewFile();
        }
        FileOutputStream out2 = new FileOutputStream(outputfile2);
        String outStr2 = "";
        Iterator<Request> longit = longList.iterator();
        while(longit.hasNext()) {
            Request r = longit.next();
            outStr2 = r.getStart().getOsmId() + "#" + r.getTarget().getOsmId() + "#" + r.getStarttime() +"\r\n";
            out2.write(outStr2.getBytes());
        }
    }

}
