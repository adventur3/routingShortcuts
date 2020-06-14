package simulator;

public class Recorder {
    private int restrainedSearchCount_DWS;
    private int restrainedSearchCount_AWS;
    private int restrainedSearchCount_AWS_MA;
    private int restrainedSearchCount_AWS_HOE;
//    private int shortcutUseCount_DWS;
//    private int shortcutUseCount_AWS;
    private int shortcutUseCount_AWS_MA;
//    private int shortcutUseCount_AWS_HOE;

    public Recorder(){
        restrainedSearchCount_DWS = 0;
        restrainedSearchCount_AWS = 0;
        restrainedSearchCount_AWS_MA = 0;
        restrainedSearchCount_AWS_HOE = 0;
//        shortcutUseCount_DWS = 0;
//        shortcutUseCount_AWS = 0;
        shortcutUseCount_AWS_MA = 0;
//        shortcutUseCount_AWS_HOE = 0;
    }

    public void restrainedSearchCount_DWS_AddOne(){
        this.restrainedSearchCount_DWS ++;
    }

    public void restrainedSearchCount_AWS_AddOne(){
        this.restrainedSearchCount_AWS ++;
    }

    public void restrainedSearchCount_AWS_MA_AddOne(){
        this.restrainedSearchCount_AWS_MA ++;
    }

    public void restrainedSearchCount_AWS_HOE_AddOne(){
        this.restrainedSearchCount_AWS_HOE ++;
    }

//    public void shortcutUseCount_DWS_AddOne(){
//        this.shortcutUseCount_DWS ++;
//    }
//
//    public void shortcutUseCount_AWS_AddOne(){
//        this.shortcutUseCount_AWS ++;
//    }
//
    public void shortcutUseCount_AWS_MA_AddOne(){
        this.shortcutUseCount_AWS_MA ++;
    }
//
//    public void shortcutUseCount_AWS_HOE_AddOne(){
//        this.shortcutUseCount_AWS_HOE ++;
//    }

    public int getRestrainedSearchCount_DWS() {
        return restrainedSearchCount_DWS;
    }

    public void setRestrainedSearchCount_DWS(int restrainedSearchCount_DWS) {
        this.restrainedSearchCount_DWS = restrainedSearchCount_DWS;
    }

    public int getRestrainedSearchCount_AWS() {
        return restrainedSearchCount_AWS;
    }

    public void setRestrainedSearchCount_AWS(int restrainedSearchCount_AWS) {
        this.restrainedSearchCount_AWS = restrainedSearchCount_AWS;
    }

    public int getRestrainedSearchCount_AWS_MA() {
        return restrainedSearchCount_AWS_MA;
    }

    public void setRestrainedSearchCount_AWS_MA(int restrainedSearchCount_AWS_MA) {
        this.restrainedSearchCount_AWS_MA = restrainedSearchCount_AWS_MA;
    }

    public int getRestrainedSearchCount_AWS_HOE() {
        return restrainedSearchCount_AWS_HOE;
    }

    public void setRestrainedSearchCount_AWS_HOE(int restrainedSearchCount_AWS_HOE) {
        this.restrainedSearchCount_AWS_HOE = restrainedSearchCount_AWS_HOE;
    }

//    public int getShortcutUseCount_DWS() {
//        return shortcutUseCount_DWS;
//    }
//
//    public void setShortcutUseCount_DWS(int shortcutUseCount_DWS) {
//        this.shortcutUseCount_DWS = shortcutUseCount_DWS;
//    }
//
//    public int getShortcutUseCount_AWS() {
//        return shortcutUseCount_AWS;
//    }
//
//    public void setShortcutUseCount_AWS(int shortcutUseCount_AWS) {
//        this.shortcutUseCount_AWS = shortcutUseCount_AWS;
//    }
//
    public int getShortcutUseCount_AWS_MA() {
        return shortcutUseCount_AWS_MA;
    }
//
//    public void setShortcutUseCount_AWS_MA(int shortcutUseCount_AWS_MA) {
//        this.shortcutUseCount_AWS_MA = shortcutUseCount_AWS_MA;
//    }
//
//    public int getShortcutUseCount_AWS_HOE() {
//        return shortcutUseCount_AWS_HOE;
//    }
//
//    public void setShortcutUseCount_AWS_HOE(int shortcutUseCount_AWS_HOE) {
//        this.shortcutUseCount_AWS_HOE = shortcutUseCount_AWS_HOE;
//    }
}
