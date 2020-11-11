public class FlightInfo { 
    //price of ticket -- store the same no matter how many pit stops
    private int price;

    //store info whether current stop is source. If not, means cannot fly from here
    private boolean isOrigin;
    private String nextCity;
    private String currCity;
    private int id;

    // need a way to store 1st/2nd/3rd pitstop
    public FlightInfo(int price, boolean isOrigin, String nextCity, String currCity, int id) {
        this.price = price;
        this.isOrigin = isOrigin;
        this.nextCity = nextCity;
        this.currCity = currCity;
        this.id = id;
    }

    public int getPrice() {
        return price;
    }
    
    @Override
    public String toString() {
        return "( price:" + price + ", isOrigin:" + isOrigin + ", " +  "current city:" + currCity + ", next city:" + nextCity + ", id=" + id + ")";
        // return ("test");
    }

    public void setIsOrigin(boolean value) {
        isOrigin = value;
    }

    public boolean getIsOrigin() {
        return isOrigin;
    }

    public String getNextCity() {
        return nextCity;
    }

    public String getCurrCity() {
        return currCity;
    }

    public int getId() {
        return id;
    }
}
