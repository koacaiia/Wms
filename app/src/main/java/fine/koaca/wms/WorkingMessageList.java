package fine.koaca.wms;

public class WorkingMessageList {
    String msg;
    String Uri;
    String nickName;
    String time;
    String date;
    String consignee;
    String InOutCargo;

    public String getInOutCargo() {
        return InOutCargo;
    }

    public void setInOutCargo(String inOutCargo) {
        InOutCargo = inOutCargo;
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

    public String getMsg(){
        return msg;
    }
    public void setMsg(String msg){
        this.msg=msg;
    }
    public String getNickName(){
        return nickName;
    }
    public void setNickName(String nickName){
        this.nickName=nickName;
    }
    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time=time;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }
}

