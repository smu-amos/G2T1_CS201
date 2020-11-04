/*
An implied condition to apply the Dijkstra's algorithm is that the weights of the graph must be positive. If the graph has negative weights and can have negative weighted cycles, we would have to employ another algorithm called the Bellman Ford's. The point here is that the properties of the graph and the goal define the kind of algorithms we might be able to use.

this problem becomes a standard shortest paths problem in a weighted graph with positive weights and hence, it becomes a prime candidate for Dijkstra's. As we all know, Dijkstra's uses a min-heap (priority queue) as the main data structure for always picking out the node which can be reached in the shortest amount of time/cost/weight from the current point starting all the way from the source. That approach as it is won't work out for this problem.

Difficult things to consider:
- Consider skipped flight, direct flight, pitstop flight.
- identify the path as pitstop/ direct . Write down the paths.
    - more complicated one since we backtracking , using the PQ.
    - what is the standard way to trace the path taken?
- our data structures has no concept of linking/linked list. It is not really a graph. 

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

// @TODO: print out if it is pitstop / direct paths
// @TODO: Things that may make algorithm more complicated: Including round-trip constraints ( think of this later )
// Limitations: Time of flight not considered (may make it not usable), round-trip is not considered as well ( this means not for travellers that plan to return by a time/date )
// Limitations: lack of data. But we did to the best of our abilities. We believe that inter-region flights etc / extend to larger number of airports -> this algorithm must be even more useful

import java.util.*;

public class Dijkstra implements GraphAlgo {

    private class AccumulatedFlightCost {
        private String city;
        private int cost;
        
        public AccumulatedFlightCost(String city, int cost) {
          this.city = city;
          this.cost = cost;
        }
    
        public int getCost() { return this.cost; }

        public String getCity() { return this.city; }
    
    }

    public String executeAlgo(String source, String dest, HashMap<String,Integer> cityToIndex, FlightInfoList graph[][]) {

        // @TODO: check whether source is a valid airport string

        // Create path tracker data structure { string destcityname: flightInfo ( responsible for reaching this cityname ) }
        // in our algorithm contxt, cityname/cityidx is the vertex , flightInfo is like the edge
        // by having a record that maps vertex to edge ( essentially can allow us to trace the path that was travelled )
        // acts like the probehashmap taught in our lecture notes
        Map<String, FlightInfo> mapCityToItsIncomingFlight = new HashMap<>();
        mapCityToItsIncomingFlight.put(source, null);


        PriorityQueue<AccumulatedFlightCost> accumulatedFlightPath = new PriorityQueue<AccumulatedFlightCost>((a, b) -> a.getCost() - b.getCost());
        accumulatedFlightPath.offer(new AccumulatedFlightCost(source, 0));

        // track the accumulated min cost to reach any particular city from src city
        int numberOfCitiesSupported = cityToIndex.size();
        
        int sourceCityIdx = cityToIndex.get(source);
        // int destCityIdx = cityToIndex.get(dest);

        int[] minCostFromSrcToCity = new int[numberOfCitiesSupported];
        // fill up all the cities cost as infinite
        Arrays.fill(minCostFromSrcToCity, Integer.MAX_VALUE);
        // cost from source to source = 0 
        minCostFromSrcToCity[sourceCityIdx] = 0;

        while (!accumulatedFlightPath.isEmpty()) {

            // get the cheapest flight path computed so far
            AccumulatedFlightCost flightCostPath = accumulatedFlightPath.poll();

            // get fightCostPath's current city location
            String currentCity = flightCostPath.getCity();
            int flightCostToCurrentCity = flightCostPath.getCost();

            // check if currentCity has reached destination.
            if (currentCity.equals(dest)) {
                break;
            }

            // if currentCity != destination, process the out-going flights from currentCity
            int cityIndex = cityToIndex.get(currentCity); 
            FlightInfoList outgoingFlights[] = graph[cityIndex];

            // get the previous flight info that led to this city
            FlightInfo previousFlightInfo = mapCityToItsIncomingFlight.get(currentCity);
            
            // look into all of the outgoingFlights from current city to other cities
            for (int towardsCityIdx = 0; towardsCityIdx < outgoingFlights.length; towardsCityIdx++) {
                // fil represents two types of flights from one city airport to another
                // the types can be : pitstop or direct flight

                if (outgoingFlights[towardsCityIdx] == null) {
                    continue;
                }

                // @TODO: give better comments description
                ArrayList<FlightInfo> flightsFromCurrentCityTowardsAnotherCity = outgoingFlights[towardsCityIdx].getFlightInfoList();


                for (int j=0; j < flightsFromCurrentCityTowardsAnotherCity.size(); j++) {
                    FlightInfo fi = flightsFromCurrentCityTowardsAnotherCity.get(j);

                    // check eligiblity of flightinfo to be selected as next path
                    
                    
                    if ( 
                        (previousFlightInfo == null && fi.getIsOrigin() == false) ||
                        (previousFlightInfo != null && fi.getIsOrigin() == false && previousFlightInfo.getId() != fi.getId())
                    ) {
                        
                        /*
                            - can choose direct/very-first-pitstop flights to carry on the travel ( isOrigin = true )
                            - cannot choose second-pitstop onwards flights to carry on the travel 
                                - all flight infos that are isOrigin = false ( considered as 2nd-pitstop onwards )
                        */

                        /*
                            - if previous flight info isOrigin = true
                                - means can pick next flightInfo that is direct flight/first pitstop ( isOrigin = true ) 
                            - if previous flight info isOrigin = false
                        
                                - means can pick next flightinfo that is either direct/first-pitstop ( isOrigin = true )  OR pitstop-second/3rdetc (isOrigin = false & flightid == previous flight id) ( for this, dont include cost )
                        */
                        continue;
                    }

                    // get flight info destination city
                    String flightInfoDestination = fi.getNextCity();

                    // get flight info destination city idx in adjacency matrix
                    int nextCityIdx = cityToIndex.get(flightInfoDestination);
                    // get accumulatedCost to that destination city
                    int minAccumulatedCostToNextCity = minCostFromSrcToCity[nextCityIdx];

                    // get cost from currentcity to that destination city ( flight info cost + accumulateCost in current city)
                    int cost = flightCostToCurrentCity + fi.getPrice();
                    if ( cost < minAccumulatedCostToNextCity) {
                        minCostFromSrcToCity[nextCityIdx] = cost;
                        // add to pq
                        accumulatedFlightPath.offer(new AccumulatedFlightCost(flightInfoDestination, cost));

                        // store the incoming flight info to the next city
                        mapCityToItsIncomingFlight.put(flightInfoDestination, fi);
                    }

                }
            }
            
            
        }

        List<String> optimalPath = generateOptimalPath(mapCityToItsIncomingFlight, dest, source);
        int numberOfCities = optimalPath.size();
        String startingCity = optimalPath.get(numberOfCities-1);
        if (numberOfCities == 0 || !startingCity.equals(source)) {
            return "no valid path could be found";
        }
        
        // if there is a path. generate optimalPathString
        StringBuilder optimalPathSB = new StringBuilder(startingCity);
        
        for(int i=numberOfCities-2; i >= 0; i--) {
            optimalPathSB.append("-");
            optimalPathSB.append(optimalPath.get(i));
        }

        // calculate total incurred cost
        int totalCost = minCostFromSrcToCity[cityToIndex.get(dest)];
        optimalPathSB.append("\nTotal Cost: ");
        optimalPathSB.append(totalCost);

        return optimalPathSB.toString();
    }

    public List<String> generateOptimalPath(Map<String, FlightInfo> mapCityToItsIncomingFlight, String destination, String source) {

        // @TODO: print out the specific steps. How to detect the move is from a skip flight/ how to detect the move is from a direct flight

        // previousFlightInfo to reach city, if city flight info and previous flight info ( same flight id == pitstop )
        // isOrigin == true + isOrigin == false ( pitstop )
        // isOrigin == false + isOrigin == false ( pitstop )
        // both == true ( direct )

        // be able to identify skipped flight strategy also ( how to know  )

        List<String> citiesInOptimalPath = new ArrayList<>();

        // map from dest
        FlightInfo flightToCity = mapCityToItsIncomingFlight.get(destination);
        if (flightToCity == null) {
            return citiesInOptimalPath;
        }

        // trace city path backwards from destination city
        citiesInOptimalPath.add(destination);

        String sourceCity;
  
        while(flightToCity != null) {
            sourceCity = flightToCity.getCurrCity();
            citiesInOptimalPath.add(sourceCity);
            flightToCity = mapCityToItsIncomingFlight.get(sourceCity);
        }

        return citiesInOptimalPath;

    }
    
}
