package astar;

import org.jgrapht.Graph;
import roadNetwork.*;
import simulator.SimClock;

import java.util.*;

public class AStar {

    public static double estimatedSpeed = 0.025;  // measurement:m/ms

    /*
     * get the astar shortest path from start to target
     */
    public static Path timeDependentSinglePath(Graph<RoadNode, RoadEdge> g, SimClock simClock, RoadNode start, RoadNode target) {
        long startTime = simClock.getNow();
        Map<String, AStarInfoNode> infoNodes=new HashMap<String,AStarInfoNode>();  //为了根据roadNode找到infoNode
        LinkedList<AStarInfoNode> priorityQueue = new LinkedList<AStarInfoNode>();
        long estimatedWeight = (long) (MillerCoordinate.distance(start, target)/AStar.estimatedSpeed);
        AStarInfoNode startInfo = new AStarInfoNode(startTime, start, target, estimatedWeight);
        infoNodes.put(start.getOsmId(),startInfo);
        priorityQueue.add(startInfo);
        startInfo.setExplored();

        Path path =null;
        while(!priorityQueue.isEmpty()) {
            AStarInfoNode inode = priorityQueue.pollFirst();
            inode.setSetted();
            if (inode.getRoadNode() == target) {
                path = outShortestPath(infoNodes, start, target);
                return path;
            }
            timeDependentUpdateForwardPriorityQueue(g, infoNodes, priorityQueue, inode, target);
        }
        return path;
    }

    /*
     * get the astar shortest path from start to target
     */
    public static Path singlePath(Graph<RoadNode, RoadEdge> g, RoadNode start, RoadNode target) {
        Map<String, AStarInfoNode> infoNodes=new HashMap<String,AStarInfoNode>();  //为了根据roadNode找到infoNode
        LinkedList<AStarInfoNode> priorityQueue = new LinkedList<AStarInfoNode>();
        long estimatedWeight = (long) (MillerCoordinate.distance(start, target) * 0.90);
        AStarInfoNode startInfo = new AStarInfoNode(0, start, target, estimatedWeight);
        infoNodes.put(start.getOsmId(),startInfo);
        priorityQueue.add(startInfo);
        startInfo.setExplored();

        Path path =null;
        while(!priorityQueue.isEmpty()) {
            AStarInfoNode inode = priorityQueue.pollFirst();
            inode.setSetted();
            if (inode.getRoadNode() == target) {
                path = outShortestPath(infoNodes, start, target);
                return path;
            }
            updateForwardPriorityQueue(g, infoNodes, priorityQueue, inode, target);
        }
        return path;
    }

    /*
     *put the node that adjacents to roadNode into forward priority queue.
     *
     * @param g
     * @param roadNode
     * @param clock
     */
    public static void timeDependentUpdateForwardPriorityQueue(Graph<RoadNode, RoadEdge> g, Map<String, AStarInfoNode> infoNodes, LinkedList<AStarInfoNode> priorityQueue, AStarInfoNode inode, RoadNode target) {
        //get the all connected edge of roadNode
        Set<RoadEdge> edgeSet = g.outgoingEdgesOf(inode.getRoadNode());
        Iterator<RoadEdge> it = edgeSet.iterator();
        while(it.hasNext()) {
            RoadEdge nextEdge = it.next();
            long weightFromParent = 0;
            RoadNode nextNode = g.getEdgeTarget(nextEdge);
            if(infoNodes.containsKey(nextNode.getOsmId())){
                AStarInfoNode nextInfoNode = infoNodes.get(nextNode.getOsmId());
                if(!nextInfoNode.isSetted()) {
                    weightFromParent = nextEdge.getWeightList().get(SimClock.getMinuteId(inode.getArrivalTime()));
                    long temp_weight = inode.getRealWeight() + weightFromParent;
                    if (temp_weight < nextInfoNode.getRealWeight()) {
                        nextInfoNode.setParent(inode);
                        nextInfoNode.setWeightFromParent(weightFromParent);
                        nextInfoNode.setRealWeight(temp_weight);
                        nextInfoNode.setWeight(nextInfoNode.getRealWeight()+nextInfoNode.getEstimatedWeight());
                        nextInfoNode.setArrivalTime(inode.getArrivalTime() + weightFromParent);
                    }
                }
            }else{
                weightFromParent = nextEdge.getWeightList().get(SimClock.getMinuteId(inode.getArrivalTime()));
                long estimatedWeight = (long) (MillerCoordinate.distance(nextNode, target)/AStar.estimatedSpeed);
                AStarInfoNode nextInfoNode = new AStarInfoNode(nextNode, inode, target, weightFromParent, estimatedWeight);
                infoNodes.put(nextNode.getOsmId(),nextInfoNode);
                nextInfoNode.setExplored();
                priorityQueue.add(nextInfoNode);
            }
        }//while(it.hasNext())
        sortList(priorityQueue);
    }

    public static void updateForwardPriorityQueue(Graph<RoadNode, RoadEdge> g, Map<String, AStarInfoNode> infoNodes, LinkedList<AStarInfoNode> priorityQueue, AStarInfoNode inode, RoadNode target) {
        //get the all connected edge of roadNode
        Set<RoadEdge> edgeSet = g.outgoingEdgesOf(inode.getRoadNode());
        Iterator<RoadEdge> it = edgeSet.iterator();
        while(it.hasNext()) {
            RoadEdge nextEdge = it.next();
            long weightFromParent = 0;
            RoadNode nextNode = g.getEdgeTarget(nextEdge);
            if(infoNodes.containsKey(nextNode.getOsmId())){
                AStarInfoNode nextInfoNode = infoNodes.get(nextNode.getOsmId());
                if(!nextInfoNode.isSetted()) {
                    weightFromParent = nextEdge.getLength();
                    long temp_weight = inode.getRealWeight() + weightFromParent;
                    if (temp_weight < nextInfoNode.getRealWeight()) {
                        nextInfoNode.setParent(inode);
                        nextInfoNode.setWeightFromParent(weightFromParent);
                        nextInfoNode.setRealWeight(temp_weight);
                        nextInfoNode.setWeight(nextInfoNode.getRealWeight()+nextInfoNode.getEstimatedWeight());
                        nextInfoNode.setArrivalTime(0);
                    }
                }
            }else{
                weightFromParent = nextEdge.getLength();
                long estimatedWeight = (long) (MillerCoordinate.distance(nextNode, target)*0.90);
                AStarInfoNode nextInfoNode = new AStarInfoNode(nextNode, inode, target, weightFromParent, estimatedWeight);
                infoNodes.put(nextNode.getOsmId(),nextInfoNode);
                nextInfoNode.setExplored();
                priorityQueue.add(nextInfoNode);
            }
        }//while(it.hasNext())
        sortList(priorityQueue);
    }


    public static Path outShortestPath(Map<String,AStarInfoNode> infoNodes, RoadNode start, RoadNode target) {
        AStarInfoNode inode = infoNodes.get(target.getOsmId());
        AStarInfoNode startInfoNode = infoNodes.get(start.getOsmId());
        long count = 0;
        Path path = new Path();
        while (inode != startInfoNode) {
            AStarInfoNode parentInfoNode = inode.getParent();
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


    public static void sortList(List<AStarInfoNode> list){
        Collections.sort(list, new Comparator<AStarInfoNode>() {
            @Override
            public int compare(AStarInfoNode n1, AStarInfoNode n2) {
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

}
