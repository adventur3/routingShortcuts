package supplementary;

import astar.AStar;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import roadNetwork.*;

import java.io.*;
import java.sql.*;
import java.util.*;

/*
    generate outgoing partition and incoming partition, the results are written to files
 */
public class DoPartition {
    private static String URL="jdbc:mysql://localhost:3306/road_network?useUnicode=true&characterEncoding=UTF8&useSSL=false";
    private static String USER="root";
    private static String PASSWORD="mocom123";
    //private static int CORE_CHOOSE_NUMS=4000;
    private static int CORE_NUM=50;
    public static String GRAPH_INFORMATION="experimentData/originGraph/origin_graph.ser"; //初始的 尚未加入core节点和分区的地图
    public static String CORE_NODE_FILE="experimentData/coreNodes/k="+CORE_NUM+".txt";
    public static String OUTGOING_FILE="experimentData/belongings/outgoingBelong_k="+CORE_NUM+".txt";
    public static String INCOMING_FILE="experimentData/belongings/incomingBelong_k="+CORE_NUM+".txt";

    static Comparator<TempNode> cmp = new Comparator<TempNode>() {
        public int compare(TempNode e1, TempNode e2) {
            return (int)(e1.getLength() - e2.getLength());
        }
    };

    public static void main(String[] args) throws Exception{
        //read core nodes information from file
        Set<String> coreInfo = readCoreInfo(CORE_NODE_FILE);
        //init the graph(not time-dependent)
        Graph<RoadNode, RoadEdge> g = initRoadNetwork();
        //set core nodes
        List<RoadNode> coreNodes = setCoreNodes(g, coreInfo);
        outgoingPartition(g, coreNodes, OUTGOING_FILE);
        //incomingPartition(g, coreNodes, INCOMING_FILE);
    }

    public static void outgoingPartition(Graph<RoadNode, RoadEdge> g, List<RoadNode> coreNodes, String fileName) throws Exception{
        Map<RoadNode, PriorityQueue<TempNode>> map = new HashMap<RoadNode, PriorityQueue<TempNode>>();
        Map<String, Integer> numMap = new HashMap<String, Integer>();
        Map<String, Boolean> finishFlag = new HashMap<String, Boolean>();
        priQueueInit(g,coreNodes,map);
        for(Map.Entry<RoadNode, PriorityQueue<TempNode>> entry:map.entrySet()){
            String id = entry.getKey().getOsmId();
            numMap.put(id, 0);
            finishFlag.put(id, false);
        }
        while(!isFinish(finishFlag)){
            for(Map.Entry<RoadNode, PriorityQueue<TempNode>> entry:map.entrySet()){
                RoadNode coreNode = entry.getKey();
                String coreId = coreNode.getOsmId();
                if(!finishFlag.get(coreId)){
                    PriorityQueue<TempNode> queue = entry.getValue();
                    RoadNode setNode = null;
                    while(setNode==null && !queue.isEmpty()){
                        TempNode tempNode = queue.poll();
                        if(tempNode.getRoadNode().getBelongTo()==null ){
                            setNode = tempNode.getRoadNode();
                            setNode.setBelongTo(coreNode);
                            addOutgoingNeighbor(g, queue, setNode);
                            int num = numMap.get(coreId);
                            numMap.put(coreId, num+1);
                            break;
                        }
                    }
                    if(setNode == null){
                        finishFlag.put(coreId,true);
                    }
                }
            }
        }
        System.out.println(numMap);
        int totalNum = 0;
        for(Map.Entry<String, Integer> entry:numMap.entrySet()){
            totalNum += entry.getValue();
        }
        System.out.println("outgoingPartition:toatalNum="+totalNum+",vertexNum="+g.vertexSet().size());
        //write partition infomation to file
        FileWriter writer=new FileWriter(fileName);
        for(RoadNode node:g.vertexSet()){
            writer.write(node.getOsmId()+":"+node.getBelongTo().getOsmId()+"\n");
        }
        writer.close();
    }

