package com.example.catchthebus;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;

public class Bus implements Comparable<Bus>, Serializable {

    /*
     * time string format: 2020-03-05T06:27:00
     */
    Stop stop;
    String expectedArrivalTime;
    String expectedDepartureTime;
    String destinationCode50;
    String lineName;
    String linePublicNumber;
    int fortifyOrderNumber;
    double latitude;
    double lineDirection;
    double longitude;
    String targetArrivalTime;
    String targetDepartureTime;
    String timingPointCode;
    int localServiceLevelCode;
    String linePlanningNumber;
    int journeyNumber;
    String busMetadata;
    boolean isTimingStop;
    //LocalDateTime arrivalDelay;
    //LocalDateTime departureDelay;
    LocalDateTime expectedArrivalTimeAsTime;
    LocalDateTime targetArrivalTimeAsTime;
    LocalDateTime targetDepartureTimeAsTime;
    LocalDateTime expectedDepartureTimeAsTime;

    Pass pass;
    ArrayList<Pass> passes = new ArrayList<Pass>();

    public Bus() {
        // TODO Auto-generated constructor stub
    }

    public void set_destinationCode50(String destinationCode50) {
        this.destinationCode50 = destinationCode50;
    }

    public String get_destinationCode50() {
        return destinationCode50;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public Stop getStop() {
        return stop;
    }

    public String getBusMetadata() {
        return busMetadata;
    }



    public void setBusMetadata(String busMetadata) {
        this.busMetadata = busMetadata;
    }



    public Pass getPass() {
        return pass;
    }



    public void setPass(Pass pass) {
        this.pass = pass;
    }



    public ArrayList<Pass> getPasses() {
        return passes;
    }



    public void setPasses(ArrayList<Pass> passes) {
        this.passes = passes;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLinePublicNumber() {
        return linePublicNumber;
    }

    public void setLinePublicNumber(String linePublicNumber) {
        this.linePublicNumber = linePublicNumber;
    }

    public void setExpectedArrivalTime(String expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(expectedArrivalTime, formatter);
        setExpectedArrivalTimeAsTime(dateTime);
    }

    public void setExpectedDepartureTime(String expectedDepartureTime) {
        this.expectedDepartureTime = expectedDepartureTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(expectedDepartureTime, formatter);
        setExpectedDepartureTimeAsTime(dateTime);
    }

    public void setTargetArrivalTime(String targetArrivalTime) {
        this.targetArrivalTime = targetArrivalTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(targetArrivalTime, formatter);
        setTargetArrivalTimeAsTime(dateTime);
    }

    public void setTargetDepartureTime(String targetDepartureTime) {
        this.targetDepartureTime = targetDepartureTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(targetDepartureTime, formatter);
        setTargetDepartureTimeAsTime(dateTime);
    }

    public boolean isTimingStop() {
        return isTimingStop;
    }

    public void setTimingStop(boolean isTimingStop) {
        this.isTimingStop = isTimingStop;
    }

    String dataOwnerCode;

    public String getDataOwnerCode() {
        return dataOwnerCode;
    }

    public void setDataOwnerCode(String dataOwnerCode) {
        this.dataOwnerCode = dataOwnerCode;
    }

    public String getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public String getExpectedDepartureTime() {
        return expectedDepartureTime;

    }

    public int getFortifyOrderNumber() {
        return fortifyOrderNumber;
    }

    public void setFortifyOrderNumber(int fortifyOrderNumber) {
        this.fortifyOrderNumber = fortifyOrderNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLineDirection() {
        return lineDirection;
    }

    public void setLineDirection(double lineDirection) {
        this.lineDirection = lineDirection;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTargetArrivalTime() {
        return targetArrivalTime;
    }

    public String getTargetDepartureTime() {
        return targetDepartureTime;
    }

    public String getTimingPointCode() {
        return timingPointCode;
    }

    public void setTimingPointCode(String timingPointCode) {
        this.timingPointCode = timingPointCode;
    }

    public int getLocalServiceLevelCode() {
        return localServiceLevelCode;
    }

    public void setLocalServiceLevelCode(int localServiceLevelCode) {
        this.localServiceLevelCode = localServiceLevelCode;
    }

    public String getLinePlanningNumber() {
        return linePlanningNumber;
    }

    public void setLinePlanningNumber(String linePlanningNumber) {
        this.linePlanningNumber = linePlanningNumber;
    }

    public int getJourneyNumber() {
        return journeyNumber;
    }

    public void setJourneyNumber(int journeyNumber) {
        this.journeyNumber = journeyNumber;
    }

    public String getBus_metadata() {
        return busMetadata;
    }

    public void setBus_metadata(String busMetadata) {
        this.busMetadata = busMetadata;
    }

    public void appendBus_metadata(String append) {
        this.busMetadata = busMetadata += append;
    }

    public LocalDateTime getExpectedArrivalTimeAsTime() {
        return expectedArrivalTimeAsTime;
    }

    public String getExpectedArrivalTimeAsTimeString() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy").format(expectedArrivalTimeAsTime);
    }

    public void setExpectedArrivalTimeAsTime(LocalDateTime expectedArrivalTimeAsTime) {
        this.expectedArrivalTimeAsTime = expectedArrivalTimeAsTime;
    }

    public LocalDateTime getTargetArrivalTimeAsTime() {
        return targetArrivalTimeAsTime;
    }

    public String getTargetArrivalTimeAsTimeString() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy").format(targetArrivalTimeAsTime);
    }

    public void setTargetArrivalTimeAsTime(LocalDateTime targetArrivalTimeAsTime) {
        this.targetArrivalTimeAsTime = targetArrivalTimeAsTime;
    }

    public LocalDateTime getTargetDepartureTimeAsTime() {
        return targetDepartureTimeAsTime;
    }

    public String getTargetDepartureTimeAsTimeString() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy").format(targetDepartureTimeAsTime);
    }

    public void setTargetDepartureTimeAsTime(LocalDateTime targetDepartureTimeAsTime) {
        this.targetDepartureTimeAsTime = targetDepartureTimeAsTime;
    }

    public LocalDateTime getExpectedDepartureTimeAsTime() {
        return expectedDepartureTimeAsTime;
    }

    public String getExpectedDepartureTimeAsTimeString() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy").format(expectedDepartureTimeAsTime);
    }

    //System.out.println(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM").format(getExpectedDepartureTimeAsTime()));



    public void setExpectedDepartureTimeAsTime(LocalDateTime expectedDepartureTimeAsTime) {
        this.expectedDepartureTimeAsTime = expectedDepartureTimeAsTime;
    }

    @Override
    public int compareTo(Bus arg0) {
        if (getExpectedDepartureTimeAsTime() == null || arg0.getExpectedDepartureTimeAsTime() == null)
            return 0;
        return getExpectedDepartureTimeAsTime().compareTo(arg0.getExpectedDepartureTimeAsTime());
    }

	/*public LocalDateTime getArrivalDelay() {
		return arrivalDelay;
	}

	public void setArrivalDelay(LocalDateTime arrivalDelay) {
		arrivalDelay = expectedArrivalTimeAsTime - targetArrivalTimeAsTime;
		this.arrivalDelay = arrivalDelay;
	}

	public LocalDateTime getDepartureDelay() {
		return departureDelay;
	}

	public void setDepartureDelay(LocalDateTime departureDelay) {
		this.departureDelay = departureDelay;
	}*/
}

