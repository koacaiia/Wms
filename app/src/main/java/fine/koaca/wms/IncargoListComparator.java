package fine.koaca.wms;

import android.util.Log;

import java.util.Comparator;

public class IncargoListComparator implements Comparator<Fine2IncargoList> {
    String sort;

    public IncargoListComparator(String sort) {
        this.sort=sort;
    }

    @Override
    public int compare(Fine2IncargoList a, Fine2IncargoList b) {

        int compare=0;
        switch(sort){
            case "date":
                compare=a.date.compareTo(b.date);
                break;
            case "bl":
                compare=a.bl.compareTo(b.bl);
                break;
            case "description":
                compare=a.description.compareTo(b.description);
                break;
            case "location":
                compare=a.location.compareTo(b.location);
                break;
            case "consignee":
                compare=a.consignee.compareTo(b.consignee);
        }

        return compare;


    }
}
