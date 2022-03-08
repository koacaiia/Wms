package fine.koaca.wms;

public class ListOutSourcingValue {
    String date;
    String name;
    String gender;
    double count;
    public ListOutSourcingValue(){

    }
    public ListOutSourcingValue(String date, String name, String gender, double count) {
        this.date = date;
        this.name = name;
        this.gender = gender;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }
}
