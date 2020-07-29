package recorder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PerformanceRecorder {

//    private LinkedList<Long> dijkstraLength;   //ms
//    private LinkedList<Long> astarLength;
//    private LinkedList<Long> dwsLength;
//    private LinkedList<Long> awsLength;
//    private LinkedList<Long> aws_maLength;
//    private LinkedList<Long> aws_hoeLength;
//
//    private long dijkstraTotalSearchTime;  //ms
//    private long astarTotalSearchTime;
//    private long dwsTotalSearchTime;
//    private long awsTotalSearchTime;
//    private long aws_maTotalSearchTime;
//    private long aws_hoeTotalSearchTime;

    Map<AlgorithmType, Long> searchTimes;
    Map<AlgorithmType, LinkedList<Long>> lengths;

    private int requestNum;

    public PerformanceRecorder(int requestNum){
        searchTimes = new HashMap<AlgorithmType, Long>();
        lengths= new HashMap<AlgorithmType, LinkedList<Long>>();
        this.requestNum = requestNum;
    }

    public void addSearchTime(AlgorithmType algorithmType, Long searchTime){
        if(searchTimes.containsKey(algorithmType)){
            searchTimes.put(algorithmType,searchTimes.get(algorithmType)+searchTime);
        }else{
            searchTimes.put(algorithmType,searchTime);
        }
    }

    public void addLength(AlgorithmType algorithmType, Long length){
        if(lengths.containsKey(algorithmType)){
            LinkedList<Long> list = lengths.get(algorithmType);
            list.add(length);
            lengths.put(algorithmType, list);
        }else{
            LinkedList<Long> list = new LinkedList<Long>();
            list.add(length);
            lengths.put(algorithmType, list);
        }
    }

    /*
    measurement:s
    */
    public double calculateMeanLength(AlgorithmType algorithmType){
        return meanOfList(lengths.get(algorithmType))/1000;
    }

    public double meanOfList(List<Long> list){
        double sum = 0;
        if(list == null || list.isEmpty()){
            return 0;
        }else{
            for(Long num : list){
                sum += num;
            }
            return sum/list.size();
        }
    }

    /*
        measurement:s
     */
    public long calculateMinOfDiff(AlgorithmType algorithmType1, AlgorithmType algorithmType2){
        List<Long> list1;
        List<Long> list2;
        list1 = lengths.get(algorithmType1);
        list2 = lengths.get(algorithmType2);
        if(list1 == null || list2 == null || list1.size()!=list2.size()){
            System.out.println("the sizes is not equal!");
            return 0;
        }
        long min = Long.MAX_VALUE;
        for(int i=0;i<list1.size();i++){
            long shortest = list1.get(i)/1000;   //ms -> s
            long other = list2.get(i)/1000;   //ms -> s
            long diff = other - shortest;
            if(diff<0 && diff>-600){
                diff = 1;
            }
            if(diff<min) min = diff;
        }
        return min;
    }

    /*
    measurement:s
 */
    public long calculateMaxOfDiff(AlgorithmType algorithmType1, AlgorithmType algorithmType2){
        List<Long> list1;
        List<Long> list2;
        list1 = lengths.get(algorithmType1);
        list2 = lengths.get(algorithmType2);
        if(list1 == null || list2 == null || list1.size()!=list2.size()){
            System.out.println("the sizes is not equal!");
            return 0;
        }
        long max = Long.MIN_VALUE;
        for(int i=0;i<list1.size();i++){
            long shortest = list1.get(i)/1000;   //ms -> s
            long other = list2.get(i)/1000;   //ms -> s
            long diff = other - shortest;
            if(diff<0 && diff>-600){
                diff = 1;
            }
            if(diff>max) max = diff;
        }
        return max;
    }

    public double calculateVarianceOfDiff(AlgorithmType algorithmType1, AlgorithmType algorithmType2){
        List<Long> list1;
        List<Long> list2;
        list1 = lengths.get(algorithmType1);
        list2 = lengths.get(algorithmType2);
        if(list1 == null || list2 == null || list1.size()!=list2.size()){
            System.out.println("the sizes is not equal!");
            return 0;
        }
        LinkedList<Long> diffList = new LinkedList<Long>();
        for(int i=0;i<list1.size();i++){
            long shortest = list1.get(i)/1000;   //ms -> s
            long other = list2.get(i)/1000;   //ms -> s
            long diff = other - shortest;
            if(diff<0 && diff>-600){
                diff = 1;
            }
            diffList.add(diff);
        }
        double meanDiff = meanOfList(diffList);
        double variance = 0;
        for(int i=0;i<diffList.size();i++){
            variance += (diffList.get(i)- meanDiff) * (diffList.get(i)- meanDiff);
        }
        return variance/diffList.size();
    }

    /*
    measurement:ms
    */
    public long getTotalSearchTime(AlgorithmType algorithmType){
        return searchTimes.get(algorithmType);
    }

    /*
    measurement:ms
    */
    public double getMeanSearchTime(AlgorithmType algorithmType){
        return (double)searchTimes.get(algorithmType)/this.requestNum;
    }

    /*
    measurement:s
    */
    public double calculateMeanOfDiff(AlgorithmType algorithmType1, AlgorithmType algorithmType2){
        List<Long> list1;
        List<Long> list2;
        list1 = lengths.get(algorithmType1);
        list2 = lengths.get(algorithmType2);
        if(list1 == null || list2 == null || list1.size()!=list2.size()){
            System.out.println("the sizes is not equal!");
            return 0;
        }
        LinkedList<Long> diffList = new LinkedList<Long>();
        for(int i=0;i<list1.size();i++){
            long shortest = list1.get(i)/1000;   //ms -> s
            long other = list2.get(i)/1000;   //ms -> s
            long diff = other - shortest;
            if(diff<0 && diff>-600){
                diff = 1;
            }
            diffList.add(diff);
        }
        double meanDiff = meanOfList(diffList);
        return meanDiff;
    }

    public int getRequestNum() {
        return requestNum;
    }
}
