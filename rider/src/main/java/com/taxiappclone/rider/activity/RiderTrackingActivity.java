/**
 * Author: Adee
 * facebook: http://facebook.com/ideal.adee
 **/
package com.taxiappclone.rider.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.model.Driver;
import com.taxiappclone.common.model.RideRequest;
import com.taxiappclone.common.notification.SendPushNotification;
import com.taxiappclone.common.remote.IGoogleAPI;
import com.taxiappclone.rider.R;
import com.taxiappclone.common.custom.CustomRiderInfoWindow;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.notification.model.Data;
import com.taxiappclone.common.notification.model.FCMMessage;
import com.taxiappclone.rider.notification.NotificationUtils;
import com.taxiappclone.common.remote.Common;
import com.taxiappclone.common.utils.PermissionManager;
import com.taxiappclone.common.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.taxiappclone.common.app.AppConfig.SELECT_DESTINATION;
import static com.taxiappclone.common.app.AppConfig.SELECT_SOURCE;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_ON_DUTY;
import static com.taxiappclone.common.app.AppConfig.TABLE_PICKUP_REQUESTS;
import static com.taxiappclone.common.app.AppConfig.TABLE_RIDERS_LOCATIONS;
import static com.taxiappclone.common.app.CommonUtils.getDateTimeString;
import static com.taxiappclone.common.app.CommonUtils.setStatusBarGradient;
import static com.taxiappclone.common.app.Constants.RIDE_FINISHED;
import static com.taxiappclone.rider.app.CommonUtils.showToast;
import static com.taxiappclone.common.app.Constants.DRIVER_ID;
import static com.taxiappclone.common.app.Constants.DRIVER_TOKEN;
import static com.taxiappclone.common.app.Constants.MAP_ZOOM;
import static com.taxiappclone.common.app.Constants.ON_RIDE;
import static com.taxiappclone.common.app.Constants.RIDE_ACCEPTED;
import static com.taxiappclone.common.app.Constants.RIDE_CANCELLED_BY_DRIVER;
import static com.taxiappclone.common.app.Constants.RIDE_CANCELLED_BY_RIDER;
import static com.taxiappclone.common.app.Constants.RIDE_CANCEL_REASON;
import static com.taxiappclone.common.app.Constants.RIDE_CANCEL_TIME;
import static com.taxiappclone.common.app.Constants.RIDE_COMPLETED;
import static com.taxiappclone.common.app.Constants.RIDE_DRIVER_ARRIVED;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REACHED_DESTINATION;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;
import static com.taxiappclone.common.app.Constants.RIDE_STARTED;
import static com.taxiappclone.common.app.Constants.RIDE_STATUS;
import static com.taxiappclone.rider.notification.NotificationUtils.PUSH_NOTIFICATION;
import static com.taxiappclone.rider.notification.NotificationUtils.TYPE_NOTIFICATION;

public class RiderTrackingActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {
    private static final String TAG = RiderTrackingActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FragmentDrawer drawerFragment;
    private SessionManager session;
    private ActionBar actionBar;

    //Views
    private ImageButton btnMyLocation;
    private CircleImageView imgDriver;
    private Button btnChangePickup, btnChangeDropOff;
    private LatLng latLngPickup, latLngDropOff;
    private Marker mSourceMarker, mDestinationMarker;
    private View viewCancel;
    private TextView tvPickup, tvDropOff, tvDriverName, tvDriverRating, tvPaymentType, tvAmount, tvCarType, tvDistance;

    //Map
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 1;
    private static String SET_PICKUP_LOCATION = "Set PickUp Location";
    private static String SET_DROPOFF_LOCATION = "Set DropOff Location";

    private PermissionManager permissionManager;
    private SupportMapFragment mapFragment;

    private Double mDistance;
    private int mDuration;
    String mPickupAddress, mDropOffAddress;
    private List<LatLng> polyLineList;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;
    private IGoogleAPI mService;

    private ProgressDialog pDialog;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private boolean closeApp = true;
    DatabaseReference ref;
    GeoFire geoFire;
    private int radius = 1;
    private BitmapDescriptor carIcon;

