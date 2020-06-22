package com.example.catchthebus;

import org.json.JSONException;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Main2 {
    static double LATITUDE = 52.4052957;
    static double LONGITUDE = 4.8883806;
    static int N_OF_CLOSEST_STOPS = 50;
    static int N_OF_STRING_STOPS = 50;
    static int DUMMY_DISTANCE = 50000000;
    static Person me;
    ArrayList<Stop> stops;


    /*public static void main(String[] args) throws IOException, InterruptedException, JSONException {
        me = new Person();
        me.setLatitude(LATITUDE);
        me.setLongitude(LONGITUDE);
        searchForStation();

    }*/

    public static void searchForStation()throws IOException, InterruptedException, JSONException {
        ArrayList<Stop> stops = parseStops();
        System.out.println("Welcome to the catch the bus demo!");
        System.out.println("First you must pick a station.");
        System.out.println("To search with a string, type s.");
        System.out.println("To search based on your location, type l. (Your location can be changed with the LATITUDE and LONGITUDE constants in this class.)");
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        if(s.equals("s")) {
            ArrayList<Stop> searchStringStops = getSearchStringStops(stops);
            ArrayList<Stop> searchStringStopsReduced = reduceStops(searchStringStops);
            Stop chosenStop = pickStation(searchStringStopsReduced);
            searchForBus(chosenStop);
        }
        else if (s.equals("l")) {
            ArrayList<Stop> closestStops = getNClosestStops(stops);
            ArrayList<Stop> closestStopsReduced = reduceStops(closestStops);
            Stop chosenStop = pickStation(closestStopsReduced);
            searchForBus(chosenStop);
        }
        else {
            System.out.println("End of program.");
            System.exit(0);
        }
    }

    public static void searchForBus(Stop stop) throws IOException, InterruptedException, JSONException {
        ArrayList<Bus> result = new ArrayList<Bus>();
        ArrayList<Bus> buses = new ArrayList<Bus>();
        buses = getBusesOnStation(stop);
        result.addAll(buses);
        for (int i = 0; i < stop.sameNameStops.size(); i++) {
            buses = getBusesOnStation(stop.sameNameStops.get(i));
            result.addAll(buses);
        }
        Collections.sort(result);
        pickABus(result);

    }

    public static ArrayList<Bus> getBusesOnStation(Stop stop) throws IOException, InterruptedException, JSONException {
        System.out.println(stop.getStop_name() +" "+ stop.get_Latitude() +" "+ stop.get_Longitude()+" "+stop.getStop_code());
        ParsingJSON parseTheJSON = new ParsingJSON();
        OVapi oVapi = new OVapi();
        oVapi.doRequestByTimeCodePoint(stop.getStop_code());
        String JSONtpcToParse = oVapi.getTpcResponse();
        if (JSONtpcToParse.equals("[]")) {
            ArrayList<Bus> dummies = new ArrayList<Bus>();
            return dummies;
        }
        //System.out.println(JSONtpcToParse);
        parseTheJSON.parseTimingPointCodeString(JSONtpcToParse, stop);
        //Station station = parseTheJSON.station; - station is stop returned by request, rarely it is erroneous, this is something to deal with in testing
        ArrayList<Bus> buses = parseTheJSON.buses;
        return buses;
    }

    public static void pickABus(ArrayList<Bus> buses) throws IOException, InterruptedException, JSONException {
        for (int i = 0; i < buses.size(); i++) {
            Bus aBus = buses.get(i);
            System.out.println(i +" #"+ aBus.getLinePublicNumber() +"  "+  aBus.get_destinationCode50() + "  "+ aBus.getExpectedDepartureTimeAsTimeString() +" "+ aBus.getStop().getStop_code());
        }
        System.out.println("\nPick my bus");
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        int result = Integer.parseInt(s);
        Bus arbitrarilyChosenBus = buses.get(result);
        getSelectedBusInfo(arbitrarilyChosenBus);
        test(arbitrarilyChosenBus.getStop(),arbitrarilyChosenBus);
    }


    public static Stop pickStation(ArrayList<Stop> stations) {
        System.out.println("\nPick a station (type its number)");
        for (int i = 0; i < stations.size(); i++) {
            System.out.println(i+" "+stations.get(i).getStop()+" "+stations.get(i).getTown());
        }
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        int result = Integer.parseInt(s);
        Stop arbitrarilyChosenStop = stations.get(result);
        return arbitrarilyChosenStop;
    }

    public static ArrayList<Stop> reduceStops(ArrayList<Stop> stops){
        ArrayList<Stop> result = new ArrayList<Stop>();
        boolean newName;
        for (int i = 0; i < stops.size(); i++) {
            newName = true;
            for (int j = 0; j < result.size(); j++) {
                if (stops.get(i).getStop_name().equals(result.get(j).getStop_name())) {
                    result.get(j).sameNameStops.add(stops.get(i));
                    newName = false;
                }
            }
            if (newName) {
                result.add(stops.get(i));
            }
        }
        return result;
    }

    public static ArrayList<Stop> isStopNameInList(Stop stop, ArrayList<Stop> stops) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getStop_name().equals(stop.getStop_name())) {
                stops.get(i).sameNameStops.add(stop);
                return stops;
            }
        }
        return stops;
    }


    public static ArrayList<Stop> getSearchStringStops(ArrayList<Stop> parsedStops){
        System.out.println("Please type in your search query (name of station):");
        Scanner sc = new Scanner(System.in);
        String query = sc.nextLine();
        ArrayList<Stop> stops = new ArrayList<Stop>();
        for (int i = 0; i < parsedStops.size(); i++) {
            if (stops.size()>N_OF_STRING_STOPS) {
                return stops;
            }
            Stop currentStop = parsedStops.get(i);
            if (containsIgnoreCase(currentStop.getStop_name(), query)) {
                stops.add(currentStop);
            }
        }
        return stops;
    }

    public static ArrayList<Stop> parseStops() throws IOException {
        ArrayList<Stop> stops = new ArrayList<Stop>();
        StopsFromCSVParser stops2 = new StopsFromCSVParser();
        stops = stops2.firstParseStops();
        return stops;
    }

    /*
     * Naive/Temporary approach, go through list one by one and find closest stops
     */
    public static ArrayList<Stop> getNClosestStops(ArrayList<Stop> parsedStops) throws IOException {
        ArrayList<Stop> stops = new ArrayList<Stop>();
        Stop dummyStop = new Stop();
        dummyStop.setDistance(DUMMY_DISTANCE);
        for (int i = 0; i < N_OF_CLOSEST_STOPS; i++) {
            stops.add(dummyStop);
        }
        double dist = 0;
        for (int i = 0; i < parsedStops.size(); i++) { //For test run change this to 100
            Stop currentStop = parsedStops.get(i);
            dist = distance(me.getLatitude(), me.getLongitude(), currentStop.get_Latitude(), currentStop.get_Longitude());
            currentStop.setDistance(dist);
            Stop lastStop = stops.get(N_OF_CLOSEST_STOPS-1);
            if(dist<=lastStop.getDistance()) {
                stops = getStops(currentStop, stops);
            }
        }
        for (int i = 0; i < stops.size(); i++) {
            System.out.println(i+" "+stops.get(i).stop_name +" "+ stops.get(i).getDistance());
        }
        return stops;
    }

    public static ArrayList<Stop> getStops(Stop currentStop, ArrayList<Stop> stops) {
        for (int j = 0; j < N_OF_CLOSEST_STOPS; j++) {
            Stop listStop = stops.get(j);
            if(currentStop.getDistance()<listStop.getDistance()) {
                stops.add(j, currentStop);
                stops.remove(N_OF_CLOSEST_STOPS);
                return stops;
            }
        }
        //for (int i = 0; i < stops.size(); i++) {
        //System.out.println(stops.get(i).stop_name + " "+ stops.get(i).getDistance());
        //}
        return stops;
    }

    public static void getSelectedBusInfo(Bus bus) throws IOException, InterruptedException, JSONException {
        ParsingJSON parseTheJSON = new ParsingJSON();
        System.out.println(bus.getDataOwnerCode()+"_"+ bus.getLocalServiceLevelCode()+"_"+bus.getLinePlanningNumber()+"_"+bus.getJourneyNumber()+"_"+bus.getFortifyOrderNumber());
        OVapi oVapi = new OVapi();
        oVapi.doRequestByJourney(bus.getDataOwnerCode(), bus.getLocalServiceLevelCode(), bus.getLinePlanningNumber(), bus.getJourneyNumber(), bus.getFortifyOrderNumber());
        String JSONjrnToParse = oVapi.getJourneyResponse();
        parseTheJSON.parseJourneyString(bus,JSONjrnToParse);
    }


    public static void test(Stop stop, Bus bus) {
        Person me = new Person();
        me.setLatitude(LATITUDE);
        me.setLongitude(LONGITUDE);

        //System.out.println(station.getLatitude());
        //System.out.println(station.getLongitude());

        double dist = distance(me.getLatitude(), me.getLongitude(), stop.get_Latitude(), stop.get_Longitude());
        System.out.println("Distance between me and station is: "+dist+" meters");

        //System.out.println("Distance between bus and station is: " + distance(bus.getLatitude(), bus.getLongitude(), station.getLatitude(), station.getLongitude())+" meters");

        //OK so the api does not give the location of the bus but of the station

        //System.out.println("The target arrival time is: " + bus.getTargetArrivalTimeAsTimeString());
        //System.out.println("The target departure time is: " + bus.getTargetDepartureTimeAsTimeString());
        //System.out.println("The expected arrival time is: " + bus.getExpectedArrivalTimeAsTimeString());

        System.out.println("The expected departure time is: " + bus.getExpectedDepartureTimeAsTimeString());
        long diff = calculateTimeDifference(bus.expectedDepartureTimeAsTime);
        System.out.println("Time to get to station is: " + diff + " seconds.");
        hapticFeedback(dist, diff);
    }

    public static void hapticFeedback(double dist, long diff) {
        String decision = "";
        double speed = dist/diff;
        System.out.println("You need a speed of: " + speed + " m/s to get to the bus on time.");
        if(speed < 0.01) {
            decision = "Sleep, relax, you have time";
        }
        else if(speed >= 0.01 && speed < 0.1) {
            decision = "Wake up, eat, take shower";
        }
        else if(speed >= 0.1 && speed < 0.8) {
            decision = "Get ready";
        }
        else if(speed >= 0.8 && speed < 1.2) {
            decision = "Start heading";
        }
        else if(speed >= 1.2 && speed < 2) {
            decision = "Hurry up";
        }
        else if(speed >= 2  && speed < 3.7) {
            decision = "RUN!";
        }
        else if(speed >= 3.7 && speed < 12.27) {
            decision = "SPRINT";
        }
        else if(speed >= 12.27) {
            decision = "You may or may not be late";
        }
        System.out.println(decision);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;
        return (c * r)*1000;
    }

    public static long calculateTimeDifference(LocalDateTime exp) {
        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.SECONDS.between(now, exp);
    }

    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().contains(subString.toLowerCase());
    }

}
