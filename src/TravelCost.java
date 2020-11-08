/**
 * TravelCost looks at accumulated travel cost to reach a particular city (node) from the source city
 * This object can be used by both the BFS and Dijkstra algorithm to decide which is the cheapest city x to reach from source and calculate x's flightinfo paths
 */
public class TravelCost {
    private String city;
    private int cost;
    
    public TravelCost(String city, int cost) {
      this.city = city;
      this.cost = cost;
    }
    
    /** Returns the accumulated computed cost by dijkstra to reach city from source city specified by user */
    public int getCost() { return this.cost; }

    public String getCity() { return this.city; }
}
