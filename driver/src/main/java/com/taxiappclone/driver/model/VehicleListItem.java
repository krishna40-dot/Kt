package com.taxiappclone.driver.model;

/**
 * Created by Adee on 9/3/2018.
 */

public class VehicleListItem {
    public int vehicleId, vehicleTypeId, passengerCapacity, luggageCapacity;
    public String vehicleName, vehicleDescription, imgUrl;
    public Double amount;
    public VehicleListItem(int vehicleId, int vehicleTypeId, String vehicleName, String vehicleDescription, String imgUrl, int passengerCapacity, int luggageCapacity, Double amount)
    {
        this.vehicleId = vehicleId;
        this.vehicleTypeId = vehicleTypeId;
        this.vehicleName = vehicleName;
        this.vehicleDescription = vehicleDescription;
        this.imgUrl = imgUrl;
        this.passengerCapacity = passengerCapacity;
        this.luggageCapacity = luggageCapacity;
        this.amount = amount;
    }
}