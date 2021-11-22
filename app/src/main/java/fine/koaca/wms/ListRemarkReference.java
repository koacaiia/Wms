package fine.koaca.wms;

public class ListRemarkReference {
    String outCargoNickName;
    String inCargoNickName;
    String outCargoRemarkValue;
    String inCargoRemarkValue;
    String inCargoDesValue;
    String outCargoDesValue;
    public ListRemarkReference(){

    }

    public ListRemarkReference(String outCargoNickName, String inCargoNickName, String outCargoRemarkValue, String inCargoRemarkValue, String inCargoDesValue, String outCargoDesValue) {
        this.outCargoNickName = outCargoNickName;
        this.inCargoNickName = inCargoNickName;
        this.outCargoRemarkValue = outCargoRemarkValue;
        this.inCargoRemarkValue = inCargoRemarkValue;
        this.inCargoDesValue = inCargoDesValue;
        this.outCargoDesValue = outCargoDesValue;
    }

    public String getOutCargoNickName() {
        return outCargoNickName;
    }

    public void setOutCargoNickName(String outCargoNickName) {
        this.outCargoNickName = outCargoNickName;
    }

    public String getInCargoNickName() {
        return inCargoNickName;
    }

    public void setInCargoNickName(String inCargoNickName) {
        this.inCargoNickName = inCargoNickName;
    }

    public String getOutCargoRemarkValue() {
        return outCargoRemarkValue;
    }

    public void setOutCargoRemarkValue(String outCargoRemarkValue) {
        this.outCargoRemarkValue = outCargoRemarkValue;
    }

    public String getInCargoRemarkValue() {
        return inCargoRemarkValue;
    }

    public void setInCargoRemarkValue(String inCargoRemarkValue) {
        this.inCargoRemarkValue = inCargoRemarkValue;
    }

    public String getInCargoDesValue() {
        return inCargoDesValue;
    }

    public void setInCargoDesValue(String inCargoDesValue) {
        this.inCargoDesValue = inCargoDesValue;
    }

    public String getOutCargoDesValue() {
        return outCargoDesValue;
    }

    public void setOutCargoDesValue(String outCargoDesValue) {
        this.outCargoDesValue = outCargoDesValue;
    }
}