    public static PriorityQueue<TempNode> addOutgoingNeighbor(Graph<RoadNode, RoadEdge> g, PriorityQueue<TempNode> nodeList, RoadNode n){
        Set<RoadEdge> edgeSet = g.outgoingEdgesOf(n);
        Iterator<RoadEdge> it = edgeSet.iterator();
        while(it.hasNext()){
            RoadEdge edge = it.next();
            RoadNode node = g.getEdgeTarget(edge);
            if(node.getBelongTo()==null){
                boolean addFlag = true;
                boolean changeLength = false;
                TempNode changeNode = null;
                for(TempNode tempNode:nodeList){
                    if(tempNode.getRoadNode() == node){
                        addFlag = false;
                        if(edge.getLength() < tempNode.getLength()){
                            changeLength = true;
                            changeNode = tempNode;
                        }
                        break;
                    }
                }
                //add to priorityQueue
                if(addFlag == true){
                    TempNode tempNode = new TempNode(node,edge.getLength());
                    nodeList.add(tempNode);
                }
                if(changeLength == true){
                    nodeList.remove(changeNode);
                    changeNode.setLength(edge.getLength());
                    nodeList.add(changeNode);
                }
            }
        }
        return nodeList;
    }

    public static void priQueueInit(Graph<RoadNode, RoadEdge> g, List<RoadNode> coreNodes, Map <RoadNode, PriorityQueue<TempNode>> map){
        Iterator<RoadNode> it = coreNodes.iterator();
        while(it.hasNext()){
            RoadNode start = it.next();
            PriorityQueue<TempNode> nodeList = new PriorityQueue<TempNode>(cmp);
            TempNode tempNode = new TempNode(start,0);
            nodeList.add(tempNode);
            map.put(start,nodeList);
        }
    }

    public static void incomingPartition(Graph<RoadNode, RoadEdge> g, List<RoadNode> coreNodes, String fileName) throws Exception{
        Map<RoadNode, PriorityQueue<TempNode>> map = new HashMap<RoadNode, PriorityQueue<TempNode>>();
        Map<String, Integer> numMap = new HashMap<String, Integer>();
        Map<String, Boolean> finishFlag = new HashMap<String, Boolean>();
        priQueueInit(g,coreNodes,map);
        for(Map.Entry<RoadNode, PriorityQueue<TempNode>> entry:map.entrySet()){
            String id = entry.getKey().getOsmId();
            numMap.put(id, 0);
            finishFlag.put(id, false);
        }
        while(!isFinish(finishFlag)){
            for(Map.Entry<RoadNode, PriorityQueue<TempNode>> entry:map.entrySet()){
                RoadNode coreNode = entry.getKey();
                String coreId = coreNode.getOsmId();
                if(!finishFlag.get(coreId)){
                    PriorityQueue<TempNode> queue = entry.getValue();
                    RoadNode setNode = null;
                    while(setNode==null && !queue.isEmpty()){
                        TempNode tempNode = queue.poll();
                        if(tempNode.getRoadNode().getBelongTo()==null ){
                            setNode = tempNode.getRoadNode();
                            setNode.setBelongTo(coreNode);
                            addIncomingNeighbor(g, queue, setNode);
                            int num = numMap.get(coreId);
                            numMap.put(coreId, num+1);
                            break;
                        }
                    }
                    if(setNode == null){
                        finishFlag.put(coreId,true);
                    }
                }
            }
        }
        System.out.println(numMap);
        int totalNum = 0;
        for(Map.Entry<String, Integer> entry:numMap.entrySet()){
            totalNum += entry.getValue();
        }
        System.out.println("incomingPartition:toatalNum="+totalNum+",vertexNum="+g.vertexSet().size());
        //write partition infomation to file
        FileWriter writer=new FileWriter(fileName);
        for(RoadNode node:g.vertexSet()){
            writer.write(node.getOsmId()+":"+node.getBelongTo().getOsmId()+"\n");
        }
        writer.close();
    }

    public static PriorityQueue<TempNode> addIncomingNeighbor(Graph<RoadNode, RoadEdge> g, PriorityQueue<TempNode> nodeList, RoadNode n){
        Set<RoadEdge> edgeSet = g.incomingEdgesOf(n);
        Iterator<RoadEdge> it = edgeSet.iterator();
        while(it.hasNext()){
            RoadEdge edge = it.next();
            RoadNode node = g.getEdgeSource(edge);
            if(node.getBelongTo()==null){
                boolean addFlag = true;
                boolean changeLength = false;
                TempNode changeNode = null;
                for(TempNode tempNode:nodeList){
                    if(tempNode.getRoadNode() == node){
                        addFlag = false;
                        if(edge.getLength() < tempNode.getLength()){
                            changeLength = true;
                            changeNode = tempNode;
                        }
                        break;
                    }
                }
                //add to priorityQueue
                if(addFlag == true){
                    TempNode tempNode = new TempNode(node,edge.getLength());
                    nodeList.add(tempNode);
                }
                if(changeLength == true){
                    nodeList.remove(changeNode);
                    changeNode.setLength(edge.getLength());
                    nodeList.add(changeNode);
                }
            }
        }
        return nodeList;
    }

