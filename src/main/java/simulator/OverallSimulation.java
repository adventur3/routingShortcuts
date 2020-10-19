package simulator;

import astar.AStar;
import dijkstra.Dijkstra;
import exporter.Exporter;
import org.jgrapht.Graph;
import recorder.AlgorithmDetail;
import recorder.PerformanceRecorder;
import recorder.ShortcutHitRecorder;
import roadNetwork.*;
import shortcuts.*;
import recorder.AlgorithmType;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/*
 * simulation on double partition graph
 */
public class OverallSimulation {

    private static String GRAPH_FILE = "experimentData/Maps/doublePartition_graph_k=30.ser";
    //private static String REQUEST_FILE = "experimentData/trajectoryRequests.txt";
    private static String REQUEST_FILE = "experimentData/request/2014-07-01-16_request.txt";
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
        PerformanceRecorder performanceRecorder = new PerformanceRecorder(requestLoader.getRequestList().size());
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

                if(algorithmType == AlgorithmType.TDA){
                    AlgorithmDetail ad = new AlgorithmDetail(r.getStart(),r.getTarget());
                    ad.setRealDistance(p.getWeight());
                    ad.setStartCore(r.getStart().getBelongTo_incoming());
                    ad.setTargetCore(r.getTarget().getBelongTo());
                    detailMap.put(r.getStart().getOsmId()+","+r.getTarget().getOsmId(),ad);
                }else if(algorithmType == AlgorithmType.AWS){
                    AlgorithmDetail ad = detailMap.get(r.getStart().getOsmId()+","+r.getTarget().getOsmId());
                    ad.setEstimateDistance(p.getWeight());
                    ad.setDifference(p.getWeight()-ad.getRealDistance());
                    detailMap.put(r.getStart().getOsmId()+","+r.getTarget().getOsmId(),ad);
                }

            }
            Instant inst2 = Instant.now();
            performanceRecorder.addSearchTime(algorithmType, Duration.between(inst1, inst2).toMillis());
        }

        Exporter exporter = new Exporter();
        exporter.exportPerformance(PERFORMANCE_FILE_PATH+String.valueOf(System.currentTimeMillis()/1000)+PERFORMANCE_FILE_SUFFIX, performanceRecorder,shortcutHitRecorder);

        System.out.println("输出完成！");


        PriorityQueue<AlgorithmDetail> pq = new PriorityQueue<AlgorithmDetail>(new Comparator<AlgorithmDetail>() {
            @Override
            public int compare(AlgorithmDetail o1, AlgorithmDetail o2) {
                return o1.getDifference() > o2.getDifference() ? -1:1;
            }
        });
        for(AlgorithmDetail ad:detailMap.values()){
            pq.add(ad);
        }
        while(!pq.isEmpty()){
            AlgorithmDetail ad = pq.poll();
            System.out.println(ad.getStart().getOsmId()+","+
                                ad.getTarget().getOsmId()+","+
                                ad.getStartCore().getOsmId()+","+
                                ad.getTargetCore().getOsmId()+","+
                                ad.getRealDistance()+","+
                                ad.getEstimateDistance()+","+
                                ad.getDifference());
        }
    }
}
