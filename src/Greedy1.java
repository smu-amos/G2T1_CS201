import java.util.*;

public class Greedy1 implements GraphAlgo {

    public String executeAlgo(String source, String dest, HashMap<String,Integer> cityToIndex, FlightInfoList graph[][]) {
        HashMap<Integer, Integer> recordMap = new HashMap<Integer, Integer>(); // store the id of all the flightinfo with pitstops 
        HashMap<String, Integer> citiesVisited = new HashMap<>();
        int sourceIndex = cityToIndex.get(source);
        int destIndex = cityToIndex.get(dest);
        FlightInfoList flightPath[] = graph[sourceIndex];
        int totalCost = 0;
        int maxTraversals = 20;
        LinkedList<String> pathTaken = new LinkedList<>();
        pathTaken.add(source);
        citiesVisited.put(source, 1);

        //shift city
        while (sourceIndex != destIndex && maxTraversals > 0) {
            FlightInfo cheapestPath = null;     
            //System.out.println(flightPath);
            boolean isCostIncurred = false;
            //for each row
            // find the cheapest next airport to travel to from 
            for (int i = 0; i < flightPath.length; i++) {
                if (flightPath[i] == null) continue;
                ArrayList<FlightInfo> flightTickets = flightPath[i].getFlightInfoList();
                //to get cheapestPath
                for (FlightInfo flightInfo : flightTickets) {
                    if (citiesVisited.containsKey(flightInfo.getNextCity())) continue;
                    if (cheapestPath == null) {
                        cheapestPath = flightInfo;
                    }
                    else{ 
                        //if already traversed
                        if (recordMap.containsKey(flightInfo.getId())) {
                            // System.out.println(flightInfo.getNextCity());
                            flightInfo.setTraversed();
                            cheapestPath = flightInfo;
                            isCostIncurred = true;
                            break;
                        }
                        else if (!flightInfo.getTraversed() && flightInfo.getPrice() < cheapestPath.getPrice()) {
                            //System.out.println(flightInfo.getNextCity() + " " + flightInfo.getPrice());
                            cheapestPath = flightInfo;
                        }
                    }
                } 
                
                if (isCostIncurred) break;
            }

            if (cheapestPath == null) {
                
                return "Dead End " + maxTraversals;
            }

            //System.out.println(cheapestPath);
            if (!recordMap.containsKey(cheapestPath.getId())){
                totalCost += cheapestPath.getPrice();
            }
            // for (String city : cityToIndex.keySet()) {
            //     if (844 == cityToIndex.get(city)) System.out.println(city);
            // }
            cheapestPath.setTraversed();
            
            if (cheapestPath.getIsOrigin()) {
                recordMap.put(cheapestPath.getId(), 1);
            }
            
            //System.out.println(cheapestPath.getNextCity());
            String nextCity = cheapestPath.getNextCity();
            citiesVisited.put(nextCity, 1);
            System.out.println(nextCity);
            //System.out.println(sourceIndex + "before");
            sourceIndex = cityToIndex.get(nextCity);
            //System.out.println(sourceIndex + "after");
            flightPath = graph[sourceIndex];
            //System.out.println(cityToIndex.get("DFW"));

            
            pathTaken.add(nextCity);
            maxTraversals--;
        }
        String path = "";
        while (pathTaken.size() > 1) {
            path += pathTaken.removeFirst() + "--";
        }

        path += pathTaken.removeFirst() + "\nTotal Cost: " + totalCost;
        return path;
    }
}
