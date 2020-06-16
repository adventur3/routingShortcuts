package roadNetwork;

import astar.AStar;
import dijkstra.Dijkstra;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.*;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/*
 * load map with incoming&outgoing partition
 */
public class LoadMap2 {

    private static String URL="jdbc:mysql://localhost:3306/road_network?useUnicode=true&characterEncoding=UTF8&useSSL=false";
    private static String USER="root";
    private static String PASSWORD="mocom123";

    private static int BREAK_POINT=24;
    private static int ut=1;
    //private static int CORE_CHOOSE_NUMS=4000;
    private static int CORE_NUM=90;

    public static String GRAPH_INFORMATION="experimentData/Maps/doublePartition_graph_k="+CORE_NUM+".ser";
    public static String CORENODE_FILE="experimentData/coreNodes/k="+CORE_NUM+".txt";
    public static String OUTGOING_FILE="experimentData/belongings/outgoingBelong_k="+CORE_NUM+".txt";
    public static String INCOMING_FILE="experimentData/belongings/incomingBelong_k="+CORE_NUM+".txt";

    //根据边的Id查找对应的TD-cost
    public static List<Long> getTdCostByRelationship(String id, Connection connection) throws SQLException
    {
        List<Long> timeList=new ArrayList<Long>();
        Long sumCost=0L;

        String sql="select * from seg_time_average_p where seg_id="+id+" order by minute_id";
        Statement statement=connection.createStatement();
        ResultSet resultSet=statement.executeQuery(sql);
        for(int i=0;i<BREAK_POINT;i++)
        {
            resultSet.next();
            sumCost = (long)resultSet.getInt("time");
            timeList.add(sumCost);
        }
        statement.close();
        return timeList;
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
                long length = (long)MillerCoordinate.distance(sRoadNode.getLat(),sRoadNode.getLon(),eRoadNode.getLat(),eRoadNode.getLon());
                long weight = (long)(length/ AStar.estimatedSpeed);
                roadEdge=g.addEdge(sRoadNode, eRoadNode);
                roadEdge.setOsm_id(String.valueOf(start_id));
                roadEdge.setLength(length);
                roadEdge.setMinWeight(weight);
                List<Long> distances=new ArrayList<>();
                for(int i=0;i<BREAK_POINT;i++){
                    distances.add(weight);
                }
                roadEdge.setWeightList(distances);
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
                List<Long> reverseDistances = new ArrayList<>();
                for (int i = 0; i < BREAK_POINT; i++) {
                    reverseDistances.add(weight);
                }
                reverseEdge.setWeightList(reverseDistances);
                start_id++;
            }
        }
        statement.close();
    }

    //加载core节点Id
    public static List<RoadNode> loadCoreId(Graph<RoadNode,RoadEdge> g) throws Exception{
        List<RoadNode> chooseNodes=new ArrayList<RoadNode>();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(CORENODE_FILE));
        String id="";
        while((id=bufferedReader.readLine())!=null){
            String t_id = id;
            RoadNode roadNode=g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(t_id)).findAny().get();
            roadNode.setCore();
            chooseNodes.add(roadNode);
            Set<CoreEdge> edgeSet=new HashSet<CoreEdge>();//存哈希SET？
            CoreNode coreNode=new CoreNode(roadNode,edgeSet);
            roadNode.setCoreNode(coreNode);
        }
        return chooseNodes;
    }

    //导入core节点的信息
    public static void addCoreInformation(Graph<RoadNode,RoadEdge> g) throws Exception{
        List<RoadNode> chooseNodes=loadCoreId(g);
        for(RoadNode m:chooseNodes){
            for(RoadNode n:chooseNodes){
                if(m!=n){
                    LinkedList<Path> pathLinkedList=new LinkedList<Path>();
                    long minDistance=0;
                    long start_time = 1546272000000L;
                    for(int minute_id=0;minute_id<BREAK_POINT;minute_id++){
                        start_time += minute_id * 3600000;
                        Path path= Dijkstra.timeDependentSinglePath(g, start_time, m, n);
                        pathLinkedList.add(path);
                        long distance=path.getWeight();
                        if(minDistance==0){
                            minDistance=distance;
                        }
                        if(distance<minDistance){
                            minDistance=distance;
                        }
                    }
                    CoreEdge coreEdge=new CoreEdge(m.getCoreNode(),n.getCoreNode(),pathLinkedList,minDistance);
                    coreEdge.setPath(Dijkstra.singlePath(g, m, n));
                    m.getCoreNode().addEdge(coreEdge);
                }
            }
        }
    }

    //构建地图
    public static Graph<RoadNode, RoadEdge> initMap() throws Exception
    {
        //如果之前存了信息，直接读
        File file=new File(GRAPH_INFORMATION);
        if(file.exists()){
            System.out.println("Loading graph from file...");
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
        System.out.println("loading graph start...");
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
            roadEdge.setWeightList(getTdCostByRelationship(roadEdge.getOsm_id(), connection));
            List<Long> weightList = roadEdge.getWeightList();
            long minWeight = weightList.get(0);
            for(int i=0;i<weightList.size();i++){
                if(weightList.get(i)<minWeight){
                    minWeight = weightList.get(i);
                }
            }
            roadEdge.setMinWeight(minWeight);
        }
        statement.close();
        System.out.println("add edges success.");
        System.out.println("adding supplementary roadEdges...");
        //将补充的边插入，以满足图的全联通性
        addSupplementaryRoadSegmentEdges(g, connection);
        System.out.println("add supplementary roadEdges success.");
        connection.close();
        System.out.println("adding core information...");
        //将coreNode和coreEdge的信息导入
        addCoreInformation(g);
        System.out.println("add core information success.");
        System.out.println("partitioning graph...");
        loadBelongFile(g);
        System.out.println("partition graph success.");

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


    /*
     * 将core节点的下属节点信息载入
     * @param g
     * @return void
     */
    public static void loadBelongFile(Graph<RoadNode, RoadEdge> g)throws Exception{
        Map<String, RoadNode> coreNodes = new HashMap<String, RoadNode>();
        try (FileReader reader = new FileReader(CORENODE_FILE); BufferedReader br = new BufferedReader(reader)) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String nodeId = line;
                RoadNode node = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(nodeId)).findAny().get();
                coreNodes.put(nodeId, node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileReader reader = new FileReader(OUTGOING_FILE); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String nodeId = line.split(":")[0];
                String coreNodeId = line.split(":")[1];
                RoadNode node = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(nodeId)).findAny().get();
                node.setBelongTo(coreNodes.get(coreNodeId));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileReader reader = new FileReader(INCOMING_FILE); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String nodeId = line.split(":")[0];
                String coreNodeId = line.split(":")[1];
                RoadNode node = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(nodeId)).findAny().get();
                node.setBelongTo_incoming(coreNodes.get(coreNodeId));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //直接根据序列化文件读取地图
    public static Graph<RoadNode, RoadEdge> getMap(String fileName) throws Exception{
        File file=new File(fileName);
        if(file.exists()){
            System.out.println("Graph loading...");
            Instant inst1 = Instant.now();
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Graph<RoadNode, RoadEdge> g = (Graph<RoadNode, RoadEdge>) in.readObject();
            in.close();
            fileIn.close();
            Instant inst2 = Instant.now();
            System.out.println("Graph done. Cost time = "+ Duration.between(inst1, inst2).toMillis());
            return g;
        }
        else{
            throw new Exception("can not find serializable file!");
        }
    }

    public static void main(String[] args) throws Exception{
//		long timeStart=System.currentTimeMillis();
//		Graph<RoadNode, RoadSegmentEdge> graph=initMap();
//		long timeEnd=System.currentTimeMillis();
//		long cost=timeEnd-timeStart;
//		System.out.println("init cost:"+cost+"ms");
        LoadMap2.initMap();
    }

}
