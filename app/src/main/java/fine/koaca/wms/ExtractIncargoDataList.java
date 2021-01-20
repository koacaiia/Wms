package fine.koaca.wms;

public class ExtractIncargoDataList {
    String consignee;
    String container40;
    String container20;
    String lcLCargo;
    String qty;

    public ExtractIncargoDataList(String consignee, String container40, String container20, String lcLCargo, String qty) {
        this.consignee = consignee;
        this.container40 = container40;
        this.container20 = container20;
        this.lcLCargo = lcLCargo;
        this.qty = qty;
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

    public String getLcLCargo() {
        return lcLCargo;
    }

    public void setLcLCargo(String lcLCargo) {
        this.lcLCargo = lcLCargo;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
