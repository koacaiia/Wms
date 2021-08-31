package fine.koaca.wms;

import java.util.Map;

public class ActivityPalletResultList {
    String consigneeName;
    int kppQty;
    int ajQty;
    int etcQty;
    public ActivityPalletResultList(){

    }
    public ActivityPalletResultList(String consigneeName, int kppQty, int ajQty, int etcQty) {
        this.consigneeName = consigneeName;
        this.kppQty = kppQty;
        this.ajQty = ajQty;
        this.etcQty = etcQty;
    }

    public ActivityPalletResultList(Map<String, Object> value) {
        this.consigneeName= (String) value.get("consignee");
        this.kppQty= (int) value.get("kppQty");
        this.ajQty=(int) value.get("ajQty");
        this.etcQty=(int)value.get("etcQty");
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public int getKppQty() {
        return kppQty;
    }

    public void setKppQty(int kppQty) {
        this.kppQty = kppQty;
    }

    public int getAjQty() {
        return ajQty;
    }

    public void setAjQty(int ajQty) {
        this.ajQty = ajQty;
    }

    public int getEtcQty() {
        return etcQty;
    }

    public void setEtcQty(int etcQty) {
        this.etcQty = etcQty;
    }
}
