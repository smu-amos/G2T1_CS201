import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataLoader {

    private static MatrixGraph matrixGraph;
    public static FlightInfoList adjacencyMatrix[][];

    public static void main (String[] args) {        
        File file = new File("");
        BufferedReader br = null;
        BufferedReader br2 = null;
        String line = "";
        Map<String, Integer> cityToIndex = new HashMap<>();
        Map<Integer, List<String>> flightIDToListOfStops = new HashMap<>();
        int cityIndex = 0;
        String[] results = null;

        try {

            br = new BufferedReader(new FileReader(file));
            br.readLine(); // this will read and skip the first line
            while ((line = br.readLine()) != null) {
                // Populate cityToIndex Hashmap 
                // for loop iterates through index of a single row
                for(int i = 0; i < line.length(); i++) {
                    results = line.split(",");
                }

                if (results.length == 4) {
                    for (int j = 1; j <= 2; j++) { //for direct flight
                        if (!cityToIndex.containsKey(results[j])) {
                            cityToIndex.put(results[j], cityIndex);
                            cityIndex++;
                        } 
                    }
                } else if (results.length == 5) { //for 1 pitstop
                    for (int j = 1; j <= 3; j++) {
                        if (!cityToIndex.containsKey(results[j])) {
                            cityToIndex.put(results[j], cityIndex);
                            cityIndex++;
                        } 
                    }
                } else if (results.length == 6) { //for 2 pitstops
                    for (int j = 1; j <= 4; j++) {
                        if (!cityToIndex.containsKey(results[j])) {
                            cityToIndex.put(results[j], cityIndex);
                            cityIndex++;
                        } 
                    }
                }
            }

            //Pass
            //create the n x n Matrix where n = number of cities
            int numberOfCities = cityToIndex.size();
            matrixGraph = new MatrixGraph(numberOfCities);

            //this while loop is to create the edges
            br2 = new BufferedReader(new FileReader(file));
            br2.readLine();
            while ((line = br2.readLine()) != null) {
                // for loop iterates through each column to create an edge
                for(int i = 0; i < line.length(); i++) {
                    //Pass
                    results = line.split(",");
                }

                // View hashmap and cross-reference to get the index of the cities
                if (results.length == 4) { //direct flight
                    int sourceIndex = cityToIndex.get(results[1]);
                    int destIndex = cityToIndex.get(results[2]);

                    // create edge
                    FlightInfo flightInfo = new FlightInfo(Integer.parseInt(results[3]), true, results[2], results[1], Integer.parseInt(results[0]));

                    // store the list of stops to its flightID
                    flightIDToListOfStops.put(flightInfo.getId(), new ArrayList<String>(Arrays.asList(results[1],results[2])));

                    if (matrixGraph.getMatrix()[sourceIndex][destIndex] == null) {
                        matrixGraph.setInit(sourceIndex, destIndex);
                    }
                    matrixGraph.addEdge(sourceIndex, destIndex, flightInfo);
                } else if (results.length == 5) { //1 pitstop
                    int sourceIndex = cityToIndex.get(results[1]);
                    int pitstopIndex = cityToIndex.get(results[2]);
                    int destIndex = cityToIndex.get(results[3]);
                    FlightInfo flightInfo1 = new FlightInfo(Integer.parseInt(results[4]), true, results[2], results[1], Integer.parseInt(results[0]));
                    FlightInfo flightInfo2 = new FlightInfo(0, false, results[3], results[2], Integer.parseInt(results[0]));

                    // store the list of stops to its flightID
                    flightIDToListOfStops.put(flightInfo1.getId(), new ArrayList<String>(Arrays.asList(results[1],results[2],results[3])));

                    if (matrixGraph.getMatrix()[sourceIndex][pitstopIndex] == null) {
                        matrixGraph.setInit(sourceIndex, pitstopIndex);
                    }

                    if (matrixGraph.getMatrix()[pitstopIndex][destIndex] == null) {
                        matrixGraph.setInit(pitstopIndex, destIndex);
                    }
                    //create Edge: src to pitstop
                    matrixGraph.addEdge(sourceIndex, pitstopIndex, flightInfo1);
                    //create Edge: pitstop to dest 
                    matrixGraph.addEdge(pitstopIndex, destIndex, flightInfo2);
                } else if (results.length == 6) { //2 pitstops
                    int sourceIndex = cityToIndex.get(results[1]);
                    int pitstopIndex1 = cityToIndex.get(results[2]);
                    int pitstopIndex2 = cityToIndex.get(results[3]);
                    int destIndex = cityToIndex.get(results[4]);

                    //create FlightInfo objects
                    FlightInfo flightInfo1 = new FlightInfo(Integer.parseInt(results[5]), true, results[2], results[1], Integer.parseInt(results[0]));
                    FlightInfo flightInfo2 = new FlightInfo(0, false, results[3], results[2], Integer.parseInt(results[0]));
                    FlightInfo flightInfo3 = new FlightInfo(0, false, results[4], results[3], Integer.parseInt(results[0]));

                    flightIDToListOfStops.put(flightInfo1.getId(), new ArrayList<String>(Arrays.asList(results[1],results[2],results[3], results[4])));

                    if (matrixGraph.getMatrix()[sourceIndex][pitstopIndex1] == null) {
                        matrixGraph.setInit(sourceIndex, pitstopIndex1);
                    }

                    if (matrixGraph.getMatrix()[pitstopIndex1][pitstopIndex2] == null) {
                        matrixGraph.setInit(pitstopIndex1, pitstopIndex2);
                    }

                    if (matrixGraph.getMatrix()[pitstopIndex2][destIndex] == null) {
                        matrixGraph.setInit(pitstopIndex2, destIndex);
                    }
                    //create Edges
                    matrixGraph.addEdge(sourceIndex, pitstopIndex1, flightInfo1);
                    matrixGraph.addEdge(pitstopIndex1, pitstopIndex2, flightInfo2);
                    matrixGraph.addEdge(pitstopIndex2, destIndex, flightInfo3);
                }
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Test Print
        // FlightInfoList adjacencyMatrix[][] = matrixGraph.getMatrix();
        // for (int i = 0; i < 3; i++) {
        //     for (int j = 0; j < 3; j++) {
        //         System.out.println(adjacencyMatrix[i][j]);
        //     }
        // }

        String result;

        // //Run greedy algo
        // System.out.println("Greedy:");
        // Greedy1 greedy1 = new Greedy1();
        // result = greedy1.executeAlgo("AMS", "PVG", cityToIndex, flightIDToListOfStops, matrixGraph.getMatrix());
        // System.out.println(result);

        // System.out.println();

        // //Run greedy backtrack algo
        // System.out.println("Greedy with Backtrack:");
        // Greedy1Backtrack greedy1Backtrack = new Greedy1Backtrack();
        // result = greedy1Backtrack.executeAlgo("AMS", "PVG", cityToIndex, flightIDToListOfStops, matrixGraph.getMatrix());
        // System.out.println(result);

        // System.out.println();

        System.out.println("Dijkstra:");
        long startTime = System.currentTimeMillis();
        Dijkstra dijkstra = new Dijkstra();
        result = dijkstra.executeAlgo("AMS", "PVG", cityToIndex, flightIDToListOfStops, matrixGraph.getMatrix());
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(result);

        System.out.println(startTime);
        System.out.println(estimatedTime);
        System.out.println();        

        //Run BFSAllPaths algo
        System.out.println("BFSAllPaths:");
        startTime = System.currentTimeMillis();
        BFSAllPaths bfsAllPaths = new BFSAllPaths();
        result = bfsAllPaths.executeAlgo("AMS", "PVG", cityToIndex, flightIDToListOfStops, matrixGraph.getMatrix());
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(result);

        System.out.println("BFSAllPaths:");
        
        System.out.println(startTime);
        System.out.println(estimatedTime);

        System.out.println();

        //Run BFSFirstPath algo
        System.out.println("BFSFirstPath start:");

        startTime = System.currentTimeMillis();
        BFSFirstPath bfsFirstPath = new BFSFirstPath();
        result = bfsFirstPath.executeAlgo("AMS", "PVG", cityToIndex, flightIDToListOfStops, matrixGraph.getMatrix());
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(result);

        System.out.println("BFSFirstPath end:");
        
        System.out.println(startTime);
        System.out.println(estimatedTime);


    
    }
}