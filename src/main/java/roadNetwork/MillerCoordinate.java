package roadNetwork;

import org.jgrapht.Graph;


/**
 * @Author: Chengyu Sun
 * @Description:
 * @Date: Created in 2019/6/27 15:24
 */
public class MillerCoordinate {

    public static double distance(String id1,String id2,Graph<RoadNode, RoadEdge> graph){

        RoadNode n1 = graph.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(id1)).findAny().get();
        RoadNode n2 = graph.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(id2)).findAny().get();
        return distance(n1,n2);
    }

    /*
        return measurement: m
     */
    public static double distance(RoadNode n1,RoadNode n2){
        return distance(n1.getLat(),n1.getLon(),n2.getLat(),n2.getLon());
    }

    /*
        return measurement: m
     */
    public static double distance(double lat1,double lon1,double lat2,double lon2){
        double[] c1=MillierConvertion(lat1,lon1);
        double[] c2=MillierConvertion(lat2,lon2);
        return Math.sqrt(Math.pow(c1[0]-c2[0],2)+Math.pow(c1[1]-c2[1],2));
    }


    /*
        return measurement: m
     */
    public static double[] MillierConvertion(double lat, double lon){
        double L = 6381372 * Math.PI * 2;//地球周长
        double W=L;// 平面展开后，x轴等于周长
        double H=L/2;// y轴约等于周长一半
        double mill=2.3;// 米勒投影中的一个常数，范围大约在正负2.3之间
        double x = lon * Math.PI / 180;// 将经度从度数转换为弧度
        double y = lat * Math.PI / 180;// 将纬度从度数转换为弧度
        y=1.25 * Math.log( Math.tan( 0.25 * Math.PI + 0.4 * y ) );
        x = ( W / 2 ) + ( W / (2 * Math.PI) ) * x; y = ( H / 2 ) - ( H / ( 2 * mill ) ) * y;
        double[] result=new double[2];
        result[0]=x; result[1]=y;
        return result;
    }

    public static void main(String[] args){
        double distance = MillerCoordinate.distance(24.489882,118.096488,24.462836,118.075469);
        System.out.println(distance);
    }
}