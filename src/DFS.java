import java.util.*;

/**
 * Experimentation but not used for our final analysis!
 * Instead our final algo to use is BFS and Dijkstra
 */
public class DFS implements GraphAlgo{

    public String executeAlgo(String source, String dest, Map<String,Integer> cityToIndex, Map<Integer, List<String>> flightIDToListOfStops, FlightInfoList[][] adjList){
        HashMap<Integer, Integer> recordMap = new HashMap<Integer, Integer>();
        HashMap<String, Integer> citiesVisited = new HashMap<>();
        int sourceIndex = cityToIndex.get(source);
        int destIndex = cityToIndex.get(dest);
        FlightInfoList[] flightPath = adjList[sourceIndex];
        int totalCost = 0;

        ArrayList<FlightInfo> isOriginNodes = new ArrayList<>(); // get all the flights where source is the origin

        for (FlightInfoList flightInfoList: flightPath) {
            if (flightInfoList == null) continue;
            // System.out.println(flightInfoList.toString());
            ArrayList<FlightInfo> flightTickets = flightInfoList.getFlightInfoList();
            for (FlightInfo flightInfo: flightTickets) {
                if (flightInfo.getIsOrigin()) {
                    isOriginNodes.add(flightInfo);
                }
            }
        }
        Stack<FlightInfo> stack = new Stack<>();
        stack.push(isOriginNodes.get(0));
        FlightInfo flightInfo = null;
        String x = "";
        int index = 0;
        while (!stack.empty()) {
            flightInfo = stack.pop();
            if (flightInfo.getIsOrigin()) recordMap.put(flightInfo.getId(), 1);
            if (flightInfo.getTraversed() == false) {
                flightInfo.setTraversed();
                index++;
            }

            for (FlightInfo node: isOriginNodes) {
                stack.push(node); //push all next level nodes from origin into the stack
            }

            sourceIndex = cityToIndex.get(flightInfo.getNextCity());
            flightPath = adjList[sourceIndex];
            isOriginNodes.clear();
            for (FlightInfoList flightInfoList: flightPath) {
                if (flightInfoList == null) continue;
                ArrayList<FlightInfo> flightTickets = flightInfoList.getFlightInfoList();
                for (FlightInfo info: flightTickets) {
                    if (!info.getTraversed() && recordMap.containsKey(info.getId())) {
                        stack.push(info); // repopulate with the
                    }
                }
            }
            // System.out.println(flightInfo.toString());
            x = flightInfo.getNextCity();
            // System.out.println(x + flightInfo.getId());
            if (x.equals(dest)){
                break;
            }
        }
        if (x.equals(dest)) {
            System.out.println("HELLO");
            System.out.println(x + " " + index + " " + flightInfo.toString());
            System.out.println(recordMap.keySet());
        }

        return "";
    }

}
