import java.util.*;

/**
 * An implementation of Dijkstra ( another greedy approach )
 *
 * Treats the vertex as the FlightInfo and cityname/idx as the node.
 * Uses priority queue as the main tool to decide which flightInfo (vertex) to pick at each node
 *
 */
public class Dijkstra implements GraphAlgo {

     /**
     * Returns a string representation of the instructions we give to our user on what are the flight paths to take. 
     * This is inclusive of direct flights, pitstop flights as well as taking skipped flights
     * 
     * This function will not only contains dijkstra logic but also logic to create instructions for user on how to interpret the shortest path
     */
    public String executeAlgo(String source, String dest, Map<String,Integer> cityToIndex, Map<Integer, List<String>> flightIDToListOfStops, FlightInfoList graph[][]) {

        /** Check if either source or dest is not supported in the data */
        if (!cityToIndex.containsKey(source) || !cityToIndex.containsKey(dest))  {
            return String.format("Flight from %s to %s is not supported", source, dest);
        }

        /** get the source city's idx representation in the adjacency matrix (graph) */
        int sourceCityIdx = cityToIndex.get(source);
        /** get the destination city's idx representation in the adjacency matrix (graph) */
        int destCityIdx = cityToIndex.get(dest);
        

        /**
         * flightTakenToReachCity is our probe hashmap which tracks the incoming flight chosen by dijkstra to reach a particular city
         * This implementation will be useful for backtracking the shortest path computed to output the cities within the path for users to see
         * The key value acts like the vertex
         * The flightinfo acts like the edge
         */
        Map<String, FlightInfo> flightTakenToReachCity = new HashMap<>();
        flightTakenToReachCity.put(source, null);

        /**
         * calculatedTravels helps dijkstra to decide its next step of selecting which flight path to compute the cost
         * The priority queue will be useful until dijkstra finds the destination city. The TravelCost from the pq will then be the final "cheapest" cost
         */
        PriorityQueue<TravelCost> calculatedTravels = new PriorityQueue<TravelCost>((a, b) -> a.getCost() - b.getCost());

        /** enqueue the first get the total number of unique cities we have in our dataset */
        calculatedTravels.offer(new TravelCost(source, 0));

        /** get the total number of unique cities we have in our dataset */
        int numberOfCitiesSupported = cityToIndex.size();

        /**
         * minCostFromSrcToCity is an important implementation used by dijkstra to keep track of the cost computed to reach a city X from source city. 
         * by knowing the computed cost, when dijkstra can know if it has calculated an even cheaper cost path to reach city X, and as a result overwrite the cost in this array
         */
        int[] minCostFromSrcToCity = new int[numberOfCitiesSupported];

        /** Fill up the cost to reach each city from src to be infinite. They will be updated by the dijkstra later */
        Arrays.fill(minCostFromSrcToCity, Integer.MAX_VALUE);

        /** Cost for source to reach itself = 0 */
        minCostFromSrcToCity[sourceCityIdx] = 0;

        /** carry out dijkstra */
        while (!calculatedTravels.isEmpty()) {

            /** get the cheapest travel computed from source so far */
            TravelCost currentCheapestCityToReachFromSource = calculatedTravels.poll();
            /** get the city for that cheapest travel */
            String cheapestCityToReach = currentCheapestCityToReachFromSource.getCity();
            /** get the cost of that travel */
            int flightCostToCurrentCity = currentCheapestCityToReachFromSource.getCost();

            /** if cheapest city to reach is destination. we have found the shortest path */
            if (cheapestCityToReach.equals(dest)) {
                break;
            }
            
            /** if cheapestCityToReach != destination, process the out-going flights from cheapestCityToReach using the adjacency matrix */
            int cityIndex = cityToIndex.get(cheapestCityToReach); 
            FlightInfoList outgoingFlights[] = graph[cityIndex];

            boolean isFlightCostToCurrentCityTheCheapest = minCostFromSrcToCity[cityIndex] >= flightCostToCurrentCity;
            if (!isFlightCostToCurrentCityTheCheapest) {
                continue;
            }

            /** get the previous flight taken to reach this cheapest city  */ 
            
            FlightInfo previousFlightInfo = flightTakenToReachCity.get(cheapestCityToReach);
            
            for (int towardsCityIdx = 0; towardsCityIdx < outgoingFlights.length; towardsCityIdx++) {
                
                if (outgoingFlights[towardsCityIdx] == null) {
                    continue;
                }

                /** the list will store all the possible flights from cheapest city to another city such as pitstop flights/ direct flights */
                ArrayList<FlightInfo> flightsFromCheapestCityTowardsAnotherCity = outgoingFlights[towardsCityIdx].getFlightInfoList();
                
                for (int j=0; j < flightsFromCheapestCityTowardsAnotherCity.size(); j++) {
                    FlightInfo fi = flightsFromCheapestCityTowardsAnotherCity.get(j);

                    /**
                     * The conditional code checks the eligiblity of the flight that can be taken from cheapest city to another city
                     * Accounts for follow conditions:
                     * 1. if flight is the very first flight, it cannot pick any flights that belong to the second/third pitstops
                     * 2. if flight is not the first flight, it cannot pick any flights that belong to second/third pitstops that is not a continuation of the previous flight taken
                     */ 
                    if ( 
                        (previousFlightInfo == null && fi.getIsOrigin() == false) ||
                        (previousFlightInfo != null && fi.getIsOrigin() == false && previousFlightInfo.getId() != fi.getId())
                    ) {
                        continue;
                    }

                    String nextFlightDestination = fi.getNextCity();
                    // get flight info destination city idx in adjacency matrix
                    int nextCityIdx = cityToIndex.get(nextFlightDestination);
                    // get accumulatedCost to that destination city
                    int accumulatedCostToNextCity = minCostFromSrcToCity[nextCityIdx];

                    /** get cost to travel from current city to next city and compare with the current accumulatedCost to the next city from source city. */
                    int costToNextCity = flightCostToCurrentCity + fi.getPrice();
                    if (costToNextCity < accumulatedCostToNextCity) {
                        /** found another path that is cheaper to next city */
                        minCostFromSrcToCity[nextCityIdx] = costToNextCity;
                        
                        calculatedTravels.offer(new TravelCost(nextFlightDestination, costToNextCity));
                        
                        flightTakenToReachCity.put(nextFlightDestination, fi);
                    }

                }
            }
            
        }

        /** Below code concerns processing the output of dijkstra to return user instructions for how to buy their air tickets */

        Stack<FlightTaken> flightsTakenToReachDestination = new Stack<>();
        FlightInfo incomingFlight = flightTakenToReachCity.get(dest);
        if (incomingFlight == null) {
            // means no incoming flight that arrives to destination
            return String.format("Flight from %s to %s is not supported", source, dest);
        }

        String incomingFlightOriginCity = "";

        /**  count the number of unique flight ids and their freq in the optimal path.
         * This aids in tracing which stops to stop in for pitstop flights later
         */
        while (incomingFlight != null) {

            incomingFlightOriginCity = incomingFlight.getCurrCity();

            /**  check if incomingFlight is a non-first leg pitstop flight. if it is, path = from first city of pitstop flight to this incoming flight */
            if (incomingFlight.getIsOrigin() == false) {
                
                List<String> pitstopsOnFlight = flightIDToListOfStops.get(incomingFlight.getId());
                String nextCity = incomingFlight.getNextCity();
                int idxOfCity = pitstopsOnFlight.indexOf(nextCity);

                flightsTakenToReachDestination.push(new FlightTaken(incomingFlight.getId(), idxOfCity));
                incomingFlightOriginCity = pitstopsOnFlight.get(0);
            } else {
                flightsTakenToReachDestination.push(new FlightTaken(incomingFlight.getId(), 1));
            }
            
            incomingFlight = flightTakenToReachCity.get(incomingFlightOriginCity);
        }

        // last source city should be the actual source
        if (!incomingFlightOriginCity.equals(source)) {
            return String.format("Flight from %s to %s is not supported", source, dest);
        }

        return stringifyOptimalPath(flightIDToListOfStops, flightsTakenToReachDestination, source, minCostFromSrcToCity[destCityIdx]);
    }

