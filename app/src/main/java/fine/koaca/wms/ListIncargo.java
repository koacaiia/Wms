package fine.koaca.wms;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ListIncargo {
    String working;
    String date;
    String consignee;
    String container;
    String incargo;
    String container40;
    String container20;
    String lclcargo;
    String remark;

    public ListIncargo(String date){
        this.date=date;
    }
    public ListIncargo(){

    }

    public ListIncargo(String working, String date, String consignee, String container, String container40, String container20,
                       String lclcargo, String remark,String incargo) {

        this.working = working;
        this.date = date;
        this.consignee = consignee;
        this.container = container;
        this.container40 = container40;
        this.container20 = container20;
        this.lclcargo = lclcargo;
        this.remark = remark;
        this.incargo=incargo;
    }

    public String getIncargo() {
        return incargo;
    }

    public void setIncargo(String incargo) {
        this.incargo = incargo;
    }

    public String getWorking() {
        return working;
    }

    public void setWorking(String working) {
        this.working = working;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result=new HashMap<>();
        result.put("working",working);
        result.put("date",date);
        result.put("consignee",consignee);
        result.put("container",container);
        result.put("container40",container40);
        result.put("container20",container20);
        result.put("lclcargo",lclcargo);
        result.put("remark",remark);
        result.put("incargo",incargo);
        return result;
    }
}
