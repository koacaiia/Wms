package fine.koaca.wms;

public class AnnualList {
    String name;
    String annual;
    String annual2;
    String half1;
    String half2;
    Double totaldate;

    public AnnualList(){

    }
    public AnnualList(String name, String annual,String annual2, String half1, String half2,Double totaldate) {
        this.name = name;
        this.annual = annual;
        this.annual2=annual2;
        this.half1 = half1;
        this.half2 = half2;
        this.totaldate=totaldate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnual() {
        return annual;
    }

    public void setAnnual(String annual) {
        this.annual = annual;
    }

    public String getHalf1() {
        return half1;
    }

    public void setHalf1(String half1) {
        this.half1 = half1;
    }

    public String getHalf2() {
        return half2;
    }

    public void setHalf2(String half2) {
        this.half2 = half2;
    }

    public Double getTotaldate() {
        return totaldate;
    }

    public void setTotaldate(Double totaldate) {
        this.totaldate = totaldate;
    }

    public String getAnnual2() {
        return annual2;
    }

    public void setAnnual2(String annual2) {
        this.annual2 = annual2;
    }
}
