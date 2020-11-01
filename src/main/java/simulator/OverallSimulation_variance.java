package simulator;

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

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class OverallSimulation_variance {

    private static String GRAPH_FILE = "experimentData/Maps/doublePartition_graph_k=80.ser";
    //private static String REQUEST_FILE = "experimentData/trajectoryRequests.txt";
    private static String REQUEST_FILE = "experimentData/request/2014-07-01_08-09_request.txt";
    private static String PERFORMANCE_FILE_PATH = "experimentData/results/performance";
    private static String PERFORMANCE_FILE_SUFFIX = ".xls";

    public void simulate() throws Exception{
        //create the road net
        Graph<RoadNode, RoadEdge> g = LoadMap2.getMap(GRAPH_FILE);
        System.out.println("graph ok");
        RequestLoader requestLoader = new RequestLoader();
        requestLoader.loadRequest(REQUEST_FILE, g);

        Map<String, AlgorithmDetail> detailMap = new HashMap<String, AlgorithmDetail>();

        //RoadNode e1 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("1881181356")).findAny().get();
        //RoadNode e2 = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals("2592412682")).findAny().get();
        ShortcutHitRecorder shortcutHitRecorder = new ShortcutHitRecorder();
        //PerformanceRecorder performanceRecorder = new PerformanceRecorder(requestLoader.getRequestList().size());
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
            List<Double> list = new ArrayList<>();
            while(it.hasNext()){
                Request r = it.next();
                Instant inst1 = Instant.now();
                Path p = (Path)method.invoke(null, g, r.getStarttime(), r.getStart(), r.getTarget(), shortcutHitRecorder);
                Instant inst2 = Instant.now();
                double t = Duration.between(inst1, inst2).toMillis();
                list.add(t);
                //performanceRecorder.addLength(algorithmType, p.getWeight());
            }
            //performanceRecorder.addSearchTime(algorithmType, Duration.between(inst1, inst2).toMillis());
            System.out.println(algorithmType+":"+String.valueOf(computeVaricance(list)));
        }
        //Exporter exporter = new Exporter();
        //exporter.exportPerformance(PERFORMANCE_FILE_PATH+String.valueOf(System.currentTimeMillis()/1000)+PERFORMANCE_FILE_SUFFIX, performanceRecorder,shortcutHitRecorder);

        System.out.println("输出完成！");
    }

    public static double computeVaricance(List<Double> list){
        double sum = 0;
        for(int i=0;i<list.size();i++){
            sum+=list.get(i);
        }
        double mean = sum/list.size();
        double sum2 = 0;
        for(int i=0;i<list.size();i++){
            sum2+=Math.pow((list.get(i)-mean),2);
        }
        return sum2/list.size();
    }
}
