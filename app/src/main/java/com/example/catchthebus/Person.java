package com.example.catchthebus;

import android.location.LocationListener;
import android.location.LocationManager;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Person implements Serializable {
    double latitude;
    double longitude;
    LocalDateTime currentTime;
    private LocationManager locationManager;
    private LocationListener listener;

    public Person() {

    }

    public double getLatitude() {
        return latitude;
    }

    public String getLatitudeAsString(){
        return Double.toString(latitude);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
