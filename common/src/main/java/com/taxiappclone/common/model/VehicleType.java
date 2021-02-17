package com.taxiappclone.common.model;

public class VehicleType {

    private boolean available;
    private boolean selected;
    private String modified_at;
    private String created_at;
    private int status;
    private double discount;
    private double tax;
    private double waiting_charge;
    private double cancellation_charge;
    private double per_min_charge;
    private double per_km_charge;
    private double base_fare;
    private int capacity;
    private String countries;
    private String image;
    private String type;
    private String name;
    private int id;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getWaiting_charge() {
        return waiting_charge;
    }

    public void setWaiting_charge(double waiting_charge) {
        this.waiting_charge = waiting_charge;
    }

    public double getCancellation_charge() {
        return cancellation_charge;
    }

    public void setCancellation_charge(double cancellation_charge) {
        this.cancellation_charge = cancellation_charge;
    }

    public double getPer_min_charge() {
        return per_min_charge;
    }

    public void setPer_min_charge(double per_min_charge) {
        this.per_min_charge = per_min_charge;
    }

    public double getPer_km_charge() {
        return per_km_charge;
    }

    public void setPer_km_charge(double per_km_charge) {
        this.per_km_charge = per_km_charge;
    }

    public double getBase_fare() {
        return base_fare;
    }

    public void setBase_fare(double base_fare) {
        this.base_fare = base_fare;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "VehicleType{" +
                "available=" + available +
                ", selected=" + selected +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}