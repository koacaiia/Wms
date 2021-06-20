package fine.koaca.wms;

import android.util.Log;

public class ConsigneeList {
    String[] consigneeList_list;


    public ConsigneeList(String[] consigneeList_list) {
        this.consigneeList_list = consigneeList_list;
    }

    public ConsigneeList() {

    }

    public String[] getConsigneeList_list() {
        Log.i("TestValue","Call back confirm");
        return consigneeList_list;
    }

    public void setConsigneeList_list(String[] consigneeList_list) {
        this.consigneeList_list = consigneeList_list;
    }
}
