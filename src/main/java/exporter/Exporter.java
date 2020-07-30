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
        //输出Excel文件
        FileOutputStream output=new FileOutputStream(outFilePath);
        wb.write(output);
        output.flush();
    }

}
