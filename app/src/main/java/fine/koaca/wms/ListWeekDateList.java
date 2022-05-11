package fine.koaca.wms;

public class ListWeekDateList {
    String date;
    ListWeekSchedule list;

    public ListWeekDateList(String date, ListWeekSchedule list) {
        this.date = date;
        this.list = list;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ListWeekSchedule getList() {
        return list;
    }

    public void setList(ListWeekSchedule list) {
        this.list = list;
    }
}
