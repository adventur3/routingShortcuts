package supplementary;

import org.jgrapht.Graph;
import roadNetwork.LoadMap2;
import roadNetwork.RoadEdge;
import roadNetwork.RoadNode;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Trajectory2Request {
    private static String TRAJECTORY_DIR = "experimentData/test";
    private static String OUT_PATH = "experimentData/testOut/a.txt";
    private static String GRAPH_FILE = "experimentData/Maps/doublePartition_graph_k=10.ser";

    public static void main(String[] args) throws Exception{
        File file = new File(TRAJECTORY_DIR);
        if(!file.isDirectory()){
            System.out.println("A directory is required!");
        }else{
            Graph<RoadNode, RoadEdge> g = LoadMap2.getMap(GRAPH_FILE);
            File outFile = new File(OUT_PATH);
            if(!outFile.exists()) {
                outFile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(outFile);
            String outStr = "";
            String[] fileList = file.list();
            for(int i=0; i < fileList.length; i++){
                File readFile = new File(TRAJECTORY_DIR+File.separator+fileList[i]);
                if(!readFile.isDirectory()){
                    outStr = oneTrajectory2OneRequest(readFile, g);
                    out.write(outStr.getBytes());
                }
            }
        }
    }

    public static String oneTrajectory2OneRequest(File readFile, Graph<RoadNode, RoadEdge> g) throws Exception{
        InputStreamReader read = new InputStreamReader(new FileInputStream(readFile));
        BufferedReader bufferedReader = new BufferedReader(read);
        String firstLine = bufferedReader.readLine();
        String currentLine = firstLine;
        String lastLine = firstLine;
        while((currentLine = bufferedReader.readLine())!=null){
            lastLine = currentLine;
        }
        String[] startInfo = firstLine.split(",");
        String[] endInfo = lastLine.split(",");
        //经度
        String startLon = startInfo[0];
        String startLat = startInfo[1];
        String startTime = startInfo[2];
        String endLon = endInfo[0];
        String endLat = endInfo[1];
        RoadNode startNode = RequestFileTrans.findNodeByCoord(g, Double.valueOf(startLon), Double.valueOf(startLat));
        RoadNode endNode = RequestFileTrans.findNodeByCoord(g, Double.valueOf(endLon), Double.valueOf(endLat));
        long ts = transTime(startTime);
        return startNode.getOsmId()+"#"+endNode.getOsmId()+"#"+String.valueOf(ts)+"\n";
    }

    public static long transTime(String time_str) throws ParseException {
        String[] time_arr = time_str.split("T");
        String t = time_arr[0] +" " + time_arr[1].split("Z")[0];
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(t);
        long ts = date.getTime();
        return ts/1000;
        //return Long.valueOf(ts);
    }
}