    public static boolean isFinish(Map<String, Boolean> finishFlag){
        for(Map.Entry<String, Boolean> entry:finishFlag.entrySet()){
            Boolean flag = entry.getValue();
            if(flag == false){
                return false;
            }
        }
        return true;
    }

    public static Set<String> readCoreInfo(String fileName){
        Set<String> coreSet = new HashSet<String>();
        try (FileReader reader = new FileReader(CORE_NODE_FILE); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String coreId = line;
                if(!coreSet.contains(coreId)){
                    coreSet.add(coreId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coreSet;
    }

    //加载core节点Id
    public static List<RoadNode> setCoreNodes(Graph<RoadNode, RoadEdge> g, Set<String> coreInfo) throws Exception{
            List<RoadNode> coreNodes=new ArrayList<RoadNode>();
            Iterator<String> it = coreInfo.iterator();
            while(it.hasNext()){
                String t_id = it.next();
                RoadNode roadNode=g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(t_id)).findAny().get();
                roadNode.setCore();
                coreNodes.add(roadNode);
                Set<CoreEdge> edgeSet=new HashSet<CoreEdge>();//存哈希SET？
                CoreNode coreNode=new CoreNode(roadNode,edgeSet);
                roadNode.setCoreNode(coreNode);
            }
            return coreNodes;
    }

    public static Graph<RoadNode, RoadEdge> initRoadNetwork() throws Exception {
        //如果之前存了信息，直接读
        File file=new File(GRAPH_INFORMATION);
        if(file.exists()){
            System.out.println("Graph loading...");
            FileInputStream fileIn = new FileInputStream(GRAPH_INFORMATION);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Graph<RoadNode, RoadEdge> g = (Graph<RoadNode, RoadEdge>) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Graph done.");
            return g;
        }
        else{
            file.createNewFile();
        }

        Graph<RoadNode, RoadEdge> g = new DefaultDirectedGraph<RoadNode, RoadEdge>(RoadEdge.class);

        //List<RoadNode> roadNodes=new ArrayList();

        //将原始图所有的点插入
        System.out.println("graph start.");
        System.out.println("adding nodes...");
        Connection connection= DriverManager.getConnection(URL,USER,PASSWORD);
        String sql="select * from node order by id";
        Statement statement=connection.createStatement();
        ResultSet resultSet=statement.executeQuery(sql);
        while(resultSet.next())
        {
            RoadNode roadNode=new RoadNode();
            long id=resultSet.getLong("id");
            double lon=resultSet.getDouble("lon");
            double lat=resultSet.getDouble("lat");
            roadNode.setOsmId(String.valueOf(id));
            roadNode.setLon(lon);
            roadNode.setLat(lat);

            //roadNodes.add(roadNode);
            g.addVertex(roadNode);
        }
        statement.close();
        System.out.println("add nodes success.");


        System.out.println("adding edges...");
        //将原始图所有的边插入
        sql="select seg_node.seg_id, seg_node.start_id, seg_node.end_id, seg_length.length from seg_node, seg_length where seg_node.seg_id = seg_length.seg_id";
        statement=connection.createStatement();
        resultSet=statement.executeQuery(sql);
        while(resultSet.next())
        {
            long id=resultSet.getLong("seg_id");
            long start=resultSet.getLong("start_id");
            long end=resultSet.getLong("end_id");
            long length = resultSet.getLong("length");
            String idT=String.valueOf(id);
            String startId=String.valueOf(start);
            String endId=String.valueOf(end);
            RoadNode startNode = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(startId)).findAny().get();
            RoadNode endNode = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(endId)).findAny().get();
            if(startNode == null){
                System.out.println("start node null");
            }
            if(endNode == null){
                System.out.println("end node null");
            }
            RoadEdge roadEdge=g.addEdge(startNode, endNode);
            roadEdge.setOsm_id(idT);
            roadEdge.setLength(length);
            //roadEdge.setWeightList(getTdCostByRelationship(roadEdge.getOsm_id(), connection));
            //List<Long> weightList = roadEdge.getWeightList();
            //long minWeight = weightList.get(0);
            //for(int i=0;i<weightList.size();i++){
            //    if(weightList.get(i)<minWeight){
            //        minWeight = weightList.get(i);
            //    }
           // }
            //roadEdge.setMinWeight(minWeight);
        }
        statement.close();

        System.out.println("add edges success.");

        System.out.println("adding supplementary roadEdges...");

        //将补充的边插入，以满足图的全联通性
        addSupplementaryRoadSegmentEdges(g, connection);

        System.out.println("add supplementary roadEdges success.");

        connection.close();

        //System.out.println("adding core information...");
        //将coreNode和coreEdge的信息导入
        //addCoreInformation(g);

        //System.out.println("add core information success.");

        //System.out.println("partitioning graph...");
        //loadBelongFile(g);
        //System.out.println("partition graph success.");

        System.out.println("writing object file...");
        //将地图信息序列化存入文件中
        FileOutputStream fileOut =
                new FileOutputStream(GRAPH_INFORMATION);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(g);
        out.close();
        fileOut.close();
        System.out.println("done.");
        return g;
    }

    //加上补充的边，使图单向全联通
    public static void addSupplementaryRoadSegmentEdges(Graph<RoadNode, RoadEdge> g, Connection conn) throws SQLException
    {
        String sql="select * from add_edge";
        Statement statement=conn.createStatement();
        ResultSet resultSet=statement.executeQuery(sql);
        int start_id=24000;
        while(resultSet.next())
        {
            long start=resultSet.getLong("start");
            long end=resultSet.getLong("end");
            String startId=String.valueOf(start);
            String endId=String.valueOf(end);
            RoadNode sRoadNode=g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(startId)).findAny().get();
            RoadNode eRoadNode=g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(endId)).findAny().get();

            if(sRoadNode==null){
                System.out.println("sRoadNode null");
            }

            if(eRoadNode==null){
                System.out.println("eRoadNode null");
            }

            RoadEdge roadEdge = g.getEdge(sRoadNode, eRoadNode);

            if(roadEdge==null){
                long length = (long) MillerCoordinate.distance(sRoadNode.getLat(),sRoadNode.getLon(),eRoadNode.getLat(),eRoadNode.getLon());
                long weight = (long)(length/ AStar.estimatedSpeed);
                roadEdge=g.addEdge(sRoadNode, eRoadNode);
                roadEdge.setOsm_id(String.valueOf(start_id));
                roadEdge.setLength(length);
                roadEdge.setMinWeight(weight);
                //List<Long> distances=new ArrayList<>();
                //for(int i=0;i<breakPoint;i++){
                //    distances.add(weight);
                //}
                //roadEdge.setWeightList(distances);
                start_id++;
            }
            if(roadEdge==null){
                System.out.println("roadEdge null");
            }

            //双向连接
            RoadEdge reverseEdge=g.getEdge(eRoadNode, sRoadNode);
            if(reverseEdge == null) {
                long length = (long)MillerCoordinate.distance(sRoadNode.getLat(),sRoadNode.getLon(),eRoadNode.getLat(),eRoadNode.getLon());
                long weight = (long)(length/AStar.estimatedSpeed);
                reverseEdge = g.addEdge(eRoadNode, sRoadNode);
                reverseEdge.setOsm_id(String.valueOf(start_id));
                reverseEdge.setLength(length);
                reverseEdge.setMinWeight(weight);
                //List<Long> reverseDistances = new ArrayList<>();
                //for (int i = 0; i < breakPoint; i++) {
                //    reverseDistances.add(weight);
                //}
                //reverseEdge.setWeightList(reverseDistances);
                start_id++;
            }
        }
        statement.close();
    }

}

class TempNode {
    RoadNode roadNode;
    long length;

    public TempNode(RoadNode roadNode, long length){
        this.roadNode = roadNode;
        this.length = length;
    }

    public RoadNode getRoadNode() {
        return roadNode;
    }

    public void setRoadNode(RoadNode roadNode) {
        this.roadNode = roadNode;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
