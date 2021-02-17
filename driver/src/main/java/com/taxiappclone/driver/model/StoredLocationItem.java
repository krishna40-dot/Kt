package com.taxiappclone.driver.model;

/**
 * Created by Adee on 1/15/2018.
 */

public class StoredLocationItem {
    public int id;
    public String name, address, addressType, city, countryCode, postalCode;
    public Double latitude, longitude;
    public StoredLocationItem(int id, String addressType, String name, String address, String city, String countryCode, String postalCode, Double latitude, Double longitude)
    {
        this.id = id;
        this.addressType = addressType;
        this.name = name;
        this.address = address;
        this.city = city;
        this.countryCode = countryCode;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}