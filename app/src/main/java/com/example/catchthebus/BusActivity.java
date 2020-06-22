package com.example.catchthebus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONException;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.IOException;
import java.util.ArrayList;

public class BusActivity extends AppCompatActivity {

    public Person me;
    Stop stop;
    Bus bus;
    ArrayList<Pass> passes;
    TextView busName;
    TextView stopName;
    TextView timeBetween;
    TextView distanceBetween;
    TextView advice;
    Handler handler;
    Runnable myRunnable;
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver broadcastReceiver2;

    private RecyclerView busRecyclerView;
    private PassAdapter busAdapter;
    private RecyclerView.LayoutManager busLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_bus);
        try {
            start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() throws InterruptedException {
        Intent i = getIntent();
        me = (Person) i.getSerializableExtra("me");
        bus = (Bus) i.getSerializableExtra("bus");
        stop = (Stop) i.getSerializableExtra("stop");
        busName = findViewById(R.id.busName);
        stopName = findViewById(R.id.yourStop2);
        timeBetween = findViewById(R.id.timeBetween);
        distanceBetween = findViewById(R.id.distanceBetween);
        advice = findViewById(R.id.advice);
        busName.setText(bus.getLinePublicNumber()+"_"+ bus.get_destinationCode50() + " is your bus");
        stopName.setText(stop.getStop()+" "+stop.getTown() + " is your stop");
        long diff = calculateTimeDifference(bus.expectedDepartureTimeAsTime);
        double dist = distance(me.getLatitude(), me.getLongitude(), stop.get_Latitude(), stop.get_Longitude());
        int distInt = (int) dist;
        timeBetween.setText(Long.toString(diff)+" seconds to get to bus");
        distanceBetween.setText(Integer.toString(distInt)+" meters to get to bus");
        advice.setText(decision(dist,diff));
        startPassesThread(bus);
        //do this call every 15 seconds
        //doRecycler(passes) on thread finish!
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    System.out.println("RECEIVED PASSES");
                    passes = (ArrayList<Pass>) intent.getSerializableExtra("passes");
                    doRecycler();
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("passes_update"));
        if(broadcastReceiver2 == null){
            broadcastReceiver2 = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    System.out.println("RECEIVED DIST TIME");
                    String timeGotten = intent.getStringExtra("time");
                    int distGotten = intent.getIntExtra("dist", 99999);
                    String dec = intent.getStringExtra("dec");
                    int stationsLeft = intent.getIntExtra("stationsLeft",0);
                    timeBetween.setText(timeGotten+" to get to bus");
                    distanceBetween.setText(Integer.toString(distGotten)+" meters to get to bus");
                    advice.setText(dec+" "+stationsLeft+" stations left");
                }
            };
        }
        registerReceiver(broadcastReceiver2,new IntentFilter("dist_time_update"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiver2);
    }

    class PassesWork implements Runnable{
        Bus workBus;
        PassesWork(Bus workBus){
            this.workBus = workBus;
        }
        @Override
        public void run() {
            try {
                getSelectedBusInfo(workBus);
            }catch (IOException | InterruptedException | JSONException e){
                e.printStackTrace();
            }
        }
    }

    public void startPassesThread(Bus bus) throws InterruptedException {
        PassesWork passesWork = new PassesWork(bus);
        Thread workThread = new Thread(passesWork);
        workThread.start();
        workThread.join();
        doRecycler();
    }

    public void stopPassesThread(){

    }

    public void getSelectedBusInfo(Bus bus) throws IOException, InterruptedException, JSONException {
        ParsingJSON parseTheJSON = new ParsingJSON();
        OVapi oVapi = new OVapi();
        oVapi.doRequestByJourney(bus.getDataOwnerCode(), bus.getLocalServiceLevelCode(), bus.getLinePlanningNumber(), bus.getJourneyNumber(), bus.getFortifyOrderNumber());
        String JSONjrnToParse = oVapi.getJourneyResponse();
        parseTheJSON.parseJourneyString(bus,JSONjrnToParse);
        passes = parseTheJSON.passes;
    }

    public void doRecycler(){
        busRecyclerView = findViewById(R.id.passesRecycler);
        busRecyclerView.setHasFixedSize(true);
        busLayout = new LinearLayoutManager(this);
        busAdapter = new PassAdapter(passes);
        busRecyclerView.setLayoutManager(busLayout);
        busRecyclerView.setAdapter(busAdapter);
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

    public static String decision(double dist, long diff) {
        String decision = "";
        double speed = dist/diff;
        System.out.println("You need a speed of: " + speed + " m/s to get to the bus on time.");
        if(speed < 0.01 && speed > 0) {
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
        else if(speed < 0){
            decision = "Too late";
        }
        return decision;
    }

}

   /* public static void test(Stop stop, Bus bus) {
        Person me = new Person();
        me.setLatitude(LATITUDE);
        me.setLongitude(LONGITUDE);
        double dist = distance(me.getLatitude(), me.getLongitude(), stop.get_Latitude(), stop.get_Longitude());
        System.out.println("Distance between me and station is: "+dist+" meters");
        System.out.println("The expected departure time is: " + bus.getExpectedDepartureTimeAsTimeString());
        long diff = calculateTimeDifference(bus.expectedDepartureTimeAsTime);
        System.out.println("Time to get to station is: " + diff + " seconds.");
        hapticFeedback(dist, diff);
    }

    public static String hapticFeedback(double dist, long diff) {
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
       return decision;
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
}


/*
 System.out.println(bus.getDataOwnerCode()+"_"+ bus.getLocalServiceLevelCode()+"_"+bus.getLinePlanningNumber()+"_"+bus.getJourneyNumber()+"_"+bus.getFortifyOrderNumber());

 */