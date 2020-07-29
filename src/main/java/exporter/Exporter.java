package exporter;

import org.antlr.v4.runtime.atn.LexerPopModeAction;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import recorder.AlgorithmType;
import recorder.PerformanceRecorder;
import recorder.PerformanceType;
import recorder.ShortcutHitRecorder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Exporter {

//    public static void main(String[] args) throws IOException{
//        Exporter e = new Exporter();
//        e.exportPerformance();
//    }

    public void exportPerformance(String outFilePath, PerformanceRecorder performanceRecorder, ShortcutHitRecorder shortcutHitRecorder) throws IOException {
        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");
        //创建HSSFRow对象
        HSSFRow the0Row = sheet.createRow(0);
        //创建HSSFCell对象
        the0Row.createCell(0);
//        HSSFCell cell1=row.createCell(1);
//        HSSFCell cell2=row.createCell(2);
//        HSSFCell cell3=row.createCell(3);
//        HSSFCell cell4=row.createCell(4);
//        HSSFCell cell5=row.createCell(5);
//        HSSFCell cell6=row.createCell(6);
//        //设置单元格的值
//        cell1.setCellValue("DIJKSTRA");
//        cell2.setCellValue("ASTAR");
//        cell3.setCellValue("DWS");
//        cell4.setCellValue("AWS");
//        cell5.setCellValue("AWS_MA");
//        cell6.setCellValue("AWS_HOE");
        int row0CellIndex = 1;
        for(AlgorithmType at : AlgorithmType.values()){
            the0Row.createCell(row0CellIndex++).setCellValue(at.name());
        }
        int rowIndex = 1;
        for(PerformanceType pt : PerformanceType.values()){
            HSSFRow tempRow = sheet.createRow(rowIndex++);
            switch(pt) {
                case MEAN_LENGTH_s:
                    tempRow.createCell(0).setCellValue(pt.name());
                    int meanLengthCellIndex = 1;
                    for(AlgorithmType at : AlgorithmType.values()){
                        tempRow.createCell(meanLengthCellIndex++).setCellValue(performanceRecorder.calculateMeanLength(at));
                    }
                    break;
                case MEAN_SEARCH_TIME_ms:
                    tempRow.createCell(0).setCellValue(pt.name());
                    int meanSearchTimeCellIndex = 1;
                    for(AlgorithmType at : AlgorithmType.values()){
                        tempRow.createCell(meanSearchTimeCellIndex++).setCellValue(performanceRecorder.getMeanSearchTime(at));
                    }
                    break;
                case RESTRAINED_SEARCH_COUNT:
                    tempRow.createCell(0).setCellValue(pt.name());
                    int restrainedSearchCountCellIndex = 1;
                    for(AlgorithmType at : AlgorithmType.values()){
                        tempRow.createCell(restrainedSearchCountCellIndex++).setCellValue(shortcutHitRecorder.getRestrainedSearchCount(at));
                    }
                    break;
                case SHORTCUT_HIT_COUNT:
                    tempRow.createCell(0).setCellValue(pt.name());
                    int shortcutHitCountCellIndex = 1;
                    for(AlgorithmType at : AlgorithmType.values()){
                        tempRow.createCell(shortcutHitCountCellIndex++).setCellValue(shortcutHitRecorder.getShortcutHitCount(at));
                    }
                    break;
                case SHORTCUT_HIT_RATE:
                    tempRow.createCell(0).setCellValue(pt.name());
                    int shortcutHitRateCellIndex = 1;
                    for(AlgorithmType at : AlgorithmType.values()){
                        tempRow.createCell(shortcutHitRateCellIndex++).setCellValue((double)shortcutHitRecorder.getShortcutHitCount(at)/performanceRecorder.getRequestNum());
                    }
                    break;
                case MAX_DIFF_s:
                    tempRow.createCell(0).setCellValue(pt.name());
                    int maxDiffCellIndex = 1;
                    for(AlgorithmType at : AlgorithmType.values()){
                        tempRow.createCell(maxDiffCellIndex++).setCellValue(performanceRecorder.calculateMaxOfDiff(AlgorithmType.TDA, at));
                    }
                    break;
                case MIN_DIFF_s:
                    tempRow.createCell(0).setCellValue(pt.name());
                    int minDiffCellIndex = 1;
                    for(AlgorithmType at : AlgorithmType.values()){
                        tempRow.createCell(minDiffCellIndex++).setCellValue(performanceRecorder.calculateMinOfDiff(AlgorithmType.TDA, at));
                    }
                    break;
                case MEAN_DIFF_s:
                    tempRow.createCell(0).setCellValue(pt.name());
                    int meanDiffCellIndex = 1;
                    for(AlgorithmType at : AlgorithmType.values()){
                        tempRow.createCell(meanDiffCellIndex++).setCellValue(performanceRecorder.calculateMeanOfDiff(AlgorithmType.TDA, at));
                    }
                    break;
            }
        }
//        //路径平均长度
//        HSSFRow row1 = sheet.createRow(1);
//        List<HSSFCell> cellInRow1 = new ArrayList<HSSFCell>();
//        for(int i=0;i<7;i++){
//            cellInRow1.add(row1.createCell(i));
//        }
//        for(int i=0;i<7;i++){
//            if(i==0){
//                cellInRow1.get(i).setCellValue("mean length(s)");
//            }else if(i==1){
//                cellInRow1.get(i).setCellValue(performanceRecorder.calculateMeanLength(AlgorithmType.DIJKSTRA));
//            }else if(i==2){
//                cellInRow1.get(i).setCellValue(performanceRecorder.calculateMeanLength(AlgorithmType.ASTAR));
//            }else if(i==3){
//                cellInRow1.get(i).setCellValue(performanceRecorder.calculateMeanLength(AlgorithmType.DWS));
//            }else if(i==4){
//                cellInRow1.get(i).setCellValue(performanceRecorder.calculateMeanLength(AlgorithmType.AWS));
//            }else if(i==5){
//                cellInRow1.get(i).setCellValue(performanceRecorder.calculateMeanLength(AlgorithmType.AWS_MA));
//            }else{
//                cellInRow1.get(i).setCellValue(performanceRecorder.calculateMeanLength(AlgorithmType.AWS_HOE));
//            }
//        }
//        //总寻路时间
//        HSSFRow row2 = sheet.createRow(2);
//        List<HSSFCell> cellInRow2 = new ArrayList<HSSFCell>();
//        for(int i=0;i<7;i++){
//            cellInRow2.add(row2.createCell(i));
//        }
//        for(int i=0;i<7;i++){
//            if(i==0){
//                cellInRow2.get(i).setCellValue("mean search time(ms)");
//            }else if(i==1){
//                cellInRow2.get(i).setCellValue(performanceRecorder.getMeanSearchTime(AlgorithmType.DIJKSTRA));
//            }else if(i==2){
//                cellInRow2.get(i).setCellValue(performanceRecorder.getMeanSearchTime(AlgorithmType.ASTAR));
//            }else if(i==3){
//                cellInRow2.get(i).setCellValue(performanceRecorder.getMeanSearchTime(AlgorithmType.DWS));
//            }else if(i==4){
//                cellInRow2.get(i).setCellValue(performanceRecorder.getMeanSearchTime(AlgorithmType.AWS));
//            }else if(i==5){
//                cellInRow2.get(i).setCellValue(performanceRecorder.getMeanSearchTime(AlgorithmType.AWS_MA));
//            }else{
//                cellInRow2.get(i).setCellValue(performanceRecorder.getMeanSearchTime(AlgorithmType.AWS_HOE));
//            }
//        }
//        //max diff
//        HSSFRow row3 = sheet.createRow(3);
//        List<HSSFCell> cellInRow3 = new ArrayList<HSSFCell>();
//        for(int i=0;i<7;i++){
//            cellInRow3.add(row3.createCell(i));
//        }
//        for(int i=0;i<7;i++){
//            if(i==0){
//                cellInRow3.get(i).setCellValue("max diff(s)");
//            }else if(i==1){
//                cellInRow3.get(i).setCellValue(performanceRecorder.calculateMaxOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.DIJKSTRA));
//            }else if(i==2){
//                cellInRow3.get(i).setCellValue(performanceRecorder.calculateMaxOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.ASTAR));
//            }else if(i==3){
//                cellInRow3.get(i).setCellValue(performanceRecorder.calculateMaxOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.DWS));
//            }else if(i==4){
//                cellInRow3.get(i).setCellValue(performanceRecorder.calculateMaxOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.AWS));
//            }else if(i==5){
//                cellInRow3.get(i).setCellValue(performanceRecorder.calculateMaxOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.AWS_MA));
//            }else{
//                cellInRow3.get(i).setCellValue(performanceRecorder.calculateMaxOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.AWS_HOE));
//            }
//        }
//
//        //min diff
//        HSSFRow row4 = sheet.createRow(4);
//        List<HSSFCell> cellInRow4 = new ArrayList<HSSFCell>();
//        for(int i=0;i<7;i++){
//            cellInRow4.add(row4.createCell(i));
//        }
//        for(int i=0;i<7;i++){
//            if(i==0){
//                cellInRow4.get(i).setCellValue("min diff(s)");
//            }else if(i==1){
//                cellInRow4.get(i).setCellValue(performanceRecorder.calculateMinOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.DIJKSTRA));
//            }else if(i==2){
//                cellInRow4.get(i).setCellValue(performanceRecorder.calculateMinOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.ASTAR));
//            }else if(i==3){
//                cellInRow4.get(i).setCellValue(performanceRecorder.calculateMinOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.DWS));
//            }else if(i==4){
//                cellInRow4.get(i).setCellValue(performanceRecorder.calculateMinOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.AWS));
//            }else if(i==5){
//                cellInRow4.get(i).setCellValue(performanceRecorder.calculateMinOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.AWS_MA));
//            }else{
//                cellInRow4.get(i).setCellValue(performanceRecorder.calculateMinOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.AWS_HOE));
//            }
//        }
//
//        //mean diff
//        HSSFRow row5 = sheet.createRow(5);
//        List<HSSFCell> cellInRow5 = new ArrayList<HSSFCell>();
//        for(int i=0;i<7;i++){
//            cellInRow5.add(row5.createCell(i));
//        }
//        for(int i=0;i<7;i++){
//            if(i==0){
//                cellInRow5.get(i).setCellValue("mean diff(s)");
//            }else if(i==1){
//                cellInRow5.get(i).setCellValue(performanceRecorder.calculateMeanOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.DIJKSTRA));
//            }else if(i==2){
//                cellInRow5.get(i).setCellValue(performanceRecorder.calculateMeanOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.ASTAR));
//            }else if(i==3){
//                cellInRow5.get(i).setCellValue(performanceRecorder.calculateMeanOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.DWS));
//            }else if(i==4){
//                cellInRow5.get(i).setCellValue(performanceRecorder.calculateMeanOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.AWS));
//            }else if(i==5){
//                cellInRow5.get(i).setCellValue(performanceRecorder.calculateMeanOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.AWS_MA));
//            }else{
//                cellInRow5.get(i).setCellValue(performanceRecorder.calculateMeanOfDiff(AlgorithmType.DIJKSTRA, AlgorithmType.AWS_HOE));
//            }
//        }
//
//        //restrained search count
//        HSSFRow row6 = sheet.createRow(6);
//        List<HSSFCell> cellInRow6 = new ArrayList<HSSFCell>();
//        for(int i=0;i<7;i++){
//            cellInRow6.add(row6.createCell(i));
//        }
//        for(int i=0;i<7;i++){
//            if(i==0){
//                cellInRow6.get(i).setCellValue("restrained search count");
//            }else if(i==1){
//                cellInRow6.get(i).setCellValue(0);
//            }else if(i==2){
//                cellInRow6.get(i).setCellValue(0);
//            }else if(i==3){
//                cellInRow6.get(i).setCellValue(shortcutHitRecorder.getRestrainedSearchCount_DWS());
//            }else if(i==4){
//                cellInRow6.get(i).setCellValue(shortcutHitRecorder.getRestrainedSearchCount_AWS());
//            }else if(i==5){
//                cellInRow6.get(i).setCellValue(shortcutHitRecorder.getRestrainedSearchCount_AWS_MA());
//            }else{
//                cellInRow6.get(i).setCellValue(shortcutHitRecorder.getRestrainedSearchCount_AWS_HOE());
//            }
//        }
//
//        //shortcuts hit count
//        HSSFRow row7 = sheet.createRow(7);
//        List<HSSFCell> cellInRow7 = new ArrayList<HSSFCell>();
//        for(int i=0;i<7;i++){
//            cellInRow7.add(row7.createCell(i));
//        }
//        for(int i=0;i<7;i++){
//            if(i==0){
//                cellInRow7.get(i).setCellValue("shortcuts hit count");
//            }else if(i==1){
//                cellInRow7.get(i).setCellValue(0);
//            }else if(i==2){
//                cellInRow7.get(i).setCellValue(0);
//            }else if(i==3){
//                cellInRow7.get(i).setCellValue(shortcutHitRecorder.getShortcutUseCount_DWS());
//            }else if(i==4){
//                cellInRow7.get(i).setCellValue(shortcutHitRecorder.getShortcutUseCount_AWS());
//            }else if(i==5){
//                cellInRow7.get(i).setCellValue(shortcutHitRecorder.getShortcutUseCount_AWS_MA());
//            }else{
//                cellInRow7.get(i).setCellValue(shortcutHitRecorder.getShortcutUseCount_AWS_HOE());
//            }
//        }
//
//        //shortcuts hit rate
//        HSSFRow row8 = sheet.createRow(8);
//        List<HSSFCell> cellInRow8 = new ArrayList<HSSFCell>();
//        for(int i=0;i<7;i++){
//            cellInRow8.add(row8.createCell(i));
//        }
//        for(int i=0;i<7;i++){
//            if(i==0){
//                cellInRow8.get(i).setCellValue("shortcuts hit rate");
//            }else if(i==1){
//                cellInRow8.get(i).setCellValue(0);
//            }else if(i==2){
//                cellInRow8.get(i).setCellValue(0);
//            }else if(i==3){
//                cellInRow8.get(i).setCellValue((double)shortcutHitRecorder.getShortcutUseCount_DWS()/performanceRecorder.getRequestNum());
//            }else if(i==4){
//                cellInRow8.get(i).setCellValue((double)shortcutHitRecorder.getShortcutUseCount_AWS()/performanceRecorder.getRequestNum());
//            }else if(i==5){
//                cellInRow8.get(i).setCellValue((double)shortcutHitRecorder.getShortcutUseCount_AWS_MA()/performanceRecorder.getRequestNum());
//            }else{
//                cellInRow8.get(i).setCellValue((double)shortcutHitRecorder.getShortcutUseCount_AWS_HOE()/performanceRecorder.getRequestNum());
//            }
//        }

        //输出Excel文件
        FileOutputStream output=new FileOutputStream(outFilePath);
        wb.write(output);
        output.flush();
    }

}
