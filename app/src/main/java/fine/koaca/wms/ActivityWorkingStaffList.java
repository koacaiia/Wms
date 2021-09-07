package fine.koaca.wms;

public class ActivityWorkingStaffList {
    String date;
    String fineStaff;
    String fineWomenStaff;
    String outsourcingMale;
    String outsourcingFemale;
    String outsourcingValue;

    public String getOutsourcingValue() {
        return outsourcingValue;
    }

    public void setOutsourcingValue(String outsourcingValue) {
        this.outsourcingValue = outsourcingValue;
    }

    public ActivityWorkingStaffList(){

    }

    public ActivityWorkingStaffList(String date, String fineStaff, String fineWomenStaff, String outsourcingMale,
                                    String outsourcingFemale,String outsourcingValue) {
        this.date = date;
        this.fineStaff = fineStaff;
        this.fineWomenStaff = fineWomenStaff;
        this.outsourcingMale = outsourcingMale;
        this.outsourcingFemale = outsourcingFemale;
        this.outsourcingValue=outsourcingValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFineStaff() {
        return fineStaff;
    }

    public void setFineStaff(String fineStaff) {
        this.fineStaff = fineStaff;
    }

    public String getFineWomenStaff() {
        return fineWomenStaff;
    }

    public void setFineWomenStaff(String fineWomenStaff) {
        this.fineWomenStaff = fineWomenStaff;
    }

    public String getOutsourcingMale() {
        return outsourcingMale;
    }

    public void setOutsourcingMale(String outsourcingMale) {
        this.outsourcingMale = outsourcingMale;
    }

    public String getOutsourcingFemale() {
        return outsourcingFemale;
    }

    public void setOutsourcingFemale(String outsourcingFemale) {
        this.outsourcingFemale = outsourcingFemale;
    }
}
