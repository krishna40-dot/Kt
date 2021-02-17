package com.taxiappclone.common.model;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Adee on 3/24/2018.
 */

public class FbDriver {
    private String model;
    private int vehicle_type;
    private double angle;
    private int on_duty;
    private int online;
    private String token;
    private String image;
    private String mobile;
    private String gender;
    private String name;
    private int id;
    private Marker marker;

    public int getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(int vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getOn_duty() {
        return on_duty;
    }

    public void setOn_duty(int on_duty) {
        this.on_duty = on_duty;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}