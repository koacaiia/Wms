package fine.koaca.wms;

public class ListWeekSchedule {
   String consignee;
    String container40;
    String container20;
    String lclcargo;
    String date;
    String outSourcingMale;
    String outSourcingFemale;
public ListWeekSchedule(){

}
    public ListWeekSchedule(String consignee, String container40, String container20, String lclcargo, String date, String outSourcingMale, String outSourcingFemale) {
        this.consignee = consignee;
        this.container40 = container40;
        this.container20 = container20;
        this.lclcargo = lclcargo;
        this.date = date;
        this.outSourcingMale = outSourcingMale;
        this.outSourcingFemale = outSourcingFemale;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getContainer40() {
        return container40;
    }

    public void setContainer40(String container40) {
        this.container40 = container40;
    }

    public String getContainer20() {
        return container20;
    }

    public void setContainer20(String container20) {
        this.container20 = container20;
    }

    public String getLclcargo() {
        return lclcargo;
    }

    public void setLclcargo(String lclcargo) {
        this.lclcargo = lclcargo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOutSourcingMale() {
        return outSourcingMale;
    }

    public void setOutSourcingMale(String outSourcingMale) {
        this.outSourcingMale = outSourcingMale;
    }

    public String getOutSourcingFemale() {
        return outSourcingFemale;
    }

    public void setOutSourcingFemale(String outSourcingFemale) {
        this.outSourcingFemale = outSourcingFemale;
    }
}
