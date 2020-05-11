package supplementary;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;

public class DeleteSameOD {
    private static String REQUEST_FILE = "experimentData/gpxTrajRequests.txt";
    private static String NEW_REQUEST_FILE = "experimentData/newGpxTrajRequests.txt";
    public static void main(String[] args) throws IOException {
        File requestFile=new File(REQUEST_FILE);
        InputStreamReader read = new InputStreamReader(new FileInputStream(requestFile));
        BufferedReader bufferedReader = new BufferedReader(read);

        FileOutputStream out = new FileOutputStream(NEW_REQUEST_FILE);
        String outStr = "";
        int count = 0;
        String lineTxt = "";
        while((lineTxt = bufferedReader.readLine()) != null) {
            String s[] = lineTxt.split("#");
            String origin = s[0];
            String destination = s[1];
            if(!origin.equals(destination)){
                outStr = origin+"#"+destination+"\r\n";
                out.write(outStr.getBytes());
            }
        }
        out.close();
    }
}
