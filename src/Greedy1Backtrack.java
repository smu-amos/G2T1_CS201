import java.util.*;

public class Greedy1Backtrack implements GraphAlgo {

    public String executeAlgo(String source, String dest, HashMap<String,Integer> cityToIndex, FlightInfoList graph[][]) {
        //get the indices of the matrix that correspond to the city
        int sourceIndex = cityToIndex.get(source);
        int destIndex = cityToIndex.get(dest);
        int prevSourceIndex = -1;

        //records flight tickets taken by id -- to determine cost of pitstopp flights
        //HashMap<Integer, Integer> ticketRecords = new HashMap<Integer, Integer>();
        int prevTicketId = -1;
        
        //records cities explored, but does not necessarily means it ends up being part of the flight path
        HashMap<Integer, Integer> citiesVisited = new HashMap<>();
        citiesVisited.put(sourceIndex, 1);

        //records cities that are part of the flight path and track the pathIndex -- important for backtracking
        ArrayList<Integer> pathTaken = new ArrayList<>();
        pathTaken.add(sourceIndex);
        int pathIndex = 0;

        //record cities that are dead ends
        HashSet<Integer> deadEndCities = new HashSet<>();

        //a row in the matrix -- an array of FlightInfoList objects
        FlightInfoList rowBasedOnCurrentSource[] = graph[sourceIndex];

        //cost incurred, and maximum number of traversals
        int totalCost = 0;
        int maxHops = 50;
        int hopsTaken = 0;

        //length of matrix
        int length = rowBasedOnCurrentSource.length;

        String printedPath = "\n===Path Taken===\n" + source;

        String fromCity = source;

        //while not start at dead end and not reached the destination and not exceed maximum number of hops
        while (sourceIndex != -1 && sourceIndex != destIndex && hopsTaken < maxHops) {
            //create a variable to store cheapest ticket -- the FlightInfo object that has the lowest price
            FlightInfo cheapestFlightInfo = null;
            //create foundZeroCost flag
            boolean foundZeroCost = false;

            //update rowBasedOnCurrentSource
            rowBasedOnCurrentSource = graph[sourceIndex];

            //in a row, each cell contains a FlightInfoList object
            for (int i = 0; i < length; i++) {
                //if no FlightInfoList in that cell,
                if (rowBasedOnCurrentSource[i] == null) continue;
                //if flying to a dead end city,
                if (deadEndCities.contains(i)) continue;
                //skip city that already visited
                if (citiesVisited.containsKey(i)) {
                    System.out.println("already visited" + i);
                    continue;
                }

                //extract FlightInfo objects
                ArrayList<FlightInfo> flightInfoList = rowBasedOnCurrentSource[i].getFlightInfoList();

                //iterate through each flight info object in the flight info list to get cheapest path
                for (FlightInfo flightInfo : flightInfoList) {

                    //initialise cheapestFlightInfo to first flightInfo object returned
                    if (cheapestFlightInfo == null) {
                        cheapestFlightInfo = flightInfo;
                    }
                    else { 

                        //if not flying from origin
                        if (!flightInfo.getIsOrigin()) {
                            if (prevTicketId == flightInfo.getId()) {
                                System.out.println("Entered");
                                //cheapestFlightInfo will be set to that ticket
                                cheapestFlightInfo = flightInfo; 

                                //set flag that cost is already incurred
                                foundZeroCost = true;

                                //no need to consider any other ticket
                                break;
                            }

                            //ignore if not origin and not part of flight --> cannot be traversed

                        } else { //if flying from origin
                            if (flightInfo.getPrice() < cheapestFlightInfo.getPrice()) {
                                //System.out.println(flightInfo.getNextCity() + " " + flightInfo.getPrice());
                                cheapestFlightInfo = flightInfo;
                            }
                        }
                    }
                } 
                
                //skip other cities  if already found minimum cost which is 0
                if (foundZeroCost) break;
            }

            //Post-Processing -- after considering each row

            //if no cheapestFlightInfo, means no flights at all, means current source is a dead end
            if (cheapestFlightInfo == null) {
                System.out.println("Here " + cityToIndex.get(source) + " cant find next path");
                //if no path at all
                if (sourceIndex == cityToIndex.get(source)) break; 

                //go back to previous source (row) and find a new path (that is not traversed yet)
                //record this current source index as a dead end
                deadEndCities.add(sourceIndex);

                //"backtrack" to previous source, to try to find another path
                pathIndex--;
                sourceIndex = pathTaken.get(pathIndex);
                pathIndex--;
                prevSourceIndex = pathTaken.get(pathIndex);

                //enter the while loop again with the new source
                continue;
                //return "Dead End " + maxTraversals;
            }


            //update the total cost
            if (!foundZeroCost) {
                System.out.println(fromCity + " to " + cheapestFlightInfo.getNextCity() + " " + cheapestFlightInfo.getPrice());
                totalCost += cheapestFlightInfo.getPrice();
            } else {
                System.out.println(fromCity + " to " + cheapestFlightInfo.getNextCity() + " zero here");
            }

            //record ticket as purchased
            prevTicketId = cheapestFlightInfo.getId();

            //cheapestFlightInfo.setTraversed();

            //store flight ticket next stop as cities visited and path taken
            citiesVisited.put(sourceIndex, 1);
            pathTaken.add(sourceIndex);

            //store current source index
            prevSourceIndex = sourceIndex;
            //update source index
            sourceIndex = cityToIndex.get(cheapestFlightInfo.getNextCity());
            fromCity = cheapestFlightInfo.getNextCity();

            //increment pathIndex
            pathIndex++;
            
            //increment hops taken
            hopsTaken++;

            //add to printedPath
            printedPath += " -- " + cheapestFlightInfo.getNextCity();

            //end of while loop
        }

        printedPath += "\n\n==Hops Taken==\n" + hopsTaken + "\n";

        if (sourceIndex != destIndex) {
            printedPath += "--> Could not find cheapest path within " + maxHops + " hops\n";
        }
        printedPath += "\n\n==Total Cost==\n$" + totalCost;
        
        return printedPath;
    }
}
