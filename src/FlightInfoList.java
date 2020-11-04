import java.util.ArrayList;

public class FlightInfoList {
private ArrayList<FlightInfo> flightInfoList;
   public FlightInfoList() {
       flightInfoList = new ArrayList<>();
   } 

   public void add(FlightInfo flightInfo) {
       flightInfoList.add(flightInfo);
   }

   public void remove(FlightInfo flightInfo) {
    flightInfoList.remove(flightInfo);
   }

   public ArrayList<FlightInfo> getFlightInfoList() {
    return flightInfoList;
   }

   @Override
   public String toString() {
       String result = "";
        for (FlightInfo flightInfo : flightInfoList) {
            result += flightInfo.toString();
        }
        return result;
   }

}

