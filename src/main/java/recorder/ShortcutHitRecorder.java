package recorder;

import java.util.HashMap;
import java.util.Map;

public class ShortcutHitRecorder {

    private Map<AlgorithmType, Integer>  restrainedSearchCounts;
    private Map<AlgorithmType, Integer> shortcutHitCounts;

    public ShortcutHitRecorder(){
        restrainedSearchCounts = new HashMap<AlgorithmType, Integer>();
        shortcutHitCounts = new HashMap<AlgorithmType, Integer>();
    }

    public void restrainedSearchCountAddOne(AlgorithmType algorithmType){
        if(restrainedSearchCounts.containsKey(algorithmType)){
            restrainedSearchCounts.put(algorithmType, restrainedSearchCounts.get(algorithmType)+1);
        }else{
            restrainedSearchCounts.put(algorithmType, 1);
        }
    }

    public void shortcutHitCountAddOne(AlgorithmType algorithmType){
        if(shortcutHitCounts.containsKey(algorithmType)){
            shortcutHitCounts.put(algorithmType, shortcutHitCounts.get(algorithmType)+1);
        }else{
            shortcutHitCounts.put(algorithmType, 1);
        }
    }

    public int getRestrainedSearchCount(AlgorithmType algorithmType){
        if(restrainedSearchCounts.containsKey(algorithmType)){
            return restrainedSearchCounts.get(algorithmType);
        }else{
            return 0;
        }
    }

    public int getShortcutHitCount(AlgorithmType algorithmType){
        if(shortcutHitCounts.containsKey(algorithmType)){
            return shortcutHitCounts.get(algorithmType);
        }else{
            return 0;
        }
    }
}
