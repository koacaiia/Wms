package fine.koaca.wms;

public class AnnualList {
    String name;
    String annual;
    String half;
    Double totaldate;
    String deptName;

    public AnnualList(){

    }

    public AnnualList(String name, String annual, String half, Double totaldate,String deptName) {
        this.name = name;
        this.annual = annual;
        this.half = half;
        this.totaldate = totaldate;
        this.deptName = deptName;
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

    public String getHalf() {
        return half;
    }

    public void setHalf(String half) {
        this.half = half;
    }

    public Double getTotaldate() {
        return totaldate;
    }

    public void setTotaldate(Double totaldate) {
        this.totaldate = totaldate;
    }



    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
