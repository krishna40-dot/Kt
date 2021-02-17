package com.taxiappclone.common.app;

import android.location.Location;

public class AppConfig {
    public static String LOCAL_DB_NAME = "app_db_driver";
    public static String PREF_NAME = "app_pref_driver";
    public static String CURRENT_USER = "user_data";
    public static String USER = "user";
    public static String USER_VEHICLE = "user_vehicle";
    public static String VEHICLE_DETAILS = "vehicle_details";
    public static String URL_MAIN = "https://mbsindia.co.in/";
    //	public static String URL_MAIN = "http://192.168.43.112/taxiappclone/";

    //Location Update params
    public static final int UPDATE_INTERVAL = 3000;
    public static final int FASTEST_INTERVAL = 2000;
    public static final int DISPLACEMENT = 2;

    // Server user login url
    public static String URL_API = "https://api.mylimobiz.com";
    public static String URL_USER_ADDRESS = URL_API + "/customers/self/addresses";
    public static String URL_RATE_LOOKUP = URL_API + "/companies/supershuttlemo/rate_lookup";
    public static String URL_ADD_CREDIT_CARD = URL_API + "/customers/self/credit_cards";
    public static String URL_GET_CREDIT_CARDS = URL_API + "/customers/self/billing_contacts";
    public static String URL_BOOKING = URL_API + "/companies/supershuttlemo/bookings";
    public static String URL_MY_RIDES = URL_API + "/customers/self/reservations";

    public static String URL_RESET_PASS = URL_API + "customer_reset_password.php";
    public static String URL_SERVER = "https://api.mylimobiz.com";

    public static String GOOGLE_API_URL = "https://maps.googleapis.com";
    public static final String URL_FCM = "https://fcm.googleapis.com/fcm/send";

    //TABLE NAMES
    public static final String TABLE_DRIVERS = "Drivers";
    public static final String TABLE_DRIVERS_ON_DUTY = "DriversOnDuty";
    public static final String TABLE_RIDERS = "Riders";
    public static final String TABLE_RIDERS_LOCATIONS = "RidersLocations";
    public static final String TABLE_PICKUP_REQUESTS = "PickupRequests";
    public static final String TABLE_TOKENS = "FCMTokens";
    public static final String TABLE_DRIVERS_AVAILABLE = "DriversLocations";

    //GLOBAL VARIABLES
    public static Location mLastLocation = null;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    //REQUEST CODES
    public static final int MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    public static final int CONTACT_PERMISSION_REQUEST_CODE = 2;
    public static final int SMS_PERMISSION_REQUEST_CODE = 3;
    public static final int PLAN_SELECT = 4;
    public static final int DEVICE_ID_PERMISSION_REQUEST_CODE = 5;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 6;

    public static final int ADD_LOCATION = 7;
    public static final int SELECT_ADDRESS = 8;
    public static final int SELECT_SOURCE = 9;
    public static final int SELECT_DESTINATION = 10;
    public static final int RIDE_ACCEPTED = 11;

    public static final int SELECT_FILE = 1001;
    public static final int REQUEST_CAMERA = 1002;
    public static final int SELECT_PHOTO = 1003;
    public static final int SELECT_DRIVING_LICENSE = 1004;
    public static final int SELECT_RC = 1005;
    public static final int SELECT_INSURANCE = 1006;
    public static final int SELECT_POLICE = 1007;
	/*public static String selectedImageString = "";
	public static Bitmap selectedBitmap = null;*/
}