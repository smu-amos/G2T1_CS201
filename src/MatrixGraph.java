import java.util.*;

public class MatrixGraph {
    // Create matrix, where each edge is an arraylist of FightInfo
    //private ArrayList<FlightInfo>[][] adjacencyMatrix;
    //private ArrayList<ArrayList<ArrayList<FlightInfo>>> adjacencyMatrix; 
    private FlightInfoList adjacencyMatrix[][];
    private int numOfNodes;

    public MatrixGraph(int numOfNodes) {
        this.numOfNodes = numOfNodes;
        //ArrayList<FlightInfo> flightInfo = new ArrayList<>();
        //adjacencyMatrix = new ArrayList<>();
        adjacencyMatrix = new FlightInfoList[numOfNodes][numOfNodes];
    }

    //
    public FlightInfoList[][] getMatrix() {
        return adjacencyMatrix;
    }

    public void setInit(int i, int j) {
        adjacencyMatrix[i][j] = new FlightInfoList();
    }

    // Add edges
    public void addEdge(int i, int j, FlightInfo flightInfo) {
            // FlightInfoList fil = adjacencyMatrix[i][j];
            FlightInfoList flightInfoList = adjacencyMatrix[i][j];
            flightInfoList.add(flightInfo);
            // System.out.println(fil);
    }

    // Remove edges
    public void removeEdge(int i, int j) {
        adjacencyMatrix[i][j] = null;
    }

    public void removeSpecificFlightInfo(int i, int j, FlightInfo flightInfo) {
        adjacencyMatrix[i][j].remove(flightInfo);
    }

    // Print an edge - the list of FlightInfo objects in each cell
    // public void printEdge(int i, int j) {
    //     for (FlightInfo temp : adjacencyMatrix[i][j]) {
    //         System.out.println(temp);
    //     }
    // }
    

    // public static void main(String args[]) {
    //     MatrixGraph g = new MatrixGraph(4);

    //     // g.addEdge(0, 1);
    //     // g.addEdge(0, 2);
    //     // g.addEdge(1, 2);
    //     // g.addEdge(2, 0);
    //     // g.addEdge(2, 3);

    //     System.out.print(g.toString());

    //     ArrayList<String> a = new ArrayList<>();
    //     a.add("hi");
    //     a.set(0, null);
    // }

}