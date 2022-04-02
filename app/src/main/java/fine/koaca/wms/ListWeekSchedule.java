package fine.koaca.wms;

public class ListWeekSchedule {
   String consignee;
    String container40;
    String container20;
    String lclcargo;
    public ListWeekSchedule(String consignee, String container40, String container20, String lclcargo) {
        this.consignee = consignee;
        this.container40 = container40;
        this.container20 = container20;
        this.lclcargo = lclcargo;
    }
public ListWeekSchedule(){

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
}
