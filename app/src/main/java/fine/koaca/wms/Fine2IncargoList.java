package fine.koaca.wms;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Fine2IncargoList {

    String bl;
    String description;
    String date;
    String count;
    String container;
    String incargo;
    String remark;
    String container40;
    String container20;
    String lclcargo;
    String working;
    String location;
    String consignee;

    public Fine2IncargoList(){

    }

    public Fine2IncargoList(String bl, String description, String date, String count_seal, String container, String incargo,
                            String remark, String container40, String container20, String lclCargo, String working,
                            String location,String consignee) {
        this.bl = bl;
        this.description = description;
        this.date = date;
        this.count = count_seal;
        this.container = container;
        this.incargo = incargo;
        this.remark = remark;
        this.container40 = container40;
        this.container20 = container20;
        this.lclcargo = lclCargo;
        this.working = working;
        this.location=location;
        this.consignee=consignee;
    }



    public String getIncargo() {
        return incargo;
    }

    public void setIncargo(String incargo) {
        this.incargo = incargo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLclcargo() {
        return lclcargo;
    }

    public void setLclcargo(String lclcargo) {
        this.lclcargo = lclcargo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getBl() {
        return bl;
    }

    public void setBl(String bl) {
        this.bl = bl;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count_seal) {
        this.count = count_seal;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }



    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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



    public String getWorking() {
        return working;
    }

    public void setWorking(String working) {
        this.working = working;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result=new HashMap<>();
        result.put("bl",bl);
        result.put("description",description);
        result.put("location",location);
        result.put("date",date);
        result.put("count",count);
        result.put("remark",remark);
        result.put("container",container);
        result.put("container40",container40);
        result.put("container20",container20);
        result.put("lclcargo",lclcargo);
        result.put("working",working);
        result.put("incargo",incargo);
        result.put("consignee",consignee);
        return result;
    }


}
