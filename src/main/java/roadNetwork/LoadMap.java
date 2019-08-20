package roadNetwork;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


import astar.AStar;
import dijkstra.Dijkstra;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import simulator.SimClock;


public class LoadMap {

    private static String URL="jdbc:mysql://localhost:3306/road_network?useUnicode=true&characterEncoding=UTF8&useSSL=false";
    private static String USER="root";
    private static String PASSWORD="mocom123";

    private static int breakPoint=24;
    private static int ut=1;
    private static int core_choose_nums=4000;
    private static int core_nums=50;

    public static String graphInformation="experimentData/core_choose_nums="+core_choose_nums+"_core_nums="+core_nums+"_graph.ser";
    public static String coreNodeFile="experimentData/core_choose_nums="+core_choose_nums+"_core_nums="+core_nums+".txt";


    //根据osmId找到对应的RoadNode
//    public static RoadNode getRoadNodeById(List<RoadNode> RoadNodes,String id)
//    {
//        int length=RoadNodes.size();
//        return findRoadNode(0, length-1, RoadNodes, id);
//    }

//    public static RoadNode findRoadNode(int left,int right,List<RoadNode> RoadNodes,String id)
//    {
//        if(left<=right)
//        {
//            int index=(left+right)/2;
//            String nowId=RoadNodes.get(index).getOsmId();
//            if(nowId.equals(id))return RoadNodes.get(index);
//            else if(Long.parseLong(nowId)>Long.parseLong(id)) return findRoadNode(left, index-1, RoadNodes, id);
//            else return findRoadNode(index+1, right, RoadNodes, id);
//        }
//        else return null;
//    }

    //根据边的Id查找对应的TD-cost
    public static List<Long> getTdCostByRelationship(String id,Connection connection) throws SQLException
    {
        List<Long> timeList=new ArrayList<Long>();
        Long sumCost=0L;

        String sql="select * from seg_time_average_p where seg_id="+id+" order by minute_id";
        Statement statement=connection.createStatement();
        ResultSet resultSet=statement.executeQuery(sql);
        for(int i=0;i<breakPoint;i++)
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
                long weight = (long)(MillerCoordinate.distance(sRoadNode.getLat(),sRoadNode.getLon(),eRoadNode.getLat(),eRoadNode.getLon())/AStar.estimatedSpeed);
                roadEdge=g.addEdge(sRoadNode, eRoadNode);
                roadEdge.setOsm_id(String.valueOf(start_id));
                roadEdge.setMinWeight(weight);
                List<Long> distances=new ArrayList<>();
                for(int i=0;i<breakPoint;i++){
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
                long weight = (long)(MillerCoordinate.distance(sRoadNode.getLat(),sRoadNode.getLon(),eRoadNode.getLat(),eRoadNode.getLon())/AStar.estimatedSpeed);
                reverseEdge = g.addEdge(eRoadNode, sRoadNode);
                reverseEdge.setOsm_id(String.valueOf(start_id));
                reverseEdge.setMinWeight(weight);
                List<Long> reverseDistances = new ArrayList<>();
                for (int i = 0; i < breakPoint; i++) {
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
        BufferedReader bufferedReader=new BufferedReader(new FileReader(coreNodeFile));
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
                    for(int minute_id=0;minute_id<breakPoint;minute_id++){
                        start_time += minute_id * 3600000;
                        Path path= Dijkstra.singlePath(g, start_time, m, n);
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
                    m.getCoreNode().addEdge(coreEdge);
                }
            }
        }
    }

    //构建地图
    public static Graph<RoadNode, RoadEdge> initMap() throws Exception
    {
        //如果之前存了信息，直接读
        File file=new File(graphInformation);
        if(file.exists()){
            System.out.println("Graph loading...");
            FileInputStream fileIn = new FileInputStream(graphInformation);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Graph<RoadNode, RoadEdge> g = (Graph<RoadNode, RoadEdge>) in.readObject();
            in.close();
            fileIn.close();

            //System.out.println("文件读取结束，开始修改内存");
            //loadBelongFile(g);
            //System.out.println("内存修改完毕，重写文件");
            //将修改后的graph重新存入
            //FileOutputStream fileOut =
            //        new FileOutputStream(graphInformation);
            //ObjectOutputStream out = new ObjectOutputStream(fileOut);
            //out.writeObject(g);
            //out.close();
            //fileOut.close();

            //System.out.println("文件重写完毕");


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
        Connection connection=DriverManager.getConnection(URL,USER,PASSWORD);
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
        sql="select * from seg_node";
        statement=connection.createStatement();
        resultSet=statement.executeQuery(sql);
        while(resultSet.next())
        {
            long id=resultSet.getLong("seg_id");
            long start=resultSet.getLong("start_id");
            long end=resultSet.getLong("end_id");
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
                new FileOutputStream(graphInformation);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(g);
        out.close();
        fileOut.close();
        System.out.println("done.");
        return g;
    }

    /*
     * 将所属core信息加载进入节点中，没有的则选取最近core冒充
     * @param g
     * @return void
     */
    public static void loadBelongFile(Graph<RoadNode, RoadEdge> g)throws Exception{
        String line="";
        PartitionOper partitionOper = new PartitionOper();

        for(RoadNode roadNode:g.vertexSet()){
            RoadNode core=partitionOper.findNearCore(roadNode,g);
            roadNode.setBelongTo(core);
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
        LoadMap.initMap();
    }

}
