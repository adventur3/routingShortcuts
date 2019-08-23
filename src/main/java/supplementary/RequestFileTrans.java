package supplementary;

import org.jgrapht.Graph;
import roadNetwork.LoadMap;
import roadNetwork.MillerCoordinate;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class RequestFileTrans {

    private static String REQUEST_FILE = "experimentData/request_orig.txt";
    private static String OUT_FILE = "experimentData/requests.txt";
    private static String GRAPH_FILE = "experimentData/core_choose_nums=4000_core_nums=50_graph.ser";

    public static void main(String args[]) throws java.io.IOException, java.lang.Exception{
        Graph<RoadNode, RoadEdge> g = LoadMap.getMap(GRAPH_FILE);
        File requestFile=new File(REQUEST_FILE);
        InputStreamReader read = new InputStreamReader(new FileInputStream(requestFile));
        BufferedReader bufferedReader = new BufferedReader(read);
        LinkedList<NodePair> nodePairs = new LinkedList<NodePair>();
        int count = 0;
        String lineTxt = "";
        while((lineTxt = bufferedReader.readLine()) != null){
            String s[]=lineTxt.split("#");
            double olon=Float.valueOf(s[0]);
            double olat=Float.valueOf(s[1]);
            double dlon=Float.valueOf(s[2]);
            double dlat=Float.valueOf(s[3]);
            RoadNode startNode = findNodeByCoord(g, olon, olat);
            RoadNode targetNode = findNodeByCoord(g, dlon, dlat);
            if(startNode == null){
                System.out.println("startNode null");
            }
            if(targetNode == null){
                System.out.println("targetNode null");
            }
            NodePair nodePair = new NodePair(startNode.getOsmId(), targetNode.getOsmId());
            nodePairs.add(nodePair);
            count ++;
            System.out.println("count=" + count);
        }


        File outputfile = new File(OUT_FILE);
        if(!outputfile.exists()) {
            outputfile.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(outputfile);
        String outStr = "";
        Iterator<NodePair> it = nodePairs.iterator();
        while(it.hasNext()){
            NodePair nodePair = it.next();
            outStr = nodePair.getStartId()+"#"+nodePair.getTargetId()+"\r\n";
            out.write(outStr.getBytes());
        }
    }

    public static RoadNode findNodeByCoord(Graph<RoadNode, RoadEdge> g, double lon, double lat){
        Set<RoadNode> nodeSet = g.vertexSet();
        Iterator<RoadNode> it = nodeSet.iterator();
        double minDistance = 99999999;
        RoadNode minNode = null;
        while(it.hasNext()){
            RoadNode roadNode = it.next();
            double tempDistance = MillerCoordinate.distance(lat,lon,roadNode.getLat(),roadNode.getLon());
            if(tempDistance < minDistance){
                minDistance = tempDistance;
                minNode = roadNode;
            }
        }
        return minNode;
    }

}


class NodePair{
    private String startId;
    private String targetId;

    public NodePair(){

    }

    public NodePair(String startId, String targetId){
        this.startId = startId;
        this.targetId = targetId;
    }

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
