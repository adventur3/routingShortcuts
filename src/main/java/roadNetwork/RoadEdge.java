package roadNetwork;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.Serializable;
import java.util.List;

public class RoadEdge extends DefaultWeightedEdge implements Serializable {

    private static final long serialVersionUID = 5883728839235218851L;
    private List<Long> weightList;
    private long minWeight;
    private String osm_id;


    public RoadEdge(){

    }

    public RoadEdge(List<Long> list, long minWeight){
        this.weightList = list;
        this.minWeight = minWeight;
    }

    public List<Long> getWeightList() {
        return this.weightList;
    }

    public void setWeightList(List<Long> list) {
        this.weightList = list;
    }

    public long getMinWeight() {
        return this.minWeight;
    }

    public void setMinWeight(long weight) {
        this.minWeight = weight;
    }

    public String getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(String osm_id) {
        this.osm_id = osm_id;
    }


}