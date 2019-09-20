package supplementary;

import dijkstra.Dijkstra;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jgrapht.Graph;
import roadNetwork.LoadMap;
import roadNetwork.MillerCoordinate;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Gpx2Request {

    private static String GPX_BASE_PATH = "experimentData/gpxData";
    private static String OUT_FILE = "experimentData/gpxTrajRequests.txt";
    private static String GRAPH_FILE = "experimentData/core_choose_nums=4000_core_nums=50_graph.ser";

    public static void main(String args[]) throws java.io.IOException, java.lang.Exception{
        Graph<RoadNode, RoadEdge> g = LoadMap.getMap(GRAPH_FILE);
        LinkedList<NodePairWithTime> nodePairs = new LinkedList<NodePairWithTime>();
        long count_distance = 0;
        long count_distance_temp = 0;
        long count_weight = 0;
        NodePairWithTime nodePair = null;
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
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(readfile);
                    //get the root element of the xml file
                    Element root = document.getRootElement();
                    int start_flag = 0;
                    RoadNode lastNode = null;
                    nodePair = new NodePairWithTime();
                    Iterator<Element> iterator = root.elementIterator();
                    while(iterator.hasNext()){
                        Element e = iterator.next();
                        if(e.getName().equals("trk")){
                            Iterator<Element> iterator_trk = e.elementIterator();
                            while(iterator_trk.hasNext()){
                                Element trkseg = iterator_trk.next();
                                if(trkseg.getName().equals("trkseg")){
                                    Iterator<Element> iterator_trkseg = trkseg.elementIterator();
                                    while(iterator_trkseg.hasNext()){
                                        double lat = 0;
                                        double lon = 0;
                                        Element e_trkpt = iterator_trkseg.next();
                                        if(e_trkpt.getName().equals("trkpt")){
                                            List<Attribute> attr_list = e_trkpt.attributes();
                                            Iterator<Attribute> attr_it = attr_list.iterator();
                                            while(attr_it.hasNext()){
                                                Attribute attr = attr_it.next();
                                                if(attr.getName().equals("lat")){
                                                    lat = Double.parseDouble(attr.getValue());
                                                }
                                                if(attr.getName().equals("lon")){
                                                    lon = Double.parseDouble(attr.getValue());
                                                }
                                            }
                                            if(start_flag == 0){
                                                long start_time = 0;
                                                Iterator<Element> trkpt_it = e_trkpt.elementIterator();
                                                while(trkpt_it.hasNext()){
                                                    Element e_time = trkpt_it.next();
                                                    if(e_time.getName().equals("time")){
                                                        start_time = transTime((String)e_time.getData());
                                                    }
                                                }
                                                RoadNode startNode = RequestFileTrans.findNodeByCoord(g, lon, lat);
                                                long distance_temp = 0;
                                                count_distance_temp = 0;
                                                nodePair.setStartId(startNode.getOsmId());
                                                if(start_time==0){
                                                    System.out.println("error:starttime 0");
                                                    return;
                                                }
                                                nodePair.setStartTime(start_time);
                                                lastNode = startNode;
                                                start_flag = 1;
                                            }else{
                                                RoadNode tempNode = RequestFileTrans.findNodeByCoord(g, lon, lat);
                                                long temp_distance = getDistance(g, lastNode,tempNode);
                                                count_distance_temp += temp_distance;
                                                nodePair.setTargetId(tempNode.getOsmId());
                                                long temp_time = 0;
                                                Iterator<Element> trkpt_it = e_trkpt.elementIterator();
                                                while(trkpt_it.hasNext()){
                                                    Element e_time = trkpt_it.next();
                                                    if(e_time.getName().equals("time")){
                                                        temp_time = transTime((String)e_time.getData());
                                                    }
                                                }
                                                nodePair.setTargetTime(temp_time);
                                                lastNode = tempNode;
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                    System.out.println(readfile.getName());
                    System.out.println("starttime=" + nodePair.getStartTime()+", targettime = " + nodePair.getTargetTime());
                    if(nodePair.getTargetTime()>=nodePair.getStartTime() && nodePair.getTargetTime()!=0 && nodePair.getStartTime() != 0 ){
                        count_weight = count_weight + (nodePair.getTargetTime() - nodePair.getStartTime());
                        count_distance += count_distance_temp;
                        nodePairs.add(nodePair);
                    }else{
                        System.out.println("error: start time or target time error");
                        return;
                    }

                }
            }
            System.out.println("count_weight= " + count_weight);
            System.out.println("count_distance = " + count_distance);
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
    public static long getDistance(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target){
        //return Dijkstra.singlePath(g, start, target).getWeight();
        return (long)MillerCoordinate.distance(start,target);
    }

    public static long transTime(String time_str) throws ParseException {
        String[] time_arr = time_str.split("T");
        String t = time_arr[0] +" " + time_arr[1].split("Z")[0];
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(t);
        long ts = date.getTime();
        return Long.valueOf(ts);
    }

}
