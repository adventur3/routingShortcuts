package roadNetwork;

import dijkstra.Dijkstra;
import org.dom4j.DocumentException;
import org.jgrapht.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Chengyu Sun
 * @Description:
 * @Date: Created in 2019/6/27 11:10
 */
public class PartitionOper {

    private  String belongFile="experimentData/belonging_choose_nums=4000_core_nums=50.txt";
    private  Map<String,String> belongingMap;
    private  Set<String> coreSet;

    public PartitionOper(){
        belongingMap=new HashMap<>();
        coreSet=new HashSet<>();
        try (FileReader reader = new FileReader(belongFile); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                belongingMap.put(line.split(":")[0],line.split(":")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileReader reader = new FileReader(LoadMap.coreNodeFile); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                coreSet.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    find the nearest core node by miller distance
     */
    public  RoadNode findNearCore(RoadNode roadNode, Graph<RoadNode, RoadEdge> g){
        if(roadNode.isCore()){
            return roadNode;
        }
        if(belongingMap.get(roadNode.getOsmId())!=null){
            return g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(belongingMap.get(roadNode.getOsmId()))).findAny().get();
            //return GraphUtil.findRoadNodeById(graph,belongingMap.get(id));
        }
        double shortestDis=99999999;
        String nearCore="";
        for(String coreId:coreSet){
            double temp_distance = MillerCoordinate.distance(roadNode.getOsmId(),coreId,g);
            if(temp_distance<shortestDis){
                nearCore=coreId;
                shortestDis=temp_distance;
            }
        }
        String theCoreId = nearCore;
        return g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(theCoreId)).findAny().get();
        //return GraphUtil.findRoadNodeById(graph,nearCore);
    }

    /*
    find the nearest core node by dijkstra distance
     */
    public  RoadNode findNearCore2(RoadNode roadNode, Graph<RoadNode, RoadEdge> g){
        if(roadNode.isCore()){
            return roadNode;
        }
        if(belongingMap.get(roadNode.getOsmId())!=null){
            return g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(belongingMap.get(roadNode.getOsmId()))).findAny().get();
            //return GraphUtil.findRoadNodeById(graph,belongingMap.get(id));
        }
        double shortestDis=999999999;
        String nearCore="";
        for(String coreId:coreSet){
            //double temp_distance = MillerCoordinate.distance(roadNode.getOsmId(),coreId,g);
            RoadNode coreNode = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(coreId)).findAny().get();
            Path thePath = Dijkstra.singlePath(g, roadNode, coreNode);
            if(thePath == null){
                continue;
            }
            double temp_distance = thePath.getWeight();
            if(temp_distance<shortestDis){
                nearCore=coreId;
                shortestDis=temp_distance;
            }
        }
        if(nearCore == ""){
            findNearCore(roadNode,g);
        }
        String theCoreId = nearCore;
        return g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(theCoreId)).findAny().get();
        //return GraphUtil.findRoadNodeById(graph,nearCore);
    }

    public Set<String> getCoreSet() {
        return coreSet;
    }

    public Map<String, String> getBelongingMap() {
        return belongingMap;
    }

    public static void main(String[] args)throws IOException, DocumentException, java.lang.Exception{
//        System.out.println("graphing...");
//        Graph<RoadNode, RoadEdge> graph = LoadMap.getMap(AStar.GRAPH_FILE);
//
//        for(String coreId:new Cluster().getBelongingMap().values()){
//            System.out.println(coreId+": "+GraphUtil.findRoadNodeById(graph,coreId).isCore());
//        }

    }
}

