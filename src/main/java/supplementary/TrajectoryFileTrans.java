package supplementary;

import org.jgrapht.Graph;
import roadNetwork.LoadMap;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import simulator.Request;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;

/*
distance is time
 */
public class TrajectoryFileTrans {
    private static String REQUEST_FILE = "experimentData/2014-07-01-01_trans_2019-08-21-16-09-42_extraction.txt";
    private static String OUT_FILE = "experimentData/trajectoryRequests.txt";
    private static String GRAPH_FILE = "experimentData/core_choose_nums=4000_core_nums=50_graph.ser";

    public static void main(String args[]) throws java.io.IOException, java.lang.Exception{
        Graph<RoadNode, RoadEdge> g = LoadMap.getMap(GRAPH_FILE);
        File requestFile=new File(REQUEST_FILE);
        InputStreamReader read = new InputStreamReader(new FileInputStream(requestFile));
        BufferedReader bufferedReader = new BufferedReader(read);
        LinkedList<NodePairWithTime> nodePairs = new LinkedList<NodePairWithTime>();
        int count = 0;
        int count_pair = 0;
        int count_trajectory = 0;
        long count_weights = 0;
        int count_zero = 0;
        String lineTxt = "";
        NodePairWithTime nodePair = null;
        int node_null = 0;
        while((lineTxt = bufferedReader.readLine()) != null){
            if(count == 0){
                count ++;
                continue;
            }
            String s[]=lineTxt.split(",");
            String jqbh = s[1];
            if(jqbh.equals("0")){
                count_zero ++;
                if(count_pair == 0){
                    nodePair = new NodePairWithTime();
                    RoadNode startNode = RequestFileTrans.findNodeByCoord(g, Double.valueOf(s[2]), Double.valueOf(s[3]));
                    if(startNode!=null) {
                        nodePair.setStartId(startNode.getOsmId());
                        nodePair.setStartTime(Long.valueOf(s[5]));
                    }else{
                        node_null = 1;
                    }
                    count_pair++;
                }else if(count_pair == 1){
                    if(node_null!=1) {
                        RoadNode targetNode = RequestFileTrans.findNodeByCoord(g, Double.valueOf(s[2]), Double.valueOf(s[3]));
                        if(targetNode!=null) {
                            nodePair.setTargetId(targetNode.getOsmId());
                            nodePair.setTargetTime(Long.valueOf(s[5]));
                            nodePairs.add(nodePair);
                            long w = nodePair.getTargetTime() - nodePair.getStartTime();
                            if(w<0){
                                System.out.println("w="+w);
                                System.out.println(lineTxt);
                            }

                            count_weights += w;
                        }
                    }
                    node_null = 0;
                    count_pair = 0;
                    count_trajectory++;
                }
            }
            count ++;
            //System.out.println("count=" + count);
        }
        System.out.println("count_trajectory=" + count_trajectory);
        System.out.println("count_weights= " + count_weights);
        System.out.println("count_zero = " + count_zero);
        File outputfile = new File(OUT_FILE);
        if(!outputfile.exists()) {
            outputfile.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(outputfile);
        String outStr = "";
        Iterator<NodePairWithTime> it = nodePairs.iterator();
        while(it.hasNext()){
            NodePair n = it.next();
            outStr = n.getStartId()+"#"+n.getTargetId()+"\r\n";
            out.write(outStr.getBytes());
        }
    }
}

class NodePairWithTime extends NodePair{
    private long startTime;
    private long targetTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(long targetTime) {
        this.targetTime = targetTime;
    }
}
