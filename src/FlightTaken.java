/**
 * An implementation made to trace the optimal path calculated by the different shortest path algorithms ( so this is not directly related to the algorithm in computing shortest path )
 *
 * Useful for tracking count of flightInfo traversed with the same flightID. By having this count, we can know how many pitstops user has traversed in a particular flight ticket route
 */
public class FlightTaken {
    private int flightID;
    private int count;
    
    public FlightTaken(int flightID, int count) {
        this.flightID = flightID;
        this.count = count;
    }
    
    /** set count for number of cities visited by user in a particular flight ticket */
    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() { 
        return this.count; 
    }

    public int getFlightID() { 
        return this.flightID; 
    }

    @Override
    public String toString() {
        return String.format("flight ID: %d, Count: %d", this.flightID, this.count);
    }

}
