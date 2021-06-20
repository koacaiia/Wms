package fine.koaca.wms;

public class OutCargoList {
    String consigneeName;
    String date;
    String description;
    String managementNo;
    String outwarehouse;
    String eaQty;
    String pltQty;
    int totalQty;

    public OutCargoList(){

    }

    public OutCargoList(String consigneeName, String date, String description, String managementNo, String outwarehouse, String eaQty, String pltQty, int totalQty) {
        this.consigneeName = consigneeName;
        this.date = date;
        this.description = description;
        this.managementNo = managementNo;
        this.outwarehouse = outwarehouse;
        this.eaQty = eaQty;
        this.pltQty = pltQty;
        this.totalQty = totalQty;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManagementNo() {
        return managementNo;
    }

    public void setManagementNo(String managementNo) {
        this.managementNo = managementNo;
    }

    public String getOutwarehouse() {
        return outwarehouse;
    }

    public void setOutwarehouse(String outwarehouse) {
        this.outwarehouse = outwarehouse;
    }

    public String getEaQty() {
        return eaQty;
    }

    public void setEaQty(String eaQty) {
        this.eaQty = eaQty;
    }

    public String getPltQty() {
        return pltQty;
    }

    public void setPltQty(String pltQty) {
        this.pltQty = pltQty;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }
}
