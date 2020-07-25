package supplementary;

import java.io.*;

public class DeleteSameOD2 {
    private static String REQUEST_FILE_DIR = "experimentData/request/";
    private static String REQUEST_FILE_NAME = "2014-07-01-16_request_DeleteDuplicateOD";
    private static String SUFFIX = ".txt";
    public static void main(String[] args) throws IOException {
        File requestFile=new File(REQUEST_FILE_DIR+REQUEST_FILE_NAME+SUFFIX);
        InputStreamReader read = new InputStreamReader(new FileInputStream(requestFile));
        BufferedReader bufferedReader = new BufferedReader(read);

        FileOutputStream out = new FileOutputStream(REQUEST_FILE_DIR+REQUEST_FILE_NAME+"_DeleteDuplicateOD"+SUFFIX);
        String outStr = "";
        int count = 0;
        String lineTxt = "";
        while((lineTxt = bufferedReader.readLine()) != null) {
            String s[] = lineTxt.split("#");
            String origin = s[0];
            String destination = s[1];

            if(!origin.equals(destination)){
                outStr = origin+"#"+destination+"#"+s[2]+"\r\n";
                out.write(outStr.getBytes());
            }
        }
        out.close();
    }
}
