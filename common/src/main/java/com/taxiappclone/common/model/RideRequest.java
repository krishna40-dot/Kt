package com.taxiappclone.common.model;

public class RideRequest {
    private String ride_id,
            rider_id,
            driver_id,
            rider_location,
            driver_location,
            pickup_location,
            dropoff_location,
            pickup_address,
            dropoff_address,
            ride_status,
            reason,
            payment_status,
            fare_status,
            fare,
            request_time,
            accept_time,
            reject_time,
            cancel_time,
            arrived_time,
            pickup_time,
            start_time,
            end_time,
            scheduled,
            driver_gst,
            mbs_gst,
            scheduled_time;

    public RideRequest() {
    }

    public RideRequest(String ride_id, String rider_id, String driver_id, String rider_location, String driver_location, String pickup_location, String dropoff_location, String pickup_address, String dropoff_address, String ride_status, String reason, String payment_status, String fare_status, String fare, String request_time, String accept_time, String reject_time, String cancel_time, String arrived_time, String pickup_time, String start_time, String end_time, String scheduled, String driver_gst, String mbs_gst, String scheduled_time) {
        this.ride_id = ride_id;
        this.rider_id = rider_id;
        this.driver_id = driver_id;
        this.rider_location = rider_location;
        this.driver_location = driver_location;
        this.pickup_location = pickup_location;
        this.dropoff_location = dropoff_location;
        this.pickup_address = pickup_address;
        this.dropoff_address = dropoff_address;
        this.ride_status = ride_status;
        this.reason = reason;
        this.payment_status = payment_status;
        this.fare_status = fare_status;
        this.fare = fare;
        this.request_time = request_time;
        this.accept_time = accept_time;
        this.reject_time = reject_time;
        this.cancel_time = cancel_time;
        this.arrived_time = arrived_time;
        this.pickup_time = pickup_time;
        this.start_time = start_time;
        this.end_time = end_time;
        this.scheduled = scheduled;
        this.driver_gst = driver_gst;
        this.mbs_gst = mbs_gst;
        this.scheduled_time = scheduled_time;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getRider_id() {
        return rider_id;
    }

    public void setRider_id(String rider_id) {
        this.rider_id = rider_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getRider_location() {
        return rider_location;
    }

    public void setRider_location(String rider_location) {
        this.rider_location = rider_location;
    }

    public String getDriver_location() {
        return driver_location;
    }

    public void setDriver_location(String driver_location) {
        this.driver_location = driver_location;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getDropoff_location() {
        return dropoff_location;
    }

    public void setDropoff_location(String dropoff_location) {
        this.dropoff_location = dropoff_location;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getDropoff_address() {
        return dropoff_address;
    }

    public void setDropoff_address(String dropoff_address) {
        this.dropoff_address = dropoff_address;
    }

    public String getRide_status() {
        return ride_status;
    }

    public void setRide_status(String ride_status) {
        this.ride_status = ride_status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getFare_status() {
        return fare_status;
    }

    public void setFare_status(String fare_status) {
        this.fare_status = fare_status;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }

    public String getAccept_time() {
        return accept_time;
    }

    public void setAccept_time(String accept_time) {
        this.accept_time = accept_time;
    }

    public String getReject_time() {
        return reject_time;
    }

    public void setReject_time(String reject_time) {
        this.reject_time = reject_time;
    }

    public String getCancel_time() {
        return cancel_time;
    }

    public void setCancel_time(String cancel_time) {
        this.cancel_time = cancel_time;
    }

    public String getArrived_time() {
        return arrived_time;
    }

    public void setArrived_time(String arrived_time) {
        this.arrived_time = arrived_time;
    }

    public String getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public String getScheduled_time() {
        return scheduled_time;
    }

    public void setScheduled_time(String scheduled_time) {
        this.scheduled_time = scheduled_time;
    }

    public String getDriver_gst() {
        return driver_gst;
    }

    public void setDriver_gst(String driver_gst) {
        this.driver_gst = driver_gst;
    }

    public String getMbs_gst() {
        return mbs_gst;
    }

    public void setMbs_gst(String mbs_gst) {
        this.mbs_gst = mbs_gst;
    }
}