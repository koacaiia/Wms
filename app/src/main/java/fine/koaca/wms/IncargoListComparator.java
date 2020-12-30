package fine.koaca.wms;

import java.util.Comparator;

public class IncargoListComparator implements Comparator<fine.koaca.wms.Fine2IncargoList> {
    String sort;

    public IncargoListComparator(String sort) {
        this.sort=sort;
    }

    @Override
    public int compare(fine.koaca.wms.Fine2IncargoList a, fine.koaca.wms.Fine2IncargoList b) {
//        if(Integer.parseInt(a.date)>Integer.parseInt(b.date)) return 1;
//        if(Integer.parseInt(a.date)<Integer.parseInt(b.date)) return -1;
//        return 0;
        int compare=0;
//        if(sort.equals("date")){
//            compare=a.bl.compareTo(b.bl);
//        }
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
        }

        return compare;

    }
}
