

/*
An implied condition to apply the Dijkstra's algorithm is that the weights of the graph must be positive. If the graph has negative weights and can have negative weighted cycles, we would have to employ another algorithm called the Bellman Ford's. The point here is that the properties of the graph and the goal define the kind of algorithms we might be able to use.

this problem becomes a standard shortest paths problem in a weighted graph with positive weights and hence, it becomes a prime candidate for Dijkstra's. As we all know, Dijkstra's uses a min-heap (priority queue) as the main data structure for always picking out the node which can be reached in the shortest amount of time/cost/weight from the current point starting all the way from the source. 

FlightInfo is our "edge", for each vertex, it has more than 1 edges to another vertex ( pitstop, direct flight etc ) ( represented via list of flightinfo )
The index of the adjacency matrix represents our vertex

What we have here is a multi-graph


Terms:

1. Multi Graph: Any graph which contain some parallel edges but doesn’t contain any self-loop is called multi graph. For example A Road Map.
https://www.geeksforgeeks.org/graph-types-and-applications/

2. The relaxation process in Dijkstra's algorithm refers to updating the cost of all vertices connected to a vertex v, if those costs would be improved by including the path via v.
Relaxing an edge, (a concept you can find in other shortest-path algorithms as well) is trying to lower the cost of getting to a vertex by using another vertex.


Complexity of our problem ( solving it means modification to the normal djikstra that we were taught on ):

- How to deal with multi-graph is not taught in class ( but i think dijkstra using pq will still work fine )
- Compressing this multi-graph to a normal graph : For every multi-edge, keep the one with lowest weight. 2: Apply Dijkstra algorithm. 
    – But this does not work for us that easily since for the pitstop "edges", they have a relationship/they are linked. So, it is not clear-cut by looking at cost directly from one node to another. For pitstops, while it can be more expensive, it covers multiple destinations ( and as a result, might cover further distances too )

*/

// @TODO: Things that may make algorithm more complicated: Including round-trip constraints
// Limitations: Time of flight not considered (may make it not usable), round-trip is not considered as well ( this means not for travellers that plan to return by a time/date )
// Limitations: lack of data. But we did to the best of our abilities. We believe that inter-region flights etc / extend to larger number of airports -> this algorithm must be even more useful

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
     * TravelCost looks at accumulated travel cost to reach a particular city (node) from the source city
     * This object is retrieved from the priority queue by the dijkstra algorithm to decide which is the cheapest city x to reach from source and calculate x's flightinfo paths
     */
    private class TravelCost {
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

    /**
     * An implementation made to trace the optimal path calculated by dijkstra ( so this is not directly related to the algorithm in computing shortest path )
     *
     * Useful for tracking count of flightInfo traversed with the same flightID. By having this count, we can know how many pitstops user has traversed in a particular flight ticket route
     */
    private class FlightTaken {
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
    
    }

     /**
     * Returns a string representation of the instructions we give to our user on what are the flight paths to take. 
     * This is inclusive of direct flights, pitstop flights as well as taking skipped flights
     * 
     * This function will not only contains dijkstra logic but also logic to create instructions for user on how to interpret the shortest path
     */
    public String executeAlgo(String source, String dest, Map<String,Integer> cityToIndex, Map<Integer, List<String>> flightIDToListOfStops, FlightInfoList graph[][]) {

        // @TODO: check whether source is a valid airport string

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
        
        /** get the source city's idx representation in the adjacency matrix (graph) */
        int sourceCityIdx = cityToIndex.get(source);
        /** get the destination city's idx representation in the adjacency matrix (graph) */
        int destCityIdx = cityToIndex.get(dest);

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

        // count the number of unique flight ids and their freq in the optimal path
        Stack<FlightTaken> flightsTakenToReachDestination = new Stack<>();
        FlightInfo incomingFlight = flightTakenToReachCity.get(dest);
        if (incomingFlight == null) {
            // means no incoming flight that arrives to destination
            return String.format("Flight from %s to %s is not supported", source, dest);
        }

        // trace city path backwards from destination city
        String incomingFlightOriginCity = "";

        while (incomingFlight != null) {

            if (!flightsTakenToReachDestination.empty() &&
                flightsTakenToReachDestination.peek().getFlightID() == incomingFlight.getId()
            ) {
                FlightTaken flightAhead = flightsTakenToReachDestination.peek();
                flightAhead.setCount(flightAhead.getCount()+1);
            } else {
                flightsTakenToReachDestination.push(new FlightTaken(incomingFlight.getId(), 1));
            }
            
            incomingFlightOriginCity = incomingFlight.getCurrCity();
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

        // use the stack to get the list of stops
        while (!flightsTakenToReachDestination.isEmpty()) {
            // map flightID to list of stops that exist in the flightID
            // @TODO give better naming
            FlightTaken fo = flightsTakenToReachDestination.pop();
            List<String> citiesReachable = flightIDToListOfStops.get(fo.getFlightID());
            int numberOfCitiesReachable = citiesReachable.size();
            int numberOfCitiesTravelled = fo.getCount();

            if (numberOfCitiesReachable == 1) {
                // direct flight
                String instruction = String.format("%d) Take direct flight to %s.\n", step, citiesReachable.get(numberOfCitiesReachable-1));
                optimalPathSB.append(instruction);
            } else if (numberOfCitiesTravelled < numberOfCitiesReachable) {
                String purchase = String.format("Purchase flight with pitstops (%s)", citiesToTravel(citiesReachable, numberOfCitiesReachable));
                String take = String.format("Take flight to %s (skip flight)", citiesToTravel(citiesReachable, numberOfCitiesTravelled));
                String finalString = String.format("%d) %s\n%s\n", step, purchase, take);
                optimalPathSB.append(finalString);
            } else {
                // take full pitstop flights
                String finalString = String.format("%d) Purchase and take flight with pitstops %s\n", step, citiesToTravel(citiesReachable, numberOfCitiesReachable));
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
