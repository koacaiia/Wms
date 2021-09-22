package fine.koaca.wms;

public class WorkingMessageList {
    String msg;
    String uri0;
    String uri1;
    String uri2;
    String uri3;
    String uri4;
    String uri5;
    String uri6;
    String nickName;
    String time;
    String date;
    String consignee;
    String InOutCargo;
    String keyValue;

    public String getUri5() {
        return uri5;
    }

    public void setUri5(String uri5) {
        this.uri5 = uri5;
    }

    public String getUri6() {
        return uri6;
    }

    public void setUri6(String uri6) {
        this.uri6 = uri6;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public WorkingMessageList(String msg, String uri0, String uri1, String uri2, String uri3, String uri4, String uri5,
                              String uri6, String nickName, String time, String date, String consignee, String inOutCargo,
                              String keyValue) {
        this.msg = msg;
        this.uri0 = uri0;
        this.uri1 = uri1;
        this.uri2 = uri2;
        this.uri3 = uri3;
        this.uri4 = uri4;
        this.uri5 = uri5;
        this.uri6 = uri6;
        this.nickName = nickName;
        this.time = time;
        this.date = date;
        this.consignee = consignee;
        this.InOutCargo = inOutCargo;
        this.keyValue=keyValue;
    }

    public WorkingMessageList() {

    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUri0() {
        return uri0;
    }

    public void setUri0(String uri0) {
        this.uri0 = uri0;
    }

    public String getUri1() {
        return uri1;
    }

    public void setUri1(String uri1) {
        this.uri1 = uri1;
    }

    public String getUri2() {
        return uri2;
    }

    public void setUri2(String uri2) {
        this.uri2 = uri2;
    }

    public String getUri3() {
        return uri3;
    }

    public void setUri3(String uri3) {
        this.uri3 = uri3;
    }

    public String getUri4() {
        return uri4;
    }

    public void setUri4(String uri4) {
        this.uri4 = uri4;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getInOutCargo() {
        return InOutCargo;
    }

    public void setInOutCargo(String inOutCargo) {
        InOutCargo = inOutCargo;
    }
}

