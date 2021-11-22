package fine.koaca.wms;

public class ListRemarkRecyclerView {
    String nickName;
    String des;
    String remark;

    public ListRemarkRecyclerView(String nickName, String des, String remark) {
        this.nickName = nickName;
        this.des = des;
        this.remark = remark;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
