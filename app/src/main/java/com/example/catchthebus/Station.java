package com.example.catchthebus;

import java.io.Serializable;

public class Station implements Serializable {

    double latitude;
    double longitude;
    String timingPointCode;
    String stationMetadata;
    String timingPointName;

    public Station() {
        // TODO Auto-generated constructor stub
    }

    public String getTimingPointName() {
        return timingPointName;
    }

    public void setTimingPointName(String timingPointName) {
        this.timingPointName = timingPointName;
    }

    public double getLatitude() {
        return latitude;
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

    public String getTimingPointCode() {
        return timingPointCode;
    }

    public void setTimingPointCode(String timingPointCode) {
        this.timingPointCode = timingPointCode;
    }

    public String getStationMetadata() {
        return stationMetadata;
    }

    public void setStationMetadata(String stationMetadata) {
        this.stationMetadata = stationMetadata;
    }
}
