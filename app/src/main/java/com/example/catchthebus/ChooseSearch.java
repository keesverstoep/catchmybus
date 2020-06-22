package com.example.catchthebus;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ChooseSearch extends AppCompatActivity {

    Button locationButton;
    Button queryButton;
    EditText edit_query;
    ArrayList<Stop> stops;
    ArrayList<Stop> locationStops;
    ArrayList<Stop> queryStops;

    private BroadcastReceiver broadcastReceiver;
    boolean sendInfo;

    static double LATITUDE = 52.3348392;
    static double LONGITUDE = 4.8714316;
    static int DUMMY_DISTANCE = 50000000;
    static int N_OF_CLOSEST_STOPS = 50;
    static int N_OF_STRING_STOPS = 50;

    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;


    double myLat;
    double myLong;



    Person me = new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_search);
        try {
            //apiTest();
            client = new FusedLocationProviderClient(this);
            start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException, InterruptedException {
        create_GPS_Service();
        startCsvThread();
    }

    private void create_GPS_Service(){
        sendInfo = false;
        Intent i =new Intent(getApplicationContext(),GPS_Service.class);
        i.putExtra("sentInfo",sendInfo);
        startService(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    myLat =intent.getDoubleExtra("myLat",69);
                    myLong =intent.getDoubleExtra("myLong",420);
                    //System.out.println("Lat "+myLat);
                    //System.out.println("Long "+myLong);
                    //myLat = 0;
                    //myLong = 0;
                    me.setLatitude(myLat);
                    me.setLongitude(myLong);
                    if(!runtime_permissions()){
                        setLocationButton();
                        setSearchButton();
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void setSearchButton(){
        queryButton = (Button) findViewById(R.id.buttonQuery);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQueryActivity();
            }
        });
    }

    public void openQueryActivity(){
        edit_query = (EditText) findViewById(R.id.edit_query);
        String query = edit_query.getText().toString();
        ArrayList<Stop> queryStops = getSearchStringStops(query);
        ArrayList<Stop> queryStopsReduced = reduceStops(queryStops);
        Intent intent = new Intent(this, QueryActivity.class);
        intent.putExtra("query",query);
        intent.putExtra("stopsList", queryStopsReduced);
        intent.putExtra("me", me);
        startActivity(intent);
    }

    public void setLocationButton(){
        locationButton = (Button) findViewById(R.id.buttonLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openLocationActivity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openLocationActivity() throws IOException {
        Intent intent = new Intent(this, LocationSearch.class);
        ArrayList<Stop> closestStops = getNClosestStops(stops);
        ArrayList<Stop> closestStopsReduced = reduceStops(closestStops);
        System.out.println(closestStopsReduced.size());
        System.out.println(closestStopsReduced.get(0).getStop_name());
        intent.putExtra("stopsList",closestStopsReduced);
        intent.putExtra("me", me);
        startActivity(intent);
    }

    public void startCsvThread() throws InterruptedException {
        CsvWork csvWork = new CsvWork();

        Thread workThread = new Thread(csvWork);
        workThread.start();
        workThread.join();
    }

    public void stopCsvThread(){

    }

    class CsvWork implements Runnable{

        CsvWork(){

        }

        @Override
        public void run() {
            try {
                parseCSVFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void parseCSVFile() throws IOException {
            InputStream inputStream = getResources().openRawResource(R.raw.amststops);
            StopsFromCSVParser csvParser = new StopsFromCSVParser();
            csvParser.setInputStream(inputStream);
            stops = csvParser.firstParseStops();
        }
    }

    public ArrayList<Stop> getNClosestStops(ArrayList<Stop> parsedStops) throws IOException {
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
        return stops;
    }

    public ArrayList<Stop> getStops(Stop currentStop, ArrayList<Stop> stops) {
        for (int j = 0; j < N_OF_CLOSEST_STOPS; j++) {
            Stop listStop = stops.get(j);
            if(currentStop.getDistance()<listStop.getDistance()) {
                stops.add(j, currentStop);
                stops.remove(N_OF_CLOSEST_STOPS);
                return stops;
            }
        }
        return stops;
    }

    public  ArrayList<Stop> reduceStops(ArrayList<Stop> stops){
        ArrayList<Stop> result = new ArrayList<Stop>();
        boolean newName;
        for (int i = 0; i < stops.size(); i++) {
            double dist = distance(me.getLatitude(), me.getLongitude(), stops.get(i).get_Latitude(), stops.get(i).get_Longitude());
            stops.get(i).setDistance(dist);
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

    public ArrayList<Stop> getSearchStringStops(String query){
       // System.out.println("query "+ query);
        ArrayList<Stop> searchStringStops = new ArrayList<Stop>();
        for (int i = 0; i < stops.size(); i++) {
            if (searchStringStops.size()>N_OF_STRING_STOPS) {
                return searchStringStops;
            }
            Stop currentStop = stops.get(i);
            if (containsIgnoreCase(currentStop.getStop_name(), query)) {
                searchStringStops.add(currentStop);
                //System.out.println("added "+currentStop.getStop_name());
            }
        }
        System.out.println("here "+searchStringStops.size());
        return searchStringStops;
    }

    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().contains(subString.toLowerCase());
    }

    public double distance(double lat1, double lon1, double lat2, double lon2) {
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

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                setLocationButton();
                setSearchButton();
            }else {
                runtime_permissions();
            }
        }
    }

}
