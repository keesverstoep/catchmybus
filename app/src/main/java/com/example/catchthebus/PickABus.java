package com.example.catchthebus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;

public class PickABus extends AppCompatActivity {
    private RecyclerView busRecyclerView;
    private BusAdapter busAdapter;
    private RecyclerView.LayoutManager busLayout;
    public Person me;
    Button refresh;
    Timer timer;
    Thread workThread;
    ArrayList<Bus> instanceBuses;
    Stop stop;

    TextView stopText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_a_bus);

        try {
            start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() throws InterruptedException {
        Intent i = getIntent();
        try {
            Stop stop = (Stop) i.getSerializableExtra("stop");
            this.stop = stop;
            stopText = findViewById(R.id.stopName);
            stopText.setText(stop.getStop());
            me = (Person) i.getSerializableExtra("me");
            refresh = findViewById(R.id.refresh);
            doLiveBuses();
            enable_refresh();
        } catch (IndexOutOfBoundsException | InterruptedException e) {
            e.printStackTrace();
            Bus dummyBus = new Bus();
            dummyBus.setLinePublicNumber("Error: No buses");
            dummyBus.set_destinationCode50("");
            dummyBus.setExpectedDepartureTime("");
            instanceBuses.add(dummyBus);
            doLiveBuses();
        }
    }

    private void enable_refresh(){
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    doLiveBuses();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doLiveBuses() throws InterruptedException {
        /*final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            startBusesThread(stop);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 12000);*/
        //maybe add refresh button
        startBusesThread(stop);
    }

    private void startBusesThread(Stop stop) throws InterruptedException {
        BusesWork busesWork = new BusesWork(stop);
        Thread workThread = new Thread(busesWork);
        workThread.start();
        workThread.join();
        doRecycler(instanceBuses);
    }

    class BusesWork implements Runnable{
        Stop workStop;
        BusesWork(Stop workStop){
            this.workStop = workStop;
        }
        @Override
        public void run() {
            try {
                instanceBuses = searchForBus(workStop);
            }catch (IOException | InterruptedException | JSONException e){
                e.printStackTrace();
            }
        }
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
        //System.out.println(stop.getStop_name() +" "+ stop.get_Latitude() +" "+ stop.get_Longitude()+" "+stop.getStop_code());
        ParsingJSON parseTheJSON = new ParsingJSON();
        OVapi oVapi = new OVapi();
        oVapi.doRequestByTimeCodePoint(stop.getStop_code());
        String JSONtpcToParse = oVapi.getTpcResponse();
        //System.out.println("here"+JSONtpcToParse);
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

    public void doRecycler(ArrayList<Bus> buses) {
        busRecyclerView = findViewById(R.id.recyclerViewBuses);
        busRecyclerView.setHasFixedSize(true);
        busLayout = new LinearLayoutManager(this);
        busAdapter = new BusAdapter(buses);
        busRecyclerView.setLayoutManager(busLayout);
        busRecyclerView.setAdapter(busAdapter);

        busAdapter.setOnItemClickListener(new BusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws InterruptedException, IOException, JSONException {
                startBusChosenThread(position);
            }
        });
    }

    public void startBusChosenThread(int position) {
        BusChosenWork busChosenWork = new BusChosenWork(position);
        new Thread(busChosenWork).start();
    }

    public void stopStopChosenThread() {

    }

    class BusChosenWork implements Runnable {
        int position;

        BusChosenWork(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            try {
                busChosen(position);
            } catch (IOException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void busChosen(int position) throws InterruptedException, JSONException, IOException {
        Bus bus = instanceBuses.get(position);
        Intent intent = new Intent(this, StartHapticActivity.class);
        //queryAdapter.notifyItemChanged(position);
        intent.putExtra("me",me);
        intent.putExtra("stop", stop);
        intent.putExtra("bus",bus);
        startActivity(intent);
    }

   /* @Override
    protected void onResume() {
        super.onResume();
        try {
            doLiveBuses();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        //timer.cancel();
        //timer.purge();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //timer.cancel();
        //timer.purge();
    }
}
