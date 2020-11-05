import java.util.*;

public interface GraphAlgo {

    public Map<Integer, Integer> recordMap = new HashMap<Integer, Integer>();
    public String executeAlgo(String source, String dest, Map<String,Integer> cityToIndex, Map<Integer, List<String>> flightIDToListOfStops, FlightInfoList graph[][]);
    /*
    {
        Row_id: 1,
    }
    (Doubly) Linked List to remember which path taken
    A -> B -> C
    */


}
