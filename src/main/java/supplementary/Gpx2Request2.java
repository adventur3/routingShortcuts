package supplementary;

import org.jgrapht.Graph;
import roadNetwork.LoadMap;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import java.io.*;
import java.util.*;

/*
 * 将gpx轨迹映射到路网上, 轨迹距离长度由TrajectoryMapping计算
 * mapping the gpx trajectory onto road network, the length of trajectory is calculated by TrajectoryMapping
 */
public class Gpx2Request2 {

    private static String GPX_BASE_PATH = "experimentData/gpxData";
    private static String OUT_FILE = "experimentData/gpxTrajRequests2.txt";
    private static String GRAPH_FILE = "experimentData/core_choose_nums=4000_core_nums=50_graph.ser";

    public static void main(String args[]) throws java.io.IOException, java.lang.Exception{
        Graph<RoadNode, RoadEdge> g = LoadMap.getMap(GRAPH_FILE);
        LinkedList<NodePair> nodePairs = new LinkedList<NodePair>();
        long count_distance = 0;
        long count_weight = 0;
        long count = 0;
        long countFile = 0;
        File file = new File(GPX_BASE_PATH);
        if (!file.isDirectory()) {
            System.out.println("为文件或不存在");
            System.out.println("path=" + file.getPath());
            System.out.println("absolutepath=" + file.getAbsolutePath());
            System.out.println("name=" + file.getName());
        } else if (file.isDirectory()) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(GPX_BASE_PATH + File.separator + filelist[i]);
                if (!readfile.isDirectory() && !readfile.getName().equals(".DS_Store") && !readfile.getName().equals("._.DS_Store") ) {
                    countFile ++;
                    System.out.println("countFile="+countFile);
                    TrajectoryMappingResult result = TrajectoryMapping.mapping(readfile, g);
                    if(result != null){
                        System.out.println("one trajectory find!");
                        count ++;
                        count_distance += result.getPath().getWeight();
                        count_weight += (result.getEndTime() - result.getStartTime());
                        NodePair nodePair = new NodePair(result.getPath().getStartNode().getOsmId(),result.getPath().getTargetNode().getOsmId());
                        nodePairs.add(nodePair);
                    }
                }
            }
            System.out.println("count= " + count);
            System.out.println("count_weight= " + count_weight);
            System.out.println("count_distance = " + count_distance);
            File outputfile = new File(OUT_FILE);
            if(!outputfile.exists()) {
                outputfile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(outputfile);
            String outStr = "";
            Iterator<NodePair> it = nodePairs.iterator();
            while(it.hasNext()){
                NodePair n = it.next();
                outStr = n.getStartId()+"#"+n.getTargetId()+"\r\n";
                out.write(outStr.getBytes());
            }
        }

    }

}
