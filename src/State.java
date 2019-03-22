import java.util.ArrayList;

public class State {
    private String name;
    private ArrayList<County> counties;
    private ArrayList<NonProfit> nonProfits;

    public ArrayList<NonProfit> getNonProfits() {
        return nonProfits;
    }

    public void setNonProfits(ArrayList<NonProfit> nonProfits) {
        this.nonProfits = nonProfits;
    }



    public State(String name, ArrayList<County> counties, ArrayList<NonProfit> nonProfits) {
        this.name = name;
        this.counties = counties;
        this.nonProfits=nonProfits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<County> getCounties() {
        return counties;
    }

    public void setCounties(ArrayList<County> counties) {
        this.counties = counties;
    }
}
