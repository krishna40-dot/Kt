package com.taxiappclone.common.app;

public class Constants {

	public static String CURRENCY_SYMBOL = "₹";
	public static final int SUCCESS_RESULT = 0;
	public static final int FAILURE_RESULT = 1;

	public static final int USE_ADDRESS_NAME = 1;
	public static final int USE_ADDRESS_LOCATION = 2;

	public static final String CAR_SEDAN = "sedan";
	public static final String CAR_MINI = "mini";
	public static final String CAR_MICRO = "micro";

	public static final String ON_DUTY = "on_duty";//For Driver
	public static final String ON_RIDE = "on_ride";//For Rider
	public static final String DRIVER_ONLINE = "online";
    public static final String MSG_TYPE = "type";
	public static final String RIDE_REQUEST = "ride_request";
	public static final String RIDER_TOKEN = "rider_token";
	public static final String DRIVER_TOKEN = "driver_token";
	public static final float MAP_ZOOM = 15.0f;

	//Ride Constants
	public static final String RIDE_ID = "ride_id";
	public static final String RIDER_ID = "rider_id";
	public static final String DRIVER_ID = "driver_id";
	public static final String RIDER_LOCATION = "rider_location";
	public static final String DRIVER_LOCATION = "driver_location";
	public static final String RIDE_PICKUP_LOCATION = "pickup_location";
	public static final String RIDE_DROPOFF_LOCATION = "dropff_location";
	public static final String RIDE_PICKUP_ADDRESS = "pickup_address";
	public static final String RIDE_DROPOFF_ADDRESS = "dropoff_address";
	public static final String RIDE_STATUS = "ride_status";
	public static final String RIDE_CANCEL_REASON = "reason";
	public static final String RIDE_PAYMENT_STATUS = "payment_status";
	public static final String RIDE_FARE_STATUS = "fare_status";
	public static final String RIDE_FARE = "fare";
	public static final String RIDE_REQUEST_TIME = "request_time";
	public static final String RIDE_ACCEPT_TIME = "accept_time";
	public static final String RIDE_REJECT_TIME = "reject_time";
	public static final String RIDE_CANCEL_TIME = "cancel_time";
	public static final String DRIVER_ARRIVED_TIME = "arrived_time";
	public static final String RIDE_START_TIME = "start_time";
	public static final String RIDE_END_TIME = "end_time";
	public static final String RIDE_SCHEDULED = "scheduled";
	public static final String RIDE_SCHEDULED_TIME = "scheduled_time";

	//Ride Status Constants
	public static final String RIDE_REQUESTED = "requested";
	public static final String RIDE_ACCEPTED = "accepted";
	public static final String RIDE_REJECTED = "rejected";
	public static final String RIDE_CANCELLED_BY_DRIVER = "cancelled_by_driver";
	public static final String RIDE_CANCELLED_BY_RIDER = "cancelled_by_rider";
	public static final String RIDE_STARTED = "started";
    public static final String RIDE_FINISHED = "finished";
	public static final String RIDE_RUNNING = "running";
	public static final String RIDE_REACHED_DESTINATION = "reached_destination";
	public static final String RIDE_COMPLETED = "completed";
	public static final String RIDE_DRIVER_ARRIVED = "driver_arrived";
}