import java.util.*;

public interface GraphAlgo {

    public HashMap<Integer, Integer> recordMap = new HashMap<Integer, Integer>();
    public String executeAlgo(String source, String dest, HashMap<String,Integer> cityToIndex, FlightInfoList graph[][]);
    /*
    {
        Row_id: 1,
    }
    (Doubly) Linked List to remember which path taken
    A -> B -> C
    */


}
