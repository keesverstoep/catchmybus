package com.example.catchthebus;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONException;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.catchthebus.App.CHANNEL_ID;

public class GPS_Service extends Service {

    private static final String TAG = "GPSIntentService";
    private double myLongitude;
    private double myLatitude;

    Notification notification;

    Timer timer;

    private double distance;
    LocalDateTime targetTime;
    LocalDateTime oldTargetTime;
    int stationsLeft;
    int oldStationsLeft;

    boolean stopLoop;
    boolean calculateDist;

    String status;
    String oldStatus;

    Bus bus;
    Stop stop;
    Person me;
    ArrayList<Pass> passes;

    String targetLongitudeString;
    String targetLatitudeString;

    String decision;
    String oldDecision;

    PowerManager.WakeLock wl;
    PowerManager pm;

    double DIST = 50 ;

    private double targetLongitude;
    private double targetLatitude;

    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private LocationCallback callback;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */

    @SuppressLint({"MissingPermission"})
    @Override
    public void onCreate() {
        super.onCreate();
        calculateDist = false;

        AndroidThreeTen.init(this);
        decision = "";
        oldDecision = "";

        Log.d(TAG, "onCreate");

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "service:myWLTag");
        wl.acquire();

        client = new FusedLocationProviderClient(this);
        callback = new LocationCallback();


