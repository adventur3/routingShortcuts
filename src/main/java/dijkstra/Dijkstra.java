package dijkstra;

import java.io.Serializable;
import java.util.*;

import org.jgrapht.Graph;
import roadNetwork.Path;
import roadNetwork.PathSegment;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;
import simulator.SimClock;

/*
 * Dijkstra Algorithm
 */
public class Dijkstra{

    /*
     *put the node that adjacents to roadNode into forward priority queue.
     *
     * @param g
     * @param roadNode
     * @param clock
     */
    public static void timeDependentUpdateForwardPriorityQueue(Graph<RoadNode, RoadEdge> g, Map<String,InfoNode> infoNodes, LinkedList<InfoNode> priorityQueue, InfoNode inode) {
        //get the all connected edge of roadNode
        Set<RoadEdge> edgeSet = g.outgoingEdgesOf(inode.getRoadNode());
        Iterator<RoadEdge> it = edgeSet.iterator();
        while(it.hasNext()) {
            RoadEdge nextEdge = it.next();
            long weightFromParent = 0;
            RoadNode nextNode = g.getEdgeTarget(nextEdge);
            if(infoNodes.containsKey(nextNode.getOsmId())){
                InfoNode nextInfoNode = infoNodes.get(nextNode.getOsmId());
                if(!nextInfoNode.isSetted()) {
                    weightFromParent = nextEdge.getWeightList().get(SimClock.getMinuteId(inode.getArrivalTime()));
                    long temp_weight = inode.getWeight() + weightFromParent;
                    if (temp_weight < nextInfoNode.getWeight()) {
                        nextInfoNode.setParent(inode);
                        nextInfoNode.setWeightFromParent(weightFromParent);
                        nextInfoNode.setWeight(temp_weight);
                        nextInfoNode.setArrivalTime(inode.getArrivalTime() + weightFromParent);
                    }
                }
            }else{
                weightFromParent = nextEdge.getWeightList().get(SimClock.getMinuteId(inode.getArrivalTime()));
                InfoNode nextInfoNode = new InfoNode(nextNode, inode, weightFromParent);
                infoNodes.put(nextNode.getOsmId(),nextInfoNode);
                nextInfoNode.setExplored();
                priorityQueue.add(nextInfoNode);
            }
        }//while(it.hasNext())
        sortList(priorityQueue);
    }

    public static void updateForwardPriorityQueue(Graph<RoadNode, RoadEdge> g, Map<String,InfoNode> infoNodes, LinkedList<InfoNode> priorityQueue, InfoNode inode) {
        //get the all connected edge of roadNode
        Set<RoadEdge> edgeSet = g.outgoingEdgesOf(inode.getRoadNode());
        Iterator<RoadEdge> it = edgeSet.iterator();
        while(it.hasNext()) {
            RoadEdge nextEdge = it.next();
            long weightFromParent = 0;
            RoadNode nextNode = g.getEdgeTarget(nextEdge);
            if(infoNodes.containsKey(nextNode.getOsmId())){
                InfoNode nextInfoNode = infoNodes.get(nextNode.getOsmId());
                if(!nextInfoNode.isSetted()) {
                    weightFromParent = nextEdge.getLength();
                    long temp_weight = inode.getWeight() + weightFromParent;
                    if (temp_weight < nextInfoNode.getWeight()) {
                        nextInfoNode.setParent(inode);
                        nextInfoNode.setWeightFromParent(weightFromParent);
                        nextInfoNode.setWeight(temp_weight);
                        nextInfoNode.setArrivalTime(0);
                    }
                }
            }else{
                weightFromParent = nextEdge.getLength();
                InfoNode nextInfoNode = new InfoNode(nextNode, inode, weightFromParent);
                infoNodes.put(nextNode.getOsmId(),nextInfoNode);
                nextInfoNode.setExplored();
                priorityQueue.add(nextInfoNode);
            }
        }//while(it.hasNext())
        sortList(priorityQueue);
    }

    /*
     * get the dijkstra shortest path from start to target
     */
    public static Path timeDependentSinglePath(Graph<RoadNode, RoadEdge> g, SimClock simClock, RoadNode start, RoadNode target) {
        long startTime = simClock.getNow();
        Map<String,InfoNode> infoNodes=new HashMap<String,InfoNode>(); //为了根据roadNode找到infoNode
        LinkedList<InfoNode> priorityQueue = new LinkedList<InfoNode>();
        InfoNode startInfo = new InfoNode(startTime, start);
        infoNodes.put(start.getOsmId(),startInfo);
        priorityQueue.add(startInfo);
        startInfo.setExplored();

        Path path =null;
        while(!priorityQueue.isEmpty()) {
            InfoNode inode = priorityQueue.pollFirst();
            inode.setSetted();
            if (inode.getRoadNode() == target) {
                path = outShortestPath(infoNodes, start, target);
                return path;
            }
            timeDependentUpdateForwardPriorityQueue(g, infoNodes, priorityQueue, inode);
        }
        return path;
    }

    /*
     * get the dijkstra shortest path from start to target
     */
    public static Path timeDependentSinglePath(Graph<RoadNode, RoadEdge> g, long start_time, RoadNode start, RoadNode target) {
        long startTime = start_time;
        Map<String,InfoNode> infoNodes=new HashMap<String,InfoNode>(); //为了根据roadNode找到infoNode
        LinkedList<InfoNode> priorityQueue = new LinkedList<InfoNode>();
        InfoNode startInfo = new InfoNode(startTime, start);
        infoNodes.put(start.getOsmId(),startInfo);
        priorityQueue.add(startInfo);
        startInfo.setExplored();

        Path path =null;
        while(!priorityQueue.isEmpty()) {
            InfoNode inode = priorityQueue.pollFirst();
            inode.setSetted();
            if (inode.getRoadNode() == target) {
                path = outShortestPath(infoNodes, start, target);
                return path;
            }
            timeDependentUpdateForwardPriorityQueue(g, infoNodes, priorityQueue, inode);
        }
        return path;
    }

    /*
     * get the dijkstra shortest path from start to target
     */
    public static Path singlePath(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target) {
        Map<String,InfoNode> infoNodes=new HashMap<String,InfoNode>(); //为了根据roadNode找到infoNode
        LinkedList<InfoNode> priorityQueue = new LinkedList<InfoNode>();
        InfoNode startInfo = new InfoNode(0, start);
        infoNodes.put(start.getOsmId(),startInfo);
        priorityQueue.add(startInfo);
        startInfo.setExplored();

        Path path =null;
        while(!priorityQueue.isEmpty()) {
            InfoNode inode = priorityQueue.pollFirst();
            inode.setSetted();
            if (inode.getRoadNode() == target) {
                path = outShortestPath(infoNodes, start, target);
                return path;
            }
            updateForwardPriorityQueue(g, infoNodes, priorityQueue, inode);
        }
        return path;
    }


    public static Path outShortestPath(Map<String,InfoNode> infoNodes, RoadNode start, RoadNode target) {
        InfoNode inode = infoNodes.get(target.getOsmId());
        InfoNode startInfoNode = infoNodes.get(start.getOsmId());
        long count = 0;
        Path path = new Path();
        while (inode != startInfoNode) {
            InfoNode parentInfoNode = inode.getParent();
            PathSegment pathSegment = new PathSegment(parentInfoNode.getRoadNode(), inode.getRoadNode(), inode.getWeightFromParent());
            path.addPathSegmentFirst(pathSegment);
            count ++;
            if (count > 9999999) {
                path = null;
                return path;
            }
            inode = parentInfoNode;
        }
        return path;
    }

    public static void sortList(List<InfoNode> list){
        Collections.sort(list, new Comparator<InfoNode>() {
            @Override
            public int compare(InfoNode n1, InfoNode n2) {
                if(n1.getWeight()<n2.getWeight()){
                    return -1;
                }
                else if(n1.getWeight()==n2.getWeight()){
                    return 0;
                }
                return 1;
            }
        });
    }

}//class


