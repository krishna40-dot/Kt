package com.taxiappclone.common.model;

public class Vehicle {
    private String modified_at;
    private String created_at;
    private int status;
    private String insurance;
    private String police_verification;
    private String registration_card;
    private String expiry_date;
    private String driving_license;
    private String vehicle_number;
    private String vehicle_model;
    private int vehicle_type;
    private int driver_id;
    private int id;

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getPolice() {
        return police_verification;
    }

    public void setPolice(String police) {
        this.police_verification = police;
    }

    public String getRegistration_card() {
        return registration_card;
    }

    public void setRegistration_card(String registration_card) {
        this.registration_card = registration_card;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getDriving_license() {
        return driving_license;
    }

    public void setDriving_license(String driving_license) {
        this.driving_license = driving_license;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    public int getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(int vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
