package fine.koaca.wms;

import java.io.Serializable;

public class OutCargoList implements Serializable {
    String consigneeName;
    String date;
    String description;
    String managementNo;
    String outwarehouse;
    String eaQty;
    String pltQty;
    String totalQty;
    String keypath;
    String workprocess;

    public OutCargoList(){

    }

    public OutCargoList(String consigneeName, String date, String description, String managementNo, String outwarehouse, String eaQty, String pltQty, String totalQty, String keypath, String workprocess) {
        this.consigneeName = consigneeName;
        this.date = date;
        this.description = description;
        this.managementNo = managementNo;
        this.outwarehouse = outwarehouse;
        this.eaQty = eaQty;
        this.pltQty = pltQty;
        this.totalQty = totalQty;
        this.keypath = keypath;
        this.workprocess = workprocess;
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

    public String getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(String totalQty) {
        this.totalQty = totalQty;
    }

    public String getKeypath() {
        return keypath;
    }

    public void setKeypath(String keypath) {
        this.keypath = keypath;
    }

    public String getWorkprocess() {
        return workprocess;
    }

    public void setWorkprocess(String workprocess) {
        this.workprocess = workprocess;
    }
}
