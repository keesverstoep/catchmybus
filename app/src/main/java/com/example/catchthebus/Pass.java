package com.example.catchthebus;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.Serializable;

public class Pass implements Comparable<Pass>, Serializable {

    LocalDateTime expectedArrivalTimeAsTime;
    LocalDateTime targetArrivalTimeAsTime;
    LocalDateTime targetDepartureTimeAsTime;
    LocalDateTime expectedDepartureTimeAsTime;
    String expectedArrivalTime;
    String expectedDepartureTime;
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
    String passMetadata;
    boolean isTimingStop;

    int orderInLine;
    String timingPointName;
    String timingPointTown;
    String timingPointDataOwner;
    String journeyStopType;
    String tripStopStatus;
    String userStopCode;
    String transportType;

    Bus bus;

    String yourStop;

    public void setYourStop(String yourStop) {
        this.yourStop = yourStop;
    }

    public String getYourStop() {
        return yourStop;
    }


    public LocalDateTime getExpectedArrivalTimeAsTime() {
        return expectedArrivalTimeAsTime;
    }




    public void setExpectedArrivalTimeAsTime(LocalDateTime expectedArrivalTimeAsTime) {
        this.expectedArrivalTimeAsTime = expectedArrivalTimeAsTime;
    }




    public LocalDateTime getTargetArrivalTimeAsTime() {
        return targetArrivalTimeAsTime;
    }




    public void setTargetArrivalTimeAsTime(LocalDateTime targetArrivalTimeAsTime) {
        this.targetArrivalTimeAsTime = targetArrivalTimeAsTime;
    }




    public LocalDateTime getTargetDepartureTimeAsTime() {
        return targetDepartureTimeAsTime;
    }




    public void setTargetDepartureTimeAsTime(LocalDateTime targetDepartureTimeAsTime) {
        this.targetDepartureTimeAsTime = targetDepartureTimeAsTime;
    }




    public LocalDateTime getExpectedDepartureTimeAsTime() {
        return expectedDepartureTimeAsTime;
    }




    public void setExpectedDepartureTimeAsTime(LocalDateTime expectedDepartureTimeAsTime) {
        this.expectedDepartureTimeAsTime = expectedDepartureTimeAsTime;
    }




    public String getExpectedArrivalTime() {
        return expectedArrivalTime;
    }









    public String getExpectedDepartureTime() {
        return expectedDepartureTime;
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

    public void appendPass_metadata(String append) {
        this.passMetadata = passMetadata += append;
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




    public String getPassMetadata() {
        return passMetadata;
    }




    public void setPassMetadata(String busMetadata) {
        this.passMetadata = busMetadata;
    }




    public boolean isTimingStop() {
        return isTimingStop;
    }




    public void setTimingStop(boolean isTimingStop) {
        this.isTimingStop = isTimingStop;
    }




    public int getOrderInLine() {
        return orderInLine;
    }




    public void setOrderInLine(int orderInLine) {
        this.orderInLine = orderInLine;
    }




    public String getTimingPointName() {
        return timingPointName;
    }




    public void setTimingPointName(String timingPointName) {
        this.timingPointName = timingPointName;
    }




    public String getTimingPointTown() {
        return timingPointTown;
    }




    public void setTimingPointTown(String timingPointTown) {
        this.timingPointTown = timingPointTown;
    }




    public String getTimingPointDataOwner() {
        return timingPointDataOwner;
    }




    public void setTimingPointDataOwner(String timingPointDataOwner) {
        this.timingPointDataOwner = timingPointDataOwner;
    }




    public String getJourneyStopType() {
        return journeyStopType;
    }




    public void setJourneyStopType(String journeyStopType) {
        this.journeyStopType = journeyStopType;
    }




    public String getTripStopStatus() {
        return tripStopStatus;
    }




    public void setTripStopStatus(String tripStopStatus) {
        this.tripStopStatus = tripStopStatus;
    }




    public String getUserStopCode() {
        return userStopCode;
    }




    public void setUserStopCode(String userStopCode) {
        this.userStopCode = userStopCode;
    }




    public String getTransportType() {
        return transportType;
    }




    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Pass() {
        // TODO Auto-generated constructor stub
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


    public String getExpectedDepartureTimeAsTimeString() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy").format(expectedDepartureTimeAsTime);
    }

    public String getTargetDepartureTimeAsTimeString() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy").format(targetDepartureTimeAsTime);
    }


    @Override
    public int compareTo(Pass arg0) {
        if (getExpectedArrivalTimeAsTime() == null || arg0.getExpectedArrivalTimeAsTime() == null)
            return 0;
        return getExpectedArrivalTimeAsTime().compareTo(arg0.getExpectedArrivalTimeAsTime());
    }


}

