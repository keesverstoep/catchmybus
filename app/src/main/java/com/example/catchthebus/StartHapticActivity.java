package com.example.catchthebus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class StartHapticActivity extends AppCompatActivity {

    Button startHaptic;
    Button stopHaptic;
    Button maps;
    Button journeyInfo;
    TextView yourBus;
    TextView yourTime;
    Person me;
    Bus bus;
    Stop stop;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_haptic);
        AndroidThreeTen.init(this);
        try {
            start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    me.setLatitude(intent.getDoubleExtra("myLat",0));
                    me.setLongitude(intent.getDoubleExtra("myLong",0));
                    //System.out.println("Lat "+myLat);
                    //System.out.println("Long "+myLong);
                    //myLat = 0;
                    //myLong = 0;
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


    public void start() throws InterruptedException {
        Intent i = getIntent();
        me = (Person) i.getSerializableExtra("me");
        bus = (Bus) i.getSerializableExtra("bus");
        stop = (Stop) i.getSerializableExtra("stop");
        startHaptic = (Button) findViewById(R.id.startHaptic);
        stopHaptic = (Button) findViewById(R.id.stopHaptic);
        journeyInfo = (Button) findViewById(R.id.showInfo);
        setTextView(bus,stop);
        enable_buttons();
        enable_info_and_maps_buttons();
    }

    private void setTextView(Bus bus, Stop stop){
        yourBus = (TextView)findViewById(R.id.busInfoHere);
        yourTime = (TextView)findViewById(R.id.timeInfoHere);
        yourBus.setText(bus.getLinePublicNumber()+" to "+bus.get_destinationCode50()+" from "+stop.getStop());
        yourTime.setText("Departure time: "+bus.getExpectedDepartureTimeAsTimeString());

    }

    private void enable_buttons() {
        startHaptic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //enable_info_and_journey_buttons();
                boolean sendInfo = true;
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                //So put bus object here, maybe also station object... see structure.
                // enable buttons on gps receival
                i.putExtra("sentInfo",sendInfo);
                i.putExtra("bus",bus);
                i.putExtra("stop",stop);
                i.putExtra("me",me);
                startService(i);
            }
        });

        stopHaptic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                stopService(i);
            }
        });
    }

    private void enable_info_and_maps_buttons(){
        journeyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),BusActivity.class);
                i.putExtra("me",me);
                i.putExtra("bus",bus);
                i.putExtra("stop",stop);
                startActivity(i);
            }
        });

        /*maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                i.putExtra("me",me);
                i.putExtra("bus",bus);
                i.putExtra("stop",stop);
                startActivity(i);
            }
        });*/
    }



}