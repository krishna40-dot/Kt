package com.taxiappclone.common.model;

/**
 * Created by Adee on 15/3/2018.
 */

public class MyRidesListItem {
    private int rideId;
    private String pickUpAddress, dropOffAddress, status, pickUpDate, distance;
    private double amount;

    public MyRidesListItem(int rideId, String pickUpAddress, String dropOffAddress, double amount, String status, String pickUpDate, String distance)
    {
        this.rideId = rideId;
        this.pickUpAddress = pickUpAddress;
        this.dropOffAddress = dropOffAddress;
        this.amount = amount;
        this.status = status;
        this.pickUpDate = pickUpDate;
        this.distance = distance;
    }

    public int getRideId() {
        return rideId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public String getStatus() {
        return status;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public void setDropOffAddress(String dropOffAddress) {
        this.dropOffAddress = dropOffAddress;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}