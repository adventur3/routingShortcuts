package recorder;

import java.util.LinkedList;
import java.util.List;

public class PerformanceRecorder {

    private LinkedList<Long> dijkstraLength;   //ms
    private LinkedList<Long> astarLength;
    private LinkedList<Long> dwsLength;
    private LinkedList<Long> awsLength;
    private LinkedList<Long> aws_maLength;
    private LinkedList<Long> aws_hoeLength;

    private long dijkstraTotalSearchTime;  //ms
    private long astarTotalSearchTime;
    private long dwsTotalSearchTime;
    private long awsTotalSearchTime;
    private long aws_maTotalSearchTime;
    private long aws_hoeTotalSearchTime;

    private int requestNum;

    public PerformanceRecorder(int requestNum){
        dijkstraLength = new LinkedList<Long>();
        astarLength = new LinkedList<Long>();
        dwsLength = new LinkedList<Long>();
        awsLength = new LinkedList<Long>();
        aws_maLength = new LinkedList<Long>();
        aws_hoeLength = new LinkedList<Long>();
        dijkstraTotalSearchTime = 0;
        astarTotalSearchTime = 0;
        dwsTotalSearchTime = 0;
        awsTotalSearchTime = 0;
        aws_maTotalSearchTime = 0;
        aws_hoeTotalSearchTime = 0;
        this.requestNum = requestNum;
    }

    public void addSearchTime(AlgorithmType algorithmType, Long searchTime){
        switch(algorithmType){
            case DIJKSTRA: dijkstraTotalSearchTime += searchTime; break;
            case    ASTAR:    astarTotalSearchTime += searchTime; break;
            case      DWS:      dwsTotalSearchTime += searchTime; break;
            case      AWS:      awsTotalSearchTime += searchTime; break;
            case   AWS_MA:   aws_maTotalSearchTime += searchTime; break;
            case  AWS_HOE:  aws_hoeTotalSearchTime += searchTime; break;
        }
    }

    public void addLength(AlgorithmType algorithmType, Long length){
        switch(algorithmType){
            case DIJKSTRA: dijkstraLength.add(length); break;
            case    ASTAR:    astarLength.add(length); break;
            case      DWS:      dwsLength.add(length); break;
            case      AWS:      awsLength.add(length); break;
            case   AWS_MA:   aws_maLength.add(length); break;
            case  AWS_HOE:  aws_hoeLength.add(length); break;
        }
    }

    /*
    measurement:s
    */
    public double calculateMeanLength(AlgorithmType algorithmType){
        switch(algorithmType){
            case DIJKSTRA: return meanOfList(dijkstraLength)/1000;
            case    ASTAR:    return meanOfList(astarLength)/1000;
            case      DWS:      return meanOfList(dwsLength)/1000;
            case      AWS:      return meanOfList(awsLength)/1000;
            case   AWS_MA:   return meanOfList(aws_maLength)/1000;
            case  AWS_HOE:  return meanOfList(aws_hoeLength)/1000;
                  default:                          return 0;
        }
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
        switch(algorithmType1){
            case DIJKSTRA: list1 = dijkstraLength; break;
            case    ASTAR:    list1 = astarLength; break;
            case      DWS:      list1 = dwsLength; break;
            case      AWS:      list1 = awsLength; break;
            case   AWS_MA:   list1 = aws_maLength; break;
            case  AWS_HOE:  list1 = aws_hoeLength; break;
            default:                     return 0;
        }
        switch(algorithmType2){
            case DIJKSTRA: list2 = dijkstraLength; break;
            case    ASTAR:    list2 = astarLength; break;
            case      DWS:      list2 = dwsLength; break;
            case      AWS:      list2 = awsLength; break;
            case   AWS_MA:   list2 = aws_maLength; break;
            case  AWS_HOE:  list2 = aws_hoeLength; break;
            default:                     return 0;
        }
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
        switch(algorithmType1){
            case DIJKSTRA: list1 = dijkstraLength; break;
            case    ASTAR:    list1 = astarLength; break;
            case      DWS:      list1 = dwsLength; break;
            case      AWS:      list1 = awsLength; break;
            case   AWS_MA:   list1 = aws_maLength; break;
            case  AWS_HOE:  list1 = aws_hoeLength; break;
            default:                     return 0;
        }
        switch(algorithmType2){
            case DIJKSTRA: list2 = dijkstraLength; break;
            case    ASTAR:    list2 = astarLength; break;
            case      DWS:      list2 = dwsLength; break;
            case      AWS:      list2 = awsLength; break;
            case   AWS_MA:   list2 = aws_maLength; break;
            case  AWS_HOE:  list2 = aws_hoeLength; break;
            default:                     return 0;
        }
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
        switch(algorithmType1){
            case DIJKSTRA: list1 = dijkstraLength; break;
            case    ASTAR:    list1 = astarLength; break;
            case      DWS:      list1 = dwsLength; break;
            case      AWS:      list1 = awsLength; break;
            case   AWS_MA:   list1 = aws_maLength; break;
            case  AWS_HOE:  list1 = aws_hoeLength; break;
            default:                     return 0;
        }
        switch(algorithmType2){
            case DIJKSTRA: list2 = dijkstraLength; break;
            case    ASTAR:    list2 = astarLength; break;
            case      DWS:      list2 = dwsLength; break;
            case      AWS:      list2 = awsLength; break;
            case   AWS_MA:   list2 = aws_maLength; break;
            case  AWS_HOE:  list2 = aws_hoeLength; break;
            default:                     return 0;
        }
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
        switch(algorithmType){
            case DIJKSTRA: return dijkstraTotalSearchTime;
            case    ASTAR:    return astarTotalSearchTime;
            case      DWS:      return dwsTotalSearchTime;
            case      AWS:      return awsTotalSearchTime;
            case   AWS_MA:   return aws_maTotalSearchTime;
            case  AWS_HOE:  return aws_hoeTotalSearchTime;
            default:                          return 0;
        }
    }

    /*
    measurement:ms
    */
    public double getMeanSearchTime(AlgorithmType algorithmType){
        switch(algorithmType){
            case DIJKSTRA: return (double)dijkstraTotalSearchTime/this.requestNum;
            case    ASTAR:    return (double)astarTotalSearchTime/this.requestNum;
            case      DWS:      return (double)dwsTotalSearchTime/this.requestNum;
            case      AWS:      return (double)awsTotalSearchTime/this.requestNum;
            case   AWS_MA:   return (double)aws_maTotalSearchTime/this.requestNum;
            case  AWS_HOE:  return (double)aws_hoeTotalSearchTime/this.requestNum;
            default:                          return 0;
        }
    }

    /*
    measurement:s
    */
    public double calculateMeanOfDiff(AlgorithmType algorithmType1, AlgorithmType algorithmType2){
        List<Long> list1;
        List<Long> list2;
        switch(algorithmType1){
            case DIJKSTRA: list1 = dijkstraLength; break;
            case    ASTAR:    list1 = astarLength; break;
            case      DWS:      list1 = dwsLength; break;
            case      AWS:      list1 = awsLength; break;
            case   AWS_MA:   list1 = aws_maLength; break;
            case  AWS_HOE:  list1 = aws_hoeLength; break;
            default:                     return 0;
        }
        switch(algorithmType2){
            case DIJKSTRA: list2 = dijkstraLength; break;
            case    ASTAR:    list2 = astarLength; break;
            case      DWS:      list2 = dwsLength; break;
            case      AWS:      list2 = awsLength; break;
            case   AWS_MA:   list2 = aws_maLength; break;
            case  AWS_HOE:  list2 = aws_hoeLength; break;
            default:                     return 0;
        }
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
}