    private String userId, driverId, rideId, driverToken;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Driver driver;
    private LatLng mDriverLocation;
    private boolean mLocationUpdate, isDriverComing = true;
    private DatabaseReference refRideRequest;
    private RideRequest ride;
    private Gson gson;
    private View btnCancel;
    private String myToken, rideStatus;
    private DatabaseReference refDriversLocation;
    private ValueEventListener driverLocationListener;
    private boolean rideFetched = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_tracking);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("On Ride");

        setStatusBarGradient(this);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_ride_info_drawer);
        drawerFragment.setUp(R.id.fragment_ride_info_drawer, drawerLayout, toolbar);

        View drawer = drawerFragment.getLayout();
        tvDriverName = drawer.findViewById(R.id.tv_driver_name);
        tvDriverRating = drawer.findViewById(R.id.tv_driver_rating);
        tvPickup = drawer.findViewById(R.id.tv_pickup_address);
        tvDropOff = drawer.findViewById(R.id.tv_dropoff_address);
        tvPaymentType = drawer.findViewById(R.id.tv_payment_type);
        tvAmount = drawer.findViewById(R.id.tv_amount);
        tvCarType = drawer.findViewById(R.id.tv_car_type);
        btnChangePickup = drawer.findViewById(R.id.btn_change_pick_up);
        btnChangeDropOff = drawer.findViewById(R.id.btn_change_drop_off);
        btnCancel = drawer.findViewById(R.id.view_cancel);

        btnChangePickup.setOnClickListener(this);
        btnChangeDropOff.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionManager = new PermissionManager(this, this);
            permissionManager.getLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnMyLocation = (ImageButton) findViewById(R.id.btn_my_location);
        btnMyLocation.setOnClickListener(this);

        int height = 100;
        int width = 100;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_new);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        carIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myToken = FirebaseInstanceId.getInstance().getToken();
        ref = FirebaseDatabase.getInstance().getReference(TABLE_RIDERS_LOCATIONS);
        geoFire = new GeoFire(ref);

        mService = Common.getGoogleAPI();

        rideId = session.getStringValue(RIDE_ID);
        driverId = session.getStringValue(DRIVER_ID);
        driverToken = session.getStringValue(DRIVER_TOKEN);
        gson = new Gson();
        refRideRequest = FirebaseDatabase.getInstance().getReference(TABLE_PICKUP_REQUESTS).child(userId).child(rideId);
        if (getIntent().hasExtra(RIDE_REQUEST)) {
            ride = gson.fromJson(getIntent().getStringExtra(RIDE_REQUEST), RideRequest.class);
            initRideRequest(ride);
        } else {
            initRideRequest(null);
        }

        /*FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_LOCATION).child(driverId).child("l")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG,"Driver Location Changed:"+dataSnapshot.getValue().toString());
                        GenericTypeIndicator<List<Double>> t = new GenericTypeIndicator<List<Double>>() {};
                        List<Double> latLng = dataSnapshot.getValue(t);
                        mDriverLocation = new LatLng(latLng.get(0),latLng.get(1));
                        setUpLocation();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
    }

    private void initRideRequest(RideRequest ride) {
        if (ride == null) {
            refRideRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        Log.d(TAG, "Ride Request Detail:" + dataSnapshot.getValue().toString());
                        RideRequest ride = dataSnapshot.getValue(RideRequest.class);
                        initRideRequest(ride);
                    } else {
                        session.setValue(ON_RIDE, false);
                        startActivity(new Intent(RiderTrackingActivity.this, HomeActivity.class));
                        finishAffinity();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, databaseError.getMessage());
                }
            });
        } else {
            rideFetched = true;
            this.ride = ride;
            rideStatus = ride.getRide_status();
            latLngPickup = gson.fromJson(ride.getPickup_location(), LatLng.class);
            latLngDropOff = gson.fromJson(ride.getDropoff_location(), LatLng.class);
            mPickupAddress = ride.getPickup_address();
            mDropOffAddress = ride.getDropoff_address();
            tvPickup.setText(mPickupAddress);
            tvDropOff.setText(mDropOffAddress);
            if (!ride.getRide_status().equals(RIDE_ACCEPTED)) {
                isDriverComing = false;
            }
            switch (ride.getRide_status()) {
                case RIDE_ACCEPTED:
                    actionBar.setTitle("Driver is coming");
                    showAlert("Ride Accepted. Your Driver is coming");
                    break;
                case RIDE_STARTED:
                    showToast("Your ride is on the way");
                    break;
                case RIDE_COMPLETED:
                case RIDE_FINISHED:
                    new AlertDialog.Builder(RiderTrackingActivity.this).setMessage("This ride is completed").setPositiveButton(R.string.ok, (dialog, which) -> {
                        Intent intent = new Intent(RiderTrackingActivity.this, FareDetailActivity.class);
                        intent.putExtra(RIDE_REQUEST, gson.toJson(ride));
                        startActivity(intent);
                        finishAffinity();
                    }).create().show();
                    break;
                case RIDE_CANCELLED_BY_DRIVER:
                    rideCancelledByDriver();
                    break;
                case RIDE_CANCELLED_BY_RIDER:
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finishAffinity();
                    break;
                case RIDE_REACHED_DESTINATION:
                    showToast("Reached at destination");
                    break;
            }
            setUpLocation();
        }
    }

    private void showAlert(String msg) {
        new AlertDialog.Builder(this).setMessage(msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_my_location:
                displayLocation(true);
                btnMyLocation.setVisibility(View.GONE);
                break;
            case R.id.btn_change_pick_up:
                intent = new Intent(RiderTrackingActivity.this, SelectPlaceActivity.class);
                startActivityForResult(intent, SELECT_SOURCE);
                break;
            case R.id.btn_change_drop_off:
                intent = new Intent(RiderTrackingActivity.this, SelectPlaceActivity.class);
                startActivityForResult(intent, SELECT_DESTINATION);
                break;
            case R.id.view_cancel:
                drawerLayout.closeDrawer(GravityCompat.START);
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(RiderTrackingActivity.this);
                builder.setTitle("Select Reason");
                String[] reasons = {"Select Reason", "Rider gives incorrect location.", "Rider's behaviour is bad.", "Passenger is not coming", "Passenger is not arriving", "Other"};
                final ArrayAdapter<String> adp = new ArrayAdapter<String>(RiderTrackingActivity.this,
                        android.R.layout.simple_spinner_item, reasons);
                adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_spinner, null);
                builder.setView(dialogView);
                AppCompatSpinner spinner = dialogView.findViewById(R.id.spinner);
                spinner.setAdapter(adp);
                builder.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    sendRideStatus(RIDE_CANCELLED_BY_DRIVER, RIDE_CANCEL_TIME, spinner.getSelectedItem().toString());
                    dialog.dismiss();
                });
                TextInputEditText inputReason = dialogView.findViewById(R.id.inputReason);

                builder.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
                final androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();
                inputReason.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0)
                            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        else
                            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        inputReason.setError(null);
                        if (position > 0) {
                            if (spinner.getSelectedItem().toString().equals("Other")) {
                                inputReason.setVisibility(View.VISIBLE);
                                if (TextUtils.isEmpty(inputReason.getText())) {
                                    inputReason.setError("Please enter your reason");
                                } else {
                                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                }
                            } else {
                                inputReason.setVisibility(View.GONE);
                                dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        } else {
                            inputReason.setVisibility(View.GONE);
                            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                /*builder.setNegativeButton("Yes", (dialog1, which) -> {
                    showDialog();
                    refRideRequest.child(RIDE_STATUS).setValue(RIDE_CANCELLED_BY_RIDER).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            session.setValue(ON_RIDE,false);
                            hideDialog();
                            Data data = new Data();
                            data.setType(RIDE_CANCELLED_BY_RIDER);
                            data.setRide_id(rideId);
                            data.setUser_id(userId);
                            data.setToken(myToken);
                            FCMMessage message = new FCMMessage(data, driverToken);
                            JSONObject messageParams = null;
                            try {
                                messageParams = new JSONObject(gson.toJson(message));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new SendPushNotification(RiderTrackingActivity.this, messageParams, new SendPushNotification.OnResponseListener() {
                                @Override
                                public void onSuccessListener() {
                                    if(rideStatus.equals(RIDE_STARTED)) {
                                        startActivity(new Intent(getApplicationContext(),FareDetailActivity.class));
                                        finishAffinity();
                                    }else{
                                        session.setValue(ON_RIDE,false);
                                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                        finishAffinity();
                                    }
                                    dialog1.dismiss();
                                }

                                @Override
                                public void onFailedListener() {
                                    showAlert(getApplicationContext(),"Some error occurred");
                                }

                                @Override
                                public void onErrorListener() {

                                }
                            });

                        }
                    });
                });
                androidx.appcompat.app.AlertDialog alert = builder.create();
                alert.show();*/
                break;
        }
    }

    private void sendRideStatus(String status, String timeField, String msg) {
        showDialog();
        Map<String, Object> values = new HashMap<>();
        values.put(RIDE_STATUS, status);
        values.put(timeField, getDateTimeString());
        if (!TextUtils.isEmpty(msg))
            values.put(RIDE_CANCEL_REASON, msg);

        refRideRequest.updateChildren(values).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Data data = new Data();
                data.setType(status);
                if (msg != null && !msg.equals("")) {
                    data.setMessage(msg);
                }
                data.setRide_id(rideId);
                data.setUser_id(userId);
                data.setToken(myToken);
                FCMMessage message = new FCMMessage(data, driverToken);
                JSONObject messageParams = null;
                try {
                    messageParams = new JSONObject(gson.toJson(message));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendPushNotification(getApplicationContext(), messageParams, new SendPushNotification.OnResponseListener() {
                    @Override
                    public void onSuccessListener() {
                        hideDialog();
                        Intent intent;
                        if (status.equals(RIDE_CANCELLED_BY_RIDER)) {
                            if (TextUtils.isEmpty(ride.getStart_time())) {
                                session.setValue(ON_RIDE, false);
                                intent = new Intent(getApplicationContext(), HomeActivity.class);
                            } else {
                                intent = new Intent(getApplicationContext(), FareDetailActivity.class);
                                intent.putExtra(RIDE_REQUEST, new Gson().toJson(ride));
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }

                    @Override
                    public void onFailedListener() {

                    }

                    @Override
                    public void onErrorListener() {

                    }
                });
                hideDialog();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void registerBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    String msgType = intent.getStringExtra("msg_type");
                    switch (msgType) {
                        case RIDE_DRIVER_ARRIVED:
                            ride.setRide_status(RIDE_DRIVER_ARRIVED);
                            hideDialog();
                            isDriverComing = false;
                            showAlert("Driver Arrived");
//                            getDirection(mDriverLocation,latLngDropOff,isDriverComing);
                            break;
                        case RIDE_ACCEPTED:
                            ride.setRide_status(RIDE_ACCEPTED);
                            hideDialog();
                            showToast("Ride Request Accepted");
                            break;
                        case RIDE_STARTED:
                            ride.setRide_status(RIDE_STARTED);
                            ride.setStart_time(getDateTimeString());
                            showAlert("Your ride has started.");
                            getDirection(mDriverLocation, latLngDropOff, isDriverComing);
                            break;
                        case RIDE_COMPLETED:
                            ride.setRide_status(RIDE_COMPLETED);
                            ride.setEnd_time(getDateTimeString());
                            intent = new Intent(RiderTrackingActivity.this, FareDetailActivity.class);
                            intent.putExtra(RIDE_REQUEST, gson.toJson(ride));
                            startActivity(intent);
                            finishAffinity();
                            break;
                        case RIDE_CANCELLED_BY_DRIVER:
                            ride.setRide_status(RIDE_CANCELLED_BY_DRIVER);
                            ride.setReason(intent.getStringExtra("message"));
                            hideDialog();
                            rideCancelledByDriver();
                            break;
                        case TYPE_NOTIFICATION:
                            break;
                    }
                }
            }
        };
    }

    private void rideCancelledByDriver() {
        String msg = "";
        if (!TextUtils.isEmpty(ride.getStart_time())) {
            msg = "Your ride has been cancelled by the driver.\nReason: " + ride.getReason() + ".";
        } else {
            msg = "Your ride has been cancelled by the driver.\nReason: " + ride.getReason() + ".\nNo need to worry. You can request a new pickup.";
        }
        new AlertDialog.Builder(RiderTrackingActivity.this).setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!TextUtils.isEmpty(ride.getStart_time())) {
                    Intent intent = new Intent(getApplicationContext(), FareDetailActivity.class);
                    intent.putExtra(RIDE_REQUEST, new Gson().toJson(ride));
                    startActivity(intent);
                    finishAffinity();
                } else {
                    session.setValue(ON_RIDE, false);
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finishAffinity();
                }
            }
        }).setCancelable(false).create().show();
    }

    private float getBearing(LatLng startPosition, LatLng endPosition) {
        double lat = Math.abs(startPosition.latitude - endPosition.latitude);
        double lng = Math.abs(startPosition.longitude - endPosition.longitude);

        if (startPosition.latitude < endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private void getDirection(final LatLng latLngSource, final LatLng latLngDestination, final Boolean isDriverComing) {
        if (polyLineList != null)
            polyLineList.clear();
        if (greyPolyline != null)
            greyPolyline.remove();
        if (blackPolyline != null)
            blackPolyline.remove();
        String requestApi = null;
        try {
            String sourceAddress = latLngSource.latitude + "," + latLngSource.longitude;
            String destinationAddress = latLngDestination.latitude + "," + latLngDestination.longitude;
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + sourceAddress + "&" +
                    "destination=" + destinationAddress + "&" +
                    "key=" + getResources().getString(R.string.google_api_browser_key);
            Log.d("request API", requestApi);
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                Log.d("RESPONSE: ", response.body().toString());
                                JSONObject jObj = new JSONObject(response.body().toString());
                                JSONArray jArr = jObj.getJSONArray("routes");
                                String status = jObj.getString("status");
                                if (status.equals("NOT_FOUND") || jArr.length() == 0) {
                                    showToast("Path Not Found");
                                    mMap.clear();
                                } else {
                                    //Code for deistance & time
                                    setDistance(jArr);
                                    //Code for Path Line
                                    for (int i = 0; i < jArr.length(); i++) {
                                        JSONObject route = jArr.getJSONObject(i);
                                        JSONObject poly = route.getJSONObject("overview_polyline");
                                        String polyline = poly.getString("points");
                                        polyLineList = decodePoly(polyline);
                                    }
                                    //Adjusting bounds
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                    for (LatLng latlng : polyLineList)
                                        builder.include(latlng);

                                    polylineOptions = new PolylineOptions();
                                    polylineOptions.color(Color.GRAY);
                                    polylineOptions.width(10);
                                    polylineOptions.startCap(new SquareCap());
                                    polylineOptions.endCap(new SquareCap());
                                    polylineOptions.jointType(JointType.ROUND);
                                    polylineOptions.addAll(polyLineList);
                                    greyPolyline = mMap.addPolyline(polylineOptions);

                                    blackPolylineOptions = new PolylineOptions();
                                    blackPolylineOptions.color(Color.BLACK);
                                    blackPolylineOptions.width(10);
                                    blackPolylineOptions.startCap(new SquareCap());
                                    blackPolylineOptions.endCap(new SquareCap());
                                    blackPolylineOptions.jointType(JointType.ROUND);
                                    blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                    if (mSourceMarker != null)
                                        mSourceMarker.remove();
                                    if (!isDriverComing) {
//                                        mSourceMarker = mMap.addMarker(new MarkerOptions()
//                                                .position(latLngSource)
//                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_dot))
//                                                .title("PickUp Location"));
                                    }
                                    //Animation
                                    //Animation
                                    ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0, 100);
                                    polyLineAnimator.setDuration(2000);
                                    polyLineAnimator.setInterpolator(new LinearInterpolator());
                                    polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                            List<LatLng> points = greyPolyline.getPoints();
                                            int percentValue = (int) valueAnimator.getAnimatedValue();
                                            int size = points.size();
                                            int newPoints = (int) (size * (percentValue / 100.0f));
                                            List<LatLng> p = points.subList(0, newPoints);
                                            blackPolyline.setPoints(p);
                                        }
                                    });
                                    polyLineAnimator.start();

                                    if (mDestinationMarker != null)
                                        mDestinationMarker.remove();
                                    if (isDriverComing) {
                                        mDestinationMarker = mMap.addMarker(new MarkerOptions().position(latLngDestination)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin))
                                                .title("Pickup Location"));
                                    } else {
                                        mDestinationMarker = mMap.addMarker(new MarkerOptions()
                                                .position(latLngDropOff)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place_black))
                                                .title(mDropOffAddress));
                                    }
                                    LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
                                    builder2.include(latLngSource);
                                    builder2.include(latLngDestination);
                                    LatLngBounds bounds2 = builder2.build();

                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds2, 50);
                                    mMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
                                        public void onCancel() {
                                        }

                                        public void onFinish() {
                                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -2.0);
                                            mMap.moveCamera(zout);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            showToast(t.getMessage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDistance(JSONArray routes) {
        try {
            JSONObject object = routes.getJSONObject(0);
            JSONArray legs = object.getJSONArray("legs");

            JSONObject legsObject = legs.getJSONObject(0);

            //Get Distance
            JSONObject distance = legsObject.getJSONObject("distance");
            String distance_text = distance.getString("text");
            mDistance = Double.parseDouble(distance_text.replaceAll("[^0-9\\\\.]+", ""));

            //Get Duration
            JSONObject duration = legsObject.getJSONObject("duration");
            String duration_text = duration.getString("text");
            mDuration = duration.getInt("value");
            //tvDistance.setText("Distance: " + distance_text + " (Time: " + duration_text+")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            poly.add(new LatLng((double) lat / 1E5, (double) lng / 1E5));
        }

        return poly;
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionManager.getLocationPermission();
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void displayLocation(final Boolean animate) {
        if (mLastLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionManager.getLocationPermission();
            } else {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
            }
        }
        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            //Save rider location firebase
            if (animate)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), MAP_ZOOM));
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), MAP_ZOOM));

            if (!isDriverTrackingStarted)
                getDriverInfo();
        } else {
//            showToast("Please enable location on your device");
            Log.d("ERROR", "Can not get your location");
        }
        btnMyLocation.setVisibility(View.GONE);
    }

    boolean isDriverTrackingStarted = false;

    private void getDriverInfo() {
        if (driverLocationListener == null) {
            trackDriverLocation();
        }
        isDriverTrackingStarted = true;
        pDialog.setMessage("Please wait...");
        final DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS)
                .child(driverId);
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get Driver information
                Log.d(TAG, "Driver Detail " + dataSnapshot.toString());
                driver = dataSnapshot.getValue(Driver.class);
                tvDriverName.setText(driver.getName());
                if (driver.getOn_duty() == 0) {
                    Marker mDriverMarker = mMap.addMarker(new MarkerOptions()
                            .position(mDriverLocation)
                            .flat(true)
                            .title(driver.getName())
                            .icon(carIcon));
                    mDriverMarker.setTag(driverId);
                    driver.setMarker(mDriverMarker);
                    switch (ride.getRide_status()) {
                        case RIDE_ACCEPTED:
                            isDriverComing = true;
//                                getDirection(mDriverLocation, latLngPickup, true);
                            break;
                        case RIDE_DRIVER_ARRIVED:
//                                getDirection(mDriverLocation, latLngDropOff, false);
                            break;
                        case RIDE_STARTED:
                            getDirection(mDriverLocation, latLngDropOff, false);
                            break;
                    }
                } else {
                    rideCancelledByDriver();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void trackDriverLocation() {
        if (driverLocationListener != null) {
            refDriversLocation.removeEventListener(driverLocationListener);
        }
        refDriversLocation = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_ON_DUTY).child(driverId).child("l");
        driverLocationListener = refDriversLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Log.d(TAG, "Driver Location Changed:" + dataSnapshot.getValue().toString());
                    GenericTypeIndicator<List<Double>> t = new GenericTypeIndicator<List<Double>>() {
                    };
                    List<Double> latLng = dataSnapshot.getValue(t);
                    mDriverLocation = new LatLng(latLng.get(0), latLng.get(1));
                    animateMarkerNew(mDriverLocation, driver.getMarker());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void animateMarkerNew(final LatLng destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = destination;

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(2000); // duration 2 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        /*mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(MAP_ZOOM)
                                .build()));*/
                        marker.setAnchor(0.5f, 0.5f);
                        rotateMarker(marker, getBearing(startPosition, new LatLng(destination.latitude, destination.longitude)), marker.getRotation());
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }

    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    public void rotateMarker(final Marker marker, final float toRotation, float st) {
        //actionBar.setSubtitle(st+" _ "+toRotation);

        if (st > 180 && toRotation < 90) {
            st = 0;
        }
        /*if(st<90&&toRotation>180)
        {
            st = 180;
        }*/
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;
        final long duration = 200;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                float rot = t * toRotation + (1 - t) * startRotation;
                //marker.setAnchor(0.0f,0.0f);
                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private void updateLocationToServer(Boolean fetchLocation) {

        if (fetchLocation) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionManager.getLocationPermission();
            }
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        //Save rider location firebase
        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {

                }
            });
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionManager.getLocationPermission();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLocationUpdate = true;

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(RiderTrackingActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConfig.MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                    }
                }
        }
    }

    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustomRiderInfoWindow(this));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                btnMyLocation.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        setUpMap();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        //setUpMap();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        displayLocation(false);
        updateLocationToServer(true);
        if (mGoogleApiClient.isConnected())
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateLocationToServer(false);
        //updateCamera();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent i;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_rides:
                                drawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(new Intent(getApplicationContext(), MyRidesActivity.class));
                                return true;
                            case R.id.nav_locations:
                                //menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(new Intent(getApplicationContext(), StoredLocationsActivity.class));
                                return true;
                            case R.id.nav_payments:
                                drawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(new Intent(getApplicationContext(), PaymentMethodActivity.class));
                                return true;
                            case R.id.nav_help:
                                drawerLayout.closeDrawer(GravityCompat.START);
                                //startActivity(new Intent(getApplicationContext(),PaymentMethodActivity.class));
                                showToast("Working on it");
                                return true;
                            case R.id.nav_log_out:
                                //menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                logOut();
                                return true;
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Bundle extras = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_SOURCE:
                    extras = intent.getExtras();
                    if (extras != null) {
                        double latitude = extras.getDouble("latitude");
                        double longitude = extras.getDouble("longitude");

                        mPickupAddress = extras.getString("address");
                        tvPickup.setText(mPickupAddress);
                        latLngPickup = new LatLng(latitude, longitude);

                        if (isDriverComing) {
                            getDirection(driver.getMarker().getPosition(), latLngPickup, isDriverComing);
                        } else
                            getDirection(latLngPickup, latLngDropOff, isDriverComing);
                    }
                    break;
                case SELECT_DESTINATION:
                    extras = intent.getExtras();
                    if (extras != null) {
                        double latitude = extras.getDouble("latitude");
                        double longitude = extras.getDouble("longitude");

                        mDropOffAddress = extras.getString("address");
                        tvDropOff.setText(mDropOffAddress);
                        latLngDropOff = new LatLng(latitude, longitude);
                        if (mDestinationMarker != null)
                            mDestinationMarker.remove();
                        mDestinationMarker = mMap.addMarker(new MarkerOptions().position(latLngDropOff)
                                .icon(BitmapDescriptorFactory.defaultMarker(0000000f))
                                .title("DropOff Location"));
                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngDropOff, MAP_ZOOM));

                        //mBottomSheet = BottomSheetRiderFragment.newInstance(mSourceAddress, mDropOffAddress);
                        //mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());
                        if (isDriverComing) {

                        } else {
                            getDirection(latLngPickup, latLngDropOff, isDriverComing);
                        }
                    }
                    break;
            }
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ride != null && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!rideFetched)
            initRideRequest(null);
        registerBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        } else {
            /*androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(RiderTrackingActivity.this);
//            builder.setTitle("");
            builder.setMessage("Are you sure want to exit?");
            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(RiderTrackingActivity.this,HomeActivity.class));
                    RiderTrackingActivity.super.onBackPressed();
                    dialog.dismiss();
                }
            });
            androidx.appcompat.app.AlertDialog alert = builder.create();
            alert.show();*/

        }
    }

    @Override
    protected void onPause() {
        rideFetched = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void logOut() {
        session.destroy();
        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