    /**
     * This function maps flights id to its full flight path ( for pitstops that includes all the cities in between )
     * With that information, instructions are printed out for users on how to proceed with the shortest path
     */
    public String stringifyOptimalPath(Map<Integer, List<String>> flightIDToListOfStops, Stack<FlightTaken> flightsTakenToReachDestination, String source, int costOfPath) {
        StringBuilder optimalPathSB = new StringBuilder(String.format("Starting airport: %s.\n", source));
        int step = 1;

        /* use the stack to trace the optimal path backwards  **/
        while (!flightsTakenToReachDestination.isEmpty()) {
            
            FlightTaken ft = flightsTakenToReachDestination.pop();
            List<String> citiesInvolvedInFlight = flightIDToListOfStops.get(ft.getFlightID());
            int numberOfCitiesReachableFromFlightSource = citiesInvolvedInFlight.size() - 1;
            int numberOfCitiesTravelledFromFlightSource = ft.getCount();

            if (numberOfCitiesReachableFromFlightSource == 1) {
                // direct flight
                String instruction = String.format("%d) Take direct flight to %s.\n", step, citiesInvolvedInFlight.get(numberOfCitiesReachableFromFlightSource));
                optimalPathSB.append(instruction);
            } else if (numberOfCitiesTravelledFromFlightSource < numberOfCitiesReachableFromFlightSource) {
                String purchase = String.format("Purchase flight with pitstops (%s)", citiesToTravel(citiesInvolvedInFlight, numberOfCitiesReachableFromFlightSource + 1));
                String take = String.format("Take flight to %s (skip flight)", citiesToTravel(citiesInvolvedInFlight, numberOfCitiesTravelledFromFlightSource + 1));
                String finalString = String.format("%d) %s\n%s\n", step, purchase, take);
                optimalPathSB.append(finalString);
            } else {
                // take full pitstop flights
                String finalString = String.format("%d) Purchase and take flight with pitstops %s\n", step, citiesToTravel(citiesInvolvedInFlight, numberOfCitiesReachableFromFlightSource + 1));
                optimalPathSB.append(finalString);
            }
            
            step++;
        }

        optimalPathSB.append(String.format("Total cost that will be incurred: $%d\n", costOfPath));

        return optimalPathSB.toString();

    }

    /**
     * Helper function to string list of cities together in this format "X-Y-Z"
     */
    public String citiesToTravel(List<String> cities, int count) {
        StringBuilder path = new StringBuilder();
        path.append(cities.get(0));

        for (int i=1; i<count; i++) {
            path.append("-");
            path.append(cities.get(i));
        }

        return path.toString();
    }

}