        Log.d(TAG, "Wakelock acquired");
        //makeTheNotification();
        //locationStuff();

    }

    @SuppressLint("MissingPermission")
    public void locationStuff(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(1000);
        client.requestLocationUpdates(locationRequest,new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                /*if(stopLoop){
                    client.removeLocationUpdates(this);
                }*/
                super.onLocationResult(locationResult);
                myLatitude = locationResult.getLastLocation().getLatitude();
                myLongitude = locationResult.getLastLocation().getLongitude();
                //myLatitude = 52.3348392;
                //myLongitude = 4.8714316;
                //System.out.println(myLatitude + "in service");
                //System.out.println(myLongitude + "in service");
                sendLocationBroadcast(myLatitude,myLongitude);
                // send gps to main activity
                if(calculateDist){
                    distance = distance(myLatitude,myLongitude,targetLatitude,targetLongitude);
                   // System.out.println("Do Location Stuff " + wl.isHeld());
                    long timeDiff = calculateTimeDifference(targetTime);
                    decision = doHaptic(distance,timeDiff);
                    String text = makeNotificationText(distance,timeDiff);
                    updateNotification(status,text);
                    sendDistanceTimeBroadcast(timeDiff,status);
                }
            }
        }, Looper.myLooper());
    }

    public void sendDistanceTimeBroadcast(long timeDiff,String dec){
        int intDist = (int)distance;
        String time = convertTime(timeDiff);
        Intent i = new Intent("dist_time_update");
        i.putExtra("dist",intDist);
        i.putExtra("time",time);
        i.putExtra("dec",dec);
        i.putExtra("stationsLeft",stationsLeft);
        sendBroadcast(i);
    }

    public void sendLocationBroadcast(double myLatitude, double myLongitude){
        Intent i = new Intent("location_update");
        i.putExtra("myLat",myLatitude);
        i.putExtra("myLong",myLongitude);
        sendBroadcast(i);
    }

    private String makeNotificationText(double distance, long timeDiff){
        int value = (int)distance;
        String text = "";
        int numberOfMinutes = (int) (((timeDiff % 86400 ) % 3600 ) / 60);
        int numberOfSeconds = (int) (((timeDiff % 86400 ) % 3600 ) % 60);
        int numberOfHours =   (int) ((timeDiff % 86400 ) / 3600) ;
        if(numberOfHours==0){
            text= ""+value+"m "+ " "+numberOfMinutes+"m"+numberOfSeconds+"s "+stationsLeft+" stations left "+decision;
            if(numberOfMinutes==0){
                text= ""+value+"m "+numberOfSeconds+"s "+stationsLeft+" stations left "+decision;
            }
        }else {
            text= ""+value+"m "+numberOfHours+"h"+numberOfMinutes+"m"+numberOfSeconds+"s "+stationsLeft+" stations left "+decision;
        }
        return text;
    }

    private String convertTime(long timeDiff){
        String text = "";
        int numberOfMinutes = (int) (((timeDiff % 86400 ) % 3600 ) / 60);
        int numberOfSeconds = (int) (((timeDiff % 86400 ) % 3600 ) % 60);
        int numberOfHours =   (int) ((timeDiff % 86400 ) / 3600) ;
        if(numberOfHours==0){
            text= ""+numberOfMinutes+"m"+numberOfSeconds+"s";
            if(numberOfMinutes==0){
                text= ""+numberOfSeconds+"s";
            }
        }else {
            text= ""+numberOfHours+"h"+numberOfMinutes+"m"+numberOfSeconds+"s ";
        }
        return text;
    }

    private void updateNotification(String status, String text){
        Intent notificationIntent = new Intent(this, StartHapticActivity.class);
        notificationIntent.putExtra("me",me);
        notificationIntent.putExtra("bus",bus);
        notificationIntent.putExtra("stop",stop);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bus: "+bus.getLinePublicNumber()+" "+status)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();
        startForeground(1,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean sentInfo = intent.getBooleanExtra("sentInfo", false);
        if(sentInfo){
            bus = (Bus) intent.getSerializableExtra("bus");
            stop = (Stop) intent.getSerializableExtra("stop");
            me = (Person) intent.getSerializableExtra("me");
            targetLatitude = bus.getLatitude();
            targetLongitude = bus.getLongitude();
            targetTime = bus.getTargetDepartureTimeAsTime();
            oldTargetTime = targetTime;
            distance = distance(myLatitude,myLongitude,targetLatitude,targetLongitude);
            String distanceString = Double.toString(distance);
            updateNotification("Starting GPS","0");
            stopLoop = false;
            calculateDist = true;
            doLivePasses();
            locationStuff();
        }else {
            calculateDist = false;
            locationStuff();
        }
        return START_NOT_STICKY;
    }

    private void doLivePasses(){
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            startPassesThread(bus);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 12000);
    }

    public void startPassesThread(Bus bus) throws InterruptedException {
        PassesWork passesWork = new PassesWork(bus);
        Thread workThread = new Thread(passesWork);
        workThread.start();
        workThread.join();
    }

    private void updateTimeAndBroadcastInfo(){
        Pass targetPass;
        stationsLeft = 0;
        for (int i = 0; i < passes.size(); i++){
            Pass pass = passes.get(i);
            if(pass.getYourStop().equals("your Stop")){
                targetPass = pass;
                targetTime = targetPass.getExpectedDepartureTimeAsTime();
                status = targetPass.getTripStopStatus();
                //System.out.println("new tt "+targetTime.toString());
                if(!(targetTime.equals(oldTargetTime))){
                    oldTargetTime = targetTime;
                    break;
                }else {
                    break;
                }
            }else {
                if((pass.getTripStopStatus().equals("DRIVING"))||(pass.getTripStopStatus().equals("UNKNOWN"))){
                    stationsLeft += 1;
                }
            }
        }
        doAPIhaptics();
        //make notification!
        broadcastPasses();
    }

    private void doAPIhaptics(){
        if(((stationsLeft<3)&&(stationsLeft!=oldStationsLeft))){
            //System.out.println("The number of stations left: "+stationsLeft);
            doVibrate(1000, 10, 0);
            oldStationsLeft = stationsLeft;
        }else if((status.equals("ARRIVED")||status.equals("PASSED"))&&(!status.equals(oldStatus))){
            System.out.println("Haptics on: "+status);
            doVibrate(1000, 10, 0);
            oldStatus = status;
            if(status.equals("PASSED")){
                decision="Too late";
            }else if(status.equals("ARRIVED")){
                decision="RUN";
            }
        }
    }

    public void broadcastPasses(){
        Intent i = new Intent("passes_update");
        i.putExtra("passes",passes);
        i.putExtra("stationsLeft",stationsLeft);
        sendBroadcast(i);
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
                updateTimeAndBroadcastInfo();
            }catch (IOException | InterruptedException | JSONException e){
                e.printStackTrace();
            }
        }
    }

    public void getSelectedBusInfo(Bus bus) throws IOException, InterruptedException, JSONException {
        ParsingJSON parseTheJSON = new ParsingJSON();
        OVapi oVapi = new OVapi();
        oVapi.doRequestByJourney(bus.getDataOwnerCode(), bus.getLocalServiceLevelCode(), bus.getLinePlanningNumber(), bus.getJourneyNumber(), bus.getFortifyOrderNumber());
        String JSONjrnToParse = oVapi.getJourneyResponse();
        parseTheJSON.parseJourneyString(bus,JSONjrnToParse);
        passes = parseTheJSON.passes;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy");
        stopForeground(true);
        stopSelf();
        timer.cancel();
        timer.purge();
        //System.out.println("release WL " +wl.isHeld());
        //Log.d(TAG, "Wakelock released");
        wl.release();
        calculateDist = false;
        //System.out.println("release WL " +wl.isHeld());
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        //System.out.println(LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.SECONDS.between(now, exp);
    }

    public void hapticVibrate(int x, int y, int z){
        if(decision != oldDecision){
            long[] pattern = {0, x, y, z};
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(pattern,-1));
            } else {
                v.vibrate(pattern,-1);
            }
        }
    }

    private void doVibrate(int x, int y, int z){
        long[] pattern = {0, x, y, z};
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(pattern,-1));
        } else {
            v.vibrate(pattern,-1);
        }
    }

    private int [] convertStringToTimeInt(String timeString){
        String [] parts = timeString.split(":");
        int [] result = new int[2];
        for (int i = 0; i < parts.length; i++){
            String part = parts[i];
            int partInt = Integer.valueOf(part);
            result[i] = partInt;
        }
        return result;
    }

    public String doHaptic(double dist, long diff) {
        if(distance<12){
            hapticVibrate(1000, 10, 0);
            decision = "You have arrived";
            oldDecision = decision;
            return decision;
        }

        dist = dist * 1.41421;

        //double mhLatDist = distance(myLatitude,0,targetLatitude,0);
        //double mhLonDist = distance(0, myLongitude,0, targetLongitude);
        //dist = mhLatDist+mhLonDist;

        //System.out.println("true dist "+ distance);
        //System.out.println("1.5 dist "+ distance*1.5);
        //System.out.println("MH dist "+dist);


        double speed = dist/diff;
        // System.out.println("You need a speed of: " + speed + " m/s to get to the bus on time.");
        if(speed >= 0 && speed < 1.0) {
            decision = "You have time";
            hapticVibrate(1000, 1000, 500);
            oldDecision = decision;
        }
        else if(speed >= 1.0 && speed < 1.45) {
            decision = "Walk Normally";
            hapticVibrate(1000, 1000, 500);
            oldDecision = decision;
        }
        else if(speed >= 1.45 && speed < 1.8) {
            decision = "Walk Faster";
            hapticVibrate(1000, 1000, 500);
            oldDecision = decision;
        }
        else if(speed >= 1.8  && speed < 2.5) {
            decision = "RUN!";
            hapticVibrate(1000, 1000, 500);
            oldDecision = decision;
        }
        else if(speed >= 2.5) {
            decision = "SPRINT";
            hapticVibrate(1000, 1000, 500);
            oldDecision = decision;
        }
        else if(speed < 0){
            decision = "Too late";
            hapticVibrate(1000, 1000, 500);
            oldDecision = decision;
            stopLoop = true;
        }
        return decision;
    }

}