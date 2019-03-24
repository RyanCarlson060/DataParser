public class City {
    private String name;
    private int numNonprofits;
    private String state;

    public City(String name, String state) {
        this.name = name;
        this.numNonprofits = 1;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumNonprofits() {
        return numNonprofits;
    }

    public void setNumNonprofits(int numNonprofits) {
        this.numNonprofits = numNonprofits;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void incrementNonprofits() {
        numNonprofits++;
    }
}
