package fine.koaca.wms;

public class ActivityWorkingStaffList {
    String date;
    double fineStaff;
    double fineWomenStaff;
    public ActivityWorkingStaffList(){

    }
    public ActivityWorkingStaffList(String date, double fineStaff, double fineWomenStaff) {
        this.date = date;
        this.fineStaff = fineStaff;
        this.fineWomenStaff = fineWomenStaff;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getFineStaff() {
        return fineStaff;
    }

    public void setFineStaff(double fineStaff) {
        this.fineStaff = fineStaff;
    }

    public double getFineWomenStaff() {
        return fineWomenStaff;
    }

    public void setFineWomenStaff(double fineWomenStaff) {
        this.fineWomenStaff = fineWomenStaff;
    }
}
