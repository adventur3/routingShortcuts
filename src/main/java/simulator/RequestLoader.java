package simulator;

import org.jgrapht.Graph;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;

import java.io.*;
import java.text.ParseException;
import java.util.LinkedList;

public class RequestLoader {

    private LinkedList<Request> requestList = new LinkedList<Request>();

    //load request file and create RequestCreateEvent
    public void loadRequest(String fileName, Graph<RoadNode, RoadEdge> g) throws NumberFormatException, IOException, ParseException {
        File requestFile=new File(fileName);
        InputStreamReader read = new InputStreamReader(new FileInputStream(requestFile));
        BufferedReader bufferedReader = new BufferedReader(read);

        String lineTxt = "";
        while((lineTxt = bufferedReader.readLine()) != null){
            String s[]=lineTxt.split("#");
            String startId = s[0];
            String targetId = s[1];
            RoadNode startNode = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(startId)).findAny().get();
            RoadNode targetNode = g.vertexSet().stream().filter(elemen -> elemen.getOsmId().equals(targetId)).findAny().get();
            Request request = new Request(startNode, targetNode);
            requestList.add(request);
        }
    }

    public LinkedList<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(LinkedList<Request> requestList) {
        this.requestList = requestList;
    }
}
