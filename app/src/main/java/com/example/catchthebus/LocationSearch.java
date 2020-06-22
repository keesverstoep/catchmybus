package com.example.catchthebus;

import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class LocationSearch extends AppCompatActivity {
    private RecyclerView locationRecyclerView;
    private ListAdapter locationAdapter;
    private RecyclerView.LayoutManager locationLayout;
    public Person me;
    private LocationManager locationManager;
    private LocationListener listener;
    ArrayList<Stop> stops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        start();

    }

    public void start(){
        Intent i = getIntent();

        try {
            me = (Person) i.getSerializableExtra("me");
            stops = (ArrayList<Stop>) i.getSerializableExtra("stopsList");
           // testLocationList.setText(stops.get(0).getStop_name());
            doRecycler();
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            Stop dummyStop = new Stop();
            dummyStop.setTown("");
            dummyStop.setStop("Error: No stops");
            dummyStop.setDistanceString("");
            stops.add(dummyStop);
            doRecycler();
        }
    }



    public void doRecycler(){
        locationRecyclerView = findViewById(R.id.recyclerViewLocation);
        locationRecyclerView.setHasFixedSize(true);
        locationLayout = new LinearLayoutManager(this);
        locationAdapter = new ListAdapter(stops);
        locationRecyclerView.setLayoutManager(locationLayout);
        locationRecyclerView.setAdapter(locationAdapter);

        locationAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws InterruptedException, IOException, JSONException {
                startStopChosenThread(position);
            }
        });
    }

    public void startStopChosenThread(int position){
        StopChosenWork stopChosenWork = new StopChosenWork(position);
        new Thread(stopChosenWork).start();
    }

    public void stopStopChosenThread(){

    }

    class StopChosenWork implements Runnable{
        int position;
        StopChosenWork(int position){
            this.position = position;
        }

        @Override
        public void run() {
            try {
                stopChosen(position);
            }catch (IOException | InterruptedException | JSONException e){
                e.printStackTrace();
            }

        }
    }

    public void stopChosen(int position) throws InterruptedException, JSONException, IOException {
        Stop stop = stops.get(position);
        //ArrayList<Bus> buses = searchForBus(stop);
        Intent intent = new Intent(this, PickABus.class);
        //queryAdapter.notifyItemChanged(position);
        intent.putExtra("me",me);
        //intent.putExtra("buses",buses);
        intent.putExtra("stop", stop);
        startActivity(intent);
    }

    public ArrayList<Bus> searchForBus(Stop stop) throws IOException, InterruptedException, JSONException {
        ArrayList<Bus> result = new ArrayList<Bus>();
        ArrayList<Bus> buses = new ArrayList<Bus>();
        buses = getBusesOnStation(stop);
        result.addAll(buses);
        for (int i = 0; i < stop.sameNameStops.size(); i++) {
            buses = getBusesOnStation(stop.sameNameStops.get(i));
            result.addAll(buses);
        }
        Collections.sort(result);
        return result;
    }

    public ArrayList<Bus> getBusesOnStation(Stop stop) throws IOException, InterruptedException, JSONException {
        System.out.println(stop.getStop_name() +" "+ stop.get_Latitude() +" "+ stop.get_Longitude()+" "+stop.getStop_code());
        ParsingJSON parseTheJSON = new ParsingJSON();
        OVapi oVapi = new OVapi();
        oVapi.doRequestByTimeCodePoint(stop.getStop_code());
        String JSONtpcToParse = oVapi.getTpcResponse();
        System.out.println("here"+JSONtpcToParse);
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
}
