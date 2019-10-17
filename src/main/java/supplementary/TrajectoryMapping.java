package supplementary;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jgrapht.Graph;
import roadNetwork.*;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TrajectoryMapping {

    public static TrajectoryMappingResult mapping(File inputFile, Graph<RoadNode, RoadEdge> g) throws java.io.IOException, java.lang.Exception{
        LinkedList<LinkedList<RoadNode>> candidateList = new LinkedList<LinkedList<RoadNode>>();
        long startTime = 0;
        long endTime = 0;
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputFile);
        //get the root element of the xml file
        Element root = document.getRootElement();
        int start_flag = 0;
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
                                    startTime = 0;
                                    Iterator<Element> trkpt_it = e_trkpt.elementIterator();
                                    while(trkpt_it.hasNext()){
                                        Element e_time = trkpt_it.next();
                                        if(e_time.getName().equals("time")){
                                            startTime = Gpx2Request.transTime((String)e_time.getData());
                                        }
                                    }
                                    LinkedList<RoadNode> roadNodeList= findNearestNodes(g, lat, lon);
                                    candidateList.add(roadNodeList);
                                    start_flag = 1;
                                }else{
                                    endTime = 0;
                                    Iterator<Element> trkpt_it = e_trkpt.elementIterator();
                                    while(trkpt_it.hasNext()){
                                        Element e_time = trkpt_it.next();
                                        if(e_time.getName().equals("time")){
                                            endTime = Gpx2Request.transTime((String)e_time.getData());
                                        }
                                    }
                                    LinkedList<RoadNode> roadNodeList= findNearestNodes(g, lat, lon);
                                    candidateList.add(roadNodeList);
                                }
                            }

                        }
                    }
                }
            }
        }
        //confirm time
        if(startTime == 0 || endTime == 0){
            return null;
        }
        //determine the path
        Path path = findConnectedPath(g, null, candidateList);
        if(path == null){
            return null;
        }
        TrajectoryMappingResult result = new TrajectoryMappingResult(path, startTime, endTime);
        return result;
    }

    public static Path findConnectedPath(Graph<RoadNode, RoadEdge> g, RoadNode candidateNode, LinkedList<LinkedList<RoadNode>> candidateList){
        LinkedList<RoadNode> roadNodeList = candidateList.poll();
        if(candidateNode == null){
            int roadNodeListSize = roadNodeList.size();
            for(int i=0; i<roadNodeListSize; i++){
                candidateNode = roadNodeList.get(i);
                if(!candidateList.isEmpty()) {
                    Path path = findConnectedPath(g, candidateNode, candidateList);
                    if (path != null) {
                        return path;
                    }
                }else{
                    return null;
                }
            }
        }else{
            int roadNodeListSize = roadNodeList.size();
            Set<RoadEdge> edgeSet = g.outgoingEdgesOf(candidateNode);
            Iterator<RoadEdge> it = edgeSet.iterator();
            while(it.hasNext()){
                RoadEdge edge = it.next();
                for(int i=0;i<roadNodeListSize;i++){
                    if(g.getEdgeTarget(edge) == roadNodeList.get(i)){
                        if(!candidateList.isEmpty()){
                            Path path = findConnectedPath(g, roadNodeList.get(i), candidateList);
                            if(path != null){
                                PathSegment pathSegment = new PathSegment(candidateNode,roadNodeList.get(i),edge.getLength());
                                path.addPathSegmentFirst(pathSegment);
                                return path;
                            }
                        }else{
                            Path path = new Path();
                            PathSegment pathSegment = new PathSegment(candidateNode,roadNodeList.get(i),edge.getLength());
                            path.addPathSegmentFirst(pathSegment);
                            return path;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static LinkedList<RoadNode> findNearestNodes(Graph<RoadNode, RoadEdge> g, double lon, double lat){
        LinkedList<RoadNode> roadNodeList = new LinkedList<RoadNode>();
        double limitDistance = 100;
        Set<RoadNode> nodeSet = g.vertexSet();
        Iterator<RoadNode> it = nodeSet.iterator();
        double minDistance = 99999999;
        RoadNode minNode = null;
        while(it.hasNext()){
            RoadNode roadNode = it.next();
            double tempDistance = MillerCoordinate.distance(lat,lon,roadNode.getLat(),roadNode.getLon());
            if(tempDistance <= limitDistance){
                roadNodeList.add(roadNode);
            }
            if(tempDistance < minDistance){
                minDistance = tempDistance;
                minNode = roadNode;
            }
        }
        if(roadNodeList.isEmpty()){
            roadNodeList.add(minNode);
        }
        return roadNodeList;
    }

}

    class TrajectoryMappingResult{
        Path path;
        long startTime;
        long endTime;

        public TrajectoryMappingResult(Path path, long startTime, long endTime){
            this.path = path;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }
