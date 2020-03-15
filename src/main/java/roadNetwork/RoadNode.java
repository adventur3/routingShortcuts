package roadNetwork;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.jgrapht.Graph;

public class RoadNode implements Serializable {

    private static final long serialVersionUID = 4831863572747170883L;
    private String osm_id;    //osm id
    private double lon;        //经度
    private double lat;        //纬度
    private boolean coreFlag;
    private CoreNode coreNode;
    private RoadNode belongTo;//从属于哪个core节点

    public RoadNode() {
        coreFlag = false;
        coreNode = null;
    }

    public RoadNode(Element e) {
        List<Attribute> list = e.attributes();
        for (Attribute attribute : list) {
            if (attribute.getName().equals("id"))
                this.osm_id = attribute.getValue();
            if (attribute.getName().equals("lon"))
                this.lon = Double.parseDouble(attribute.getValue());
            if (attribute.getName().equals("lat"))
                this.lat = Double.parseDouble(attribute.getValue());
        }
        coreFlag = false;
        coreNode = null;
    }

    public String getOsmId() {
        return osm_id;
    }

    public void setOsmId(String osm_id) {
        this.osm_id = osm_id;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLon() {
        return this.lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return this.lat;
    }

    public void setCore() {
        this.coreFlag = true;
    }

    public boolean isCore() {
        return this.coreFlag;
    }

    public Set<RoadEdge> getAllNextEdge(Graph<RoadNode, RoadEdge> g) {
        Set<RoadEdge> edgeSet = g.edgesOf(this);
        Iterator it = edgeSet.iterator();
        Set<RoadEdge> nextEdgeSet = new HashSet<RoadEdge>();
        while (it.hasNext()) {
            RoadEdge nextEdge = (RoadEdge) it.next();
            if (g.getEdgeSource(nextEdge) == this) {
                nextEdgeSet.add(nextEdge);
            }
        }
        return nextEdgeSet;
    }

    public boolean setCoreNode(CoreNode coreNode) {
        if (this.coreFlag) {
            this.coreNode = coreNode;
            this.belongTo = this;
            return true;
        } else {
            return false;
        }
    }

    public CoreNode getCoreNode() {
        return this.coreNode;
    }

    public void setBelongTo(Graph<RoadNode, RoadEdge> g,String coreId){
        //RoadNode core= GraphUtil.findRoadNodeById(g,coreId);
        RoadNode core = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(coreId)).findAny().get();
        this.belongTo=core;
    }

    public void setBelongTo(RoadNode coreNode){
        this.belongTo=coreNode;
    }

    public RoadNode getBelongTo(){
        return belongTo;
    }

}

