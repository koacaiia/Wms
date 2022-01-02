package fine.koaca.wms;

public class ActivityPalletList {
    String date;
    String keyValue;
    String nickName;
    String bl;
    String des;
    String refPath;
    String tDate;
    int inQty;
    int outQty;
    int stockQty;

    public ActivityPalletList(){

    }

    public ActivityPalletList(String date, String keyValue, String nickName, String bl, String des, String refPath, String tDate, int inQty, int outQty, int stockQty) {
        this.date = date;
        this.keyValue = keyValue;
        this.nickName = nickName;
        this.bl = bl;
        this.des = des;
        this.refPath = refPath;
        this.tDate = tDate;
        this.inQty = inQty;
        this.outQty = outQty;
        this.stockQty = stockQty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBl() {
        return bl;
    }

    public void setBl(String bl) {
        this.bl = bl;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getRefPath() {
        return refPath;
    }

    public void setRefPath(String refPath) {
        this.refPath = refPath;
    }

    public String gettDate() {
        return tDate;
    }

    public void settDate(String tDate) {
        this.tDate = tDate;
    }

    public int getInQty() {
        return inQty;
    }

    public void setInQty(int inQty) {
        this.inQty = inQty;
    }

    public int getOutQty() {
        return outQty;
    }

    public void setOutQty(int outQty) {
        this.outQty = outQty;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }
}
