package fine.koaca.wms;

public class ActivityEquipFacilityList {
    String date;
    String eFName;
    String manageContent;
    String remark;
    String process;

    int estimateAmount;
    int confirmAmount;

    public ActivityEquipFacilityList(){

    }
    public ActivityEquipFacilityList(String date, String eFName, String manageContent, String remark, String process,
                                    int estimateAmount, int confirmAmount) {
        this.date = date;
        this.eFName = eFName;
        this.manageContent = manageContent;
        this.remark = remark;
        this.process = process;
        this.estimateAmount = estimateAmount;
        this.confirmAmount = confirmAmount;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String geteFName() {
        return eFName;
    }

    public void seteFName(String eFName) {
        this.eFName = eFName;
    }

    public String getManageContent() {
        return manageContent;
    }

    public void setManageContent(String manageContent) {
        this.manageContent = manageContent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public int getEstimateAmount() {
        return estimateAmount;
    }

    public void setEstimateAmount(int estimateAmount) {
        this.estimateAmount = estimateAmount;
    }

    public int getConfirmAmount() {
        return confirmAmount;
    }

    public void setConfirmAmount(int confirmAmount) {
        this.confirmAmount = confirmAmount;
    }
}
