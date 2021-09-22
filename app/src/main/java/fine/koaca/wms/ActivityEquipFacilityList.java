package fine.koaca.wms;

import android.widget.TextView;

public class ActivityEquipFacilityList {
    String name;
    String content;
    String askDate;
    String estAmountDate;
    int estAmount;
    String confirmDate;
    String repairDate;
    String conAmountDate;
    int conAmount;
    public ActivityEquipFacilityList(){

    }
    public ActivityEquipFacilityList(String name, String content, String askDate, String estAmountDate, int estAmount, String confirmDate, String repairDate, String conAmountDate, int conAmount) {
        this.name = name;
        this.content = content;
        this.askDate = askDate;
        this.estAmountDate = estAmountDate;
        this.estAmount = estAmount;
        this.confirmDate = confirmDate;
        this.repairDate = repairDate;
        this.conAmountDate = conAmountDate;
        this.conAmount = conAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAskDate() {
        return askDate;
    }

    public void setAskDate(String askDate) {
        this.askDate = askDate;
    }

    public String getEstAmountDate() {
        return estAmountDate;
    }

    public void setEstAmountDate(String estAmountDate) {
        this.estAmountDate = estAmountDate;
    }

    public int getEstAmount() {
        return estAmount;
    }

    public void setEstAmount(int estAmount) {
        this.estAmount = estAmount;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(String repairDate) {
        this.repairDate = repairDate;
    }

    public String getConAmountDate() {
        return conAmountDate;
    }

    public void setConAmountDate(String conAmountDate) {
        this.conAmountDate = conAmountDate;
    }

    public int getConAmount() {
        return conAmount;
    }

    public void setConAmount(int conAmount) {
        this.conAmount = conAmount;
    }
}
