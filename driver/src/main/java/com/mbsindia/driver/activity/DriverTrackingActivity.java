package com.mbsindia.driver.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
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
import androidx.appcompat.app.AlertDialog;
;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.ncorti.slidetoact.SlideToActView;
import com.taxiappclone.common.activity.CustomActivity;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.remote.IGoogleAPI;
import com.mbsindia.driver.R;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.custom.CustomRiderInfoWindow;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.RideRequest;
import com.taxiappclone.common.model.Rider;
import com.taxiappclone.common.notification.SendPushNotification;
import com.taxiappclone.common.notification.model.Data;
import com.taxiappclone.common.notification.model.FCMMessage;
import com.mbsindia.driver.notification.NotificationUtils;
import com.taxiappclone.common.remote.Common;
import com.mbsindia.driver.service.UpdateLocationService;
import com.taxiappclone.common.utils.PermissionManager;

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


import static com.taxiappclone.common.app.AppConfig.DISPLACEMENT;
import static com.taxiappclone.common.app.AppConfig.FASTEST_INTERVAL;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_ON_DUTY;
import static com.taxiappclone.common.app.AppConfig.TABLE_RIDERS;
import static com.taxiappclone.common.app.AppConfig.TABLE_PICKUP_REQUESTS;
import static com.taxiappclone.common.app.AppConfig.UPDATE_INTERVAL;
import static com.taxiappclone.common.app.AppConfig.mLastLocation;
import static com.taxiappclone.common.app.CommonUtils.getDateTimeString;
import static com.taxiappclone.common.app.CommonUtils.showAlert;
import static com.taxiappclone.common.app.Constants.DRIVER_ARRIVED_TIME;
import static com.taxiappclone.common.app.Constants.DRIVER_ONLINE;
import static com.taxiappclone.common.app.Constants.ON_DUTY;
import static com.taxiappclone.common.app.Constants.RIDER_ID;
import static com.taxiappclone.common.app.Constants.RIDER_TOKEN;
import static com.taxiappclone.common.app.Constants.RIDE_ACCEPTED;
import static com.taxiappclone.common.app.Constants.RIDE_CANCELLED_BY_DRIVER;
import static com.taxiappclone.common.app.Constants.RIDE_CANCELLED_BY_RIDER;
import static com.taxiappclone.common.app.Constants.RIDE_CANCEL_REASON;
import static com.taxiappclone.common.app.Constants.RIDE_CANCEL_TIME;
import static com.taxiappclone.common.app.Constants.RIDE_COMPLETED;
import static com.taxiappclone.common.app.Constants.RIDE_DRIVER_ARRIVED;
import static com.taxiappclone.common.app.Constants.RIDE_END_TIME;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REACHED_DESTINATION;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;
import static com.taxiappclone.common.app.Constants.RIDE_STARTED;
import static com.taxiappclone.common.app.Constants.RIDE_START_TIME;
import static com.taxiappclone.common.app.Constants.RIDE_STATUS;
import static com.mbsindia.driver.notification.NotificationUtils.PUSH_NOTIFICATION;
import static com.mbsindia.driver.notification.NotificationUtils.TYPE_NOTIFICATION;

public class DriverTrackingActivity extends CustomActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, SlideToActView.OnSlideCompleteListener {

    private static final String TAG = DriverTrackingActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Toolbar toolbar;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private FragmentDrawer drawerFragment;
    private TextView tvRiderName, tvRiderRating, tvPickup, tvDropOff, tvPaymentType, tvAmount, tvDestination;
    private View btnCancelRide;
    private Button btnArrived;
    private SlideToActView btnStartRide, btnEndRide;
    private SessionManager session;

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;

    private ImageButton btnMyLocation;

    DatabaseReference driverLocationRef;
    GeoFire geoFire;
    private String userId, myToken;

    Marker mCurrent;
    SupportMapFragment mapFragment;

    // latitude and longitude
    double latitude = 0;
    double longitude = 0;
    private boolean mRequestingLocationUpdates = false;

    private PermissionManager permissionManager;

    //Car Animation
    private List<LatLng> polyLineList;
    private Marker carMarker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition, currentPosition;
    private LatLng riderPickup, riderDropOff, riderLocation;
    private String riderId, riderToken, rideId;
    private int index, next;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;
    private IGoogleAPI mService;

    private double textLat = 24.0733566, testLng = 75.068596;
    private BitmapDescriptor carIcon;
    private SensorManager mSensorManager;
    private float start_rotation = 0;
    private Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
            if (index < polyLineList.size() - 1) {
                index++;
                next = index + 1;
            }
            if (index < polyLineList.size() - 1) {
                startPosition = polyLineList.get(index);
                endPosition = polyLineList.get(next);
            }
            animateMarkerNew(endPosition, carMarker);
            handler.postDelayed(this, 3000);
        }
    };
    private Marker mDestinationMarker;
    private Boolean riderPathCreated = false;
    private LatLng myPosition;
    private View viewNavigate;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private ProgressDialog pDialog;
    private DatabaseReference refRideRequest;
    private RideRequest ride;
    private Gson gson;
    private Rider rider;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String paymentMode = "cod";
    private String totalFare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tracking);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("ON RIDE");

        CommonUtils.setStatusBarGradient(this);
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(this, EnableLocationActivity.class));
        }

        if (getIntent().hasExtra("paymentMode")) {
            paymentMode = getIntent().getStringExtra("paymentMode");
        }
        if (getIntent().hasExtra("totalFare")) {
            totalFare = getIntent().getStringExtra("totalFare");
        }

        tvDestination = findViewById(R.id.tv_destination);
        btnArrived = findViewById(R.id.btnArrived);
        btnStartRide = findViewById(R.id.btnStartRide);
        btnEndRide = findViewById(R.id.btnEndRide);

        btnArrived.setOnClickListener(this);
        btnStartRide.setOnSlideCompleteListener(this);
        btnEndRide.setOnSlideCompleteListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, drawerLayout, toolbar);

        View drawer = drawerFragment.getLayout();
        tvRiderName = drawer.findViewById(R.id.tv_rider_name);
        tvRiderRating = drawer.findViewById(R.id.tv_rating);
        tvPickup = drawer.findViewById(R.id.tv_pickup_address);
        tvDropOff = drawer.findViewById(R.id.tv_dropoff_address);
        tvPaymentType = drawer.findViewById(R.id.tv_payment_type);
        tvAmount = drawer.findViewById(R.id.tv_amount);
        btnCancelRide = drawer.findViewById(R.id.view_cancel);

        btnMyLocation = findViewById(R.id.btn_my_location);
        viewNavigate = findViewById(R.id.view_navigate);

        btnCancelRide.setOnClickListener(this);
        viewNavigate.setOnClickListener(this);
        btnMyLocation.setOnClickListener(this);
        btnMyLocation.setVisibility(View.GONE);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myToken = FirebaseInstanceId.getInstance().getToken();

        polyLineList = new ArrayList<>();
        carIcon = BitmapDescriptorFactory.fromResource(R.drawable.car);
        session.setValue(ON_DUTY, true);
        driverLocationRef = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_ON_DUTY);
        geoFire = new GeoFire(driverLocationRef);
        //updateTokenToServer();
        //setupSensorManager();
        gson = new Gson();
        rideId = session.getStringValue(RIDE_ID);
        riderId = session.getStringValue(RIDER_ID);
        riderToken = session.getStringValue(RIDER_TOKEN);
        refRideRequest = FirebaseDatabase.getInstance().getReference(TABLE_PICKUP_REQUESTS).child(riderId).child(rideId);
        showDialog();
        if (getIntent().hasExtra(RIDE_REQUEST)) {
            ride = gson.fromJson(getIntent().getStringExtra(RIDE_REQUEST), RideRequest.class);
            initRideRequest();
        } else {
            refRideRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Ride Request Detail:" + dataSnapshot.getValue().toString());
                    ride = dataSnapshot.getValue(RideRequest.class);
                    initRideRequest();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        /*destination = place.getAddress().toString();
        destination = destination.replace(" ", "+");
        getDirection();*/
    }

    private void initRideRequest() {

        riderLocation = gson.fromJson(ride.getRider_location(), LatLng.class);
        riderPickup = gson.fromJson(ride.getPickup_location(), LatLng.class);
        riderDropOff = gson.fromJson(ride.getDropoff_location(), LatLng.class);
        tvPickup.setText(ride.getPickup_address());
        tvDropOff.setText(ride.getDropoff_address());
        if (ride.getRide_status().equals(RIDE_ACCEPTED)) {
            tvDestination.setText(ride.getPickup_address());
        } else {
            tvDestination.setText(ride.getDropoff_address());
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ride.getRide_status().equals(RIDE_CANCELLED_BY_DRIVER)) {
            if (TextUtils.isEmpty(ride.getStart_time())) {
                session.setValue(ON_DUTY, false);
                session.setValue(DRIVER_ONLINE, true);
                Intent intent = new Intent(getApplicationContext(), DriverMap2Activity.class);
                disconnectDriver(intent);
            } else {
                setRideEnded();
            }
        }
        if (!ride.getRide_status().equals(RIDE_COMPLETED)) {
            getRiderInfo();
            setUpLocation();
            mService = Common.getGoogleAPI();
            hideDialog();
        }
    }

    private void getRiderInfo() {
        DatabaseReference refRider = FirebaseDatabase.getInstance().getReference(TABLE_RIDERS).child(riderId);
        refRider.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rider = dataSnapshot.getValue(Rider.class);
                if (rider != null)
                    tvRiderName.setText(rider.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_my_location:
                displayLocation();
                break;
            case R.id.view_navigate:
                if (ride.getRide_status().equals(RIDE_ACCEPTED)) {
                    openNavigation(riderPickup);
                } else {
                    openNavigation(riderDropOff);
                }
                break;
            case R.id.view_cancel:
                drawerLayout.closeDrawer(GravityCompat.START);
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("Select Reason");
                String[] reasons = {"Select Reason", "Rider gives incorrect location.", "Rider's behaviour is bad.", "Passenger is not coming", "Passenger is not arriving", "Other"};
                final ArrayAdapter<String> adp = new ArrayAdapter<String>(DriverTrackingActivity.this,
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
                final AlertDialog dialog = builder.create();
                dialog.show();
                inputReason.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0)
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        else
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
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
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                }
                            } else {
                                inputReason.setVisibility(View.GONE);
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        } else {
                            inputReason.setVisibility(View.GONE);
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case R.id.btnArrived:
                new android.app.AlertDialog.Builder(DriverTrackingActivity.this).setMessage("Are you sure you have arrived at pickup location of rider?")
                        .setPositiveButton("YES", (dialog12, which) -> {
                            dialog12.dismiss();
                            sendRideStatus(RIDE_DRIVER_ARRIVED, DRIVER_ARRIVED_TIME, null);
                        }).setNegativeButton("No", (dialog1, which) -> dialog1.dismiss()).create().show();
                break;
        }
    }

    private void sendRideStatus(String status, String timeField, String msg) {
        ride.setRide_status(status);
        if (ride.getRide_status().equals(RIDE_ACCEPTED)) {
            tvDestination.setText(ride.getPickup_address());
        } else {
            tvDestination.setText(ride.getDropoff_address());
        }
        showDialog();
        Map<String, Object> values = new HashMap<>();
        values.put(RIDE_STATUS, status);
        String time = getDateTimeString();
        values.put(timeField, getDateTimeString());
        if (!TextUtils.isEmpty(msg))
            values.put(RIDE_CANCEL_REASON, msg);

        refRideRequest.updateChildren(values).addOnSuccessListener(aVoid -> {
            Data data = new Data();
            data.setType(status);
            if (!TextUtils.isEmpty(msg)) {
                data.setMessage(msg);
            }
            data.setRide_id(rideId);
            data.setUser_id(userId);
            data.setToken(myToken);
            FCMMessage message = new FCMMessage(data, riderToken);
            JSONObject messageParams = null;
            try {
                messageParams = new JSONObject(gson.toJson(message));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (status) {
                case RIDE_DRIVER_ARRIVED:
                    ride.setArrived_time(time);
                    setDriverArrived();
                    break;
                case RIDE_CANCELLED_BY_DRIVER:
                    ride.setCancel_time(time);
                    if (TextUtils.isEmpty(ride.getStart_time())) {
                        session.setValue(ON_DUTY, false);
                        session.setValue(DRIVER_ONLINE, true);
                        Intent intent = new Intent(getApplicationContext(), DriverMap2Activity.class);
                        disconnectDriver(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CollectFareActivity.class);
                        intent.putExtra("totalFare", totalFare);
                        intent.putExtra("paymentMode", paymentMode);
                        intent.putExtra(RIDE_REQUEST, gson.toJson(ride));
                        disconnectDriver(intent);
                    }
                    break;
                case RIDE_STARTED:
                    ride.setStart_time(time);
                    setRideStarted();
                    break;
                case RIDE_COMPLETED:
                    ride.setEnd_time(time);
                    setRideEnded();
                    break;
            }
            hideDialog();

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setDriverArrived() {
        getDirection(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), riderDropOff, false);
        btnArrived.setVisibility(View.GONE);
        btnStartRide.setVisibility(View.VISIBLE);
        btnEndRide.setVisibility(View.GONE);
    }

    private void setRideStarted() {
        getDirection(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), riderDropOff, false);
        btnArrived.setVisibility(View.GONE);
        btnStartRide.setVisibility(View.GONE);
        btnEndRide.setVisibility(View.VISIBLE);
    }

    private void setRideEnded() {
        Intent intent = new Intent(getApplicationContext(), CollectFareActivity.class);
        intent.putExtra("totalFare", totalFare);
        intent.putExtra("paymentMode", paymentMode);
        intent.putExtra(RIDE_REQUEST, gson.toJson(ride));
        disconnectDriver(intent);
    }

    @Override
    public void onSlideComplete(SlideToActView slideToActView) {
        if (slideToActView.getId() == R.id.btnStartRide) {
            sendRideStatus(RIDE_STARTED, RIDE_START_TIME, null);
        } else {
            sendRideStatus(RIDE_COMPLETED, RIDE_END_TIME, null);
        }

    }

    private void openNavigation(LatLng destination) {
        double lat = destination.latitude;
        double lng = destination.longitude;
        //Uri mapUri = Uri.parse("geo:"+lat+","+lng+"?q="+lat+","+lng+"(Destination Address)");
        Uri mapUri = Uri.parse("google.navigation:q=" + lat + "," + lng);
        //Uri mapUri = Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void getDirectionToRider() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionManager.getLocationPermission();
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            myPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            getDirection(myPosition, riderPickup, true);
        } else {
            showToast("Please unable location on your phone.");
            Log.d("ERROR", "Can not get your location");
        }
        btnMyLocation.setVisibility(View.GONE);
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

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void connectDriver() {
        showDialog();
        if (UpdateLocationService.isRunning())
            UpdateLocationService.restart(this);
        else
            startService(new Intent(this, UpdateLocationService.class));

        mRequestingLocationUpdates = true;
        startLocationUpdates();
        updateLocation(null);
        displayLocation();
        session.setValue(DRIVER_ONLINE, true);
        hideDialog();
    }

    private void disconnectDriver(Intent intent) {
        showDialog();
        if (UpdateLocationService.isRunning())
            stopService(new Intent(this, UpdateLocationService.class));

//        session.setValue(DRIVER_ONLINE,true);

        if (mRequestingLocationUpdates) {
            stopLocationUpdates();
            mRequestingLocationUpdates = false;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_ON_DUTY);
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, (key, error) -> {
            mMap.clear();
            hideDialog();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        });
    }

    private void getDirection(final LatLng latLngSource, final LatLng latLngDestination, final Boolean pickupRider) {
        if (polyLineList != null)
            polyLineList.clear();
        if (greyPolyline != null)
            greyPolyline.remove();
        if (blackPolyline != null)
            blackPolyline.remove();
        //mMap.clear();
        //stopLocationUpdates();
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
                                    //setDistance(jArr);

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

                                    /*if(mSourceMarker!=null)
                                        mSourceMarker.remove();
                                    mSourceMarker = mMap.addMarker(new MarkerOptions()
                                            .position(latLngSource)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_dot))
                                            .title("PickUp Location"));*/

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
                                    if (pickupRider) {
                                        mDestinationMarker = mMap.addMarker(new MarkerOptions().position(latLngDestination)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_black))
                                                .title("Rider Location"));
                                    } else {
                                        mDestinationMarker = mMap.addMarker(new MarkerOptions().position(latLngDestination)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place_black))
                                                .title("DropOff Location"));
                                    }

                                    LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
                                    builder.include(latLngSource);
                                    builder.include(latLngDestination);
                                    LatLngBounds bounds2 = builder.build();

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

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.setInfoWindowAdapter(new CustomRiderInfoWindow(this));
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

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

    /*
     * This method will only show current Location & zoom camera
     * */
    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionManager.getLocationPermission();
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            LatLng myPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18.0f));
        } else {
            showToast("Please unable location on your phone.");
            Log.d("ERROR", "Can not get your location");
        }
        btnMyLocation.setVisibility(View.GONE);
    }

    /*
     * This method will show location & save on GeoFire
     * */
    private void updateLocation(Location location) {
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionManager.getLocationPermission();
            }
            location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        mLastLocation = location;
        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            final LatLng endPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if (session.getBooleanValue(DRIVER_ONLINE)) {
                //Save driver location firebase
                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
            }
        } else {
            showToast("Please unable location on your phone.");
            Log.d("ERROR", "Can not get your location");
        }
    }

    public void updateCamera(float bearing, LatLng position) {
        CameraPosition currentPos = mMap.getCameraPosition();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .tilt(currentPos.tilt)
                .zoom(currentPos.zoom)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 400, null);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        //Point startPoint = proj.toScreenLocation(marker.getPosition());
        //final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final LatLng startLatLng = marker.getPosition();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setAnchor(0.5f, 0.5f);
                //rotateMarker(marker,getBearing(startLatLng, toPosition),mMap);
                marker.setPosition(new LatLng(lat, lng));
                //marker.setRotation(getBearing(startLatLng, toPosition));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private void animateMarkerNew(final LatLng destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(18.0f)
                                .build()));
                        //marker.setAnchor(0.5f,0.5f);
                        //marker.setRotation(getBearing(startPosition, new LatLng(destination.latitude, destination.longitude)));rotateMarker(carMarker,getBearing(startPosition, new LatLng(destination.latitude, destination.longitude)));
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

    private void rotateMarkerNew(final Marker marker, final float i) {

        if (marker != null) {

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();
            final Interpolator interpolator = new LinearInterpolator();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(100); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        //float v = animation.getAnimatedFraction();
                        //LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        float t = animation.getAnimatedFraction();
                        float rot = t * i + (1 - t) * startRotation;
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setRotation(-rot > 180 ? rot / 2 : rot);
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

        class LinearFixed implements DriverTrackingActivity.LatLngInterpolatorNew {
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

    private void rotateMarker(final Marker marker, final float i) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                float rot = t * i + (1 - t) * startRotation;
                marker.setAnchor(0.5f, 0.5f);
                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public void moveVehicle(final Marker marker, final Location finalPosition) {

        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 1500;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng endPosition = new LatLng(
                        startPosition.latitude * (1 - v) + (finalPosition.getLatitude()) * v,
                        startPosition.longitude * (1 - v) + (finalPosition.getLongitude()) * v);
                marker.setPosition(endPosition);
                // myMarker.setRotation(finalPosition.getBearing());
                updateCamera(0, endPosition);

                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                }
            }
        });
    }

    public void rotateMarker(final Marker marker, final float toRotation, float st) {
        actionBar.setSubtitle(st + " _ " + toRotation);

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
        final long duration = 500;

        final Interpolator interpolator = new AccelerateInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;
                //marker.setAnchor(0.0f,0.0f);
                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                start_rotation = -rot > 180 ? rot / 2 : rot;

                /*marker.setRotation(rot);
                start_rotation = rot;*/

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private float mOrientation;
    /**
     * The sensor event listener.
     */
    SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mOrientation = event.values[0];
            Log.d(TAG, "Phone Moved " + mOrientation);
            //draw(mOrientation);
            if (mCurrent != null) {
                //mCurrent.setRotation(mOrientation);
                //rotateMarker(mCurrent,mOrientation,start_rotation);
                CameraPosition currentPos = mMap.getCameraPosition();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentPos.target)
                        .bearing(mOrientation)
                        .tilt(currentPos.tilt)
                        .zoom(currentPos.zoom)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 400, null);

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    /**
     * Initialize the sensor manager.
     */
    private void setupSensorManager() {
        mSensorManager = (SensorManager) getApplicationContext()
                .getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        Log.d(TAG, "SensorManager setup");
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionManager.getLocationPermission();
        }
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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

    @Override
    protected void onStart() {
        super.onStart();
        if (ride != null && !ride.getRide_status().equals(RIDE_COMPLETED) && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
        if (ride != null && !ride.getRide_status().equals(RIDE_COMPLETED)) {
            checkPlayServices();
            // Resuming the periodic location updates
            if (mGoogleApiClient != null) {
                if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                    startLocationUpdates();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRegistrationBroadcastReceiver != null) {
            try {
                unregisterReceiver(mRegistrationBroadcastReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
        Log.e(TAG, "onMapReady: " + riderPickup);
        if (riderPickup != null) {
            GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_ON_DUTY));
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(riderPickup.latitude, riderPickup.longitude), 0.05f);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if (key.equals(userId)) {
                        //Automatically send a notification when driver comes at rider's location
//                    sendRideStatus(RIDE_DRIVER_ARRIVED,DRIVER_ARRIVED_TIME,null);
                    }
                }

                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (session.getBooleanValue(ON_DUTY)) {
            connectDriver();
        }
        switch (ride.getRide_status()) {
            case RIDE_ACCEPTED:
                getDirectionToRider();
                break;
            case RIDE_DRIVER_ARRIVED:
                setDriverArrived();
                break;
            case RIDE_STARTED:
                setRideStarted();
                break;
            case RIDE_COMPLETED:
                setRideEnded();
                break;
            case RIDE_CANCELLED_BY_DRIVER:
                session.setValue(ON_DUTY, false);
                session.setValue(DRIVER_ONLINE, true);
                Intent intent = new Intent(this, DriverMap2Activity.class);
                disconnectDriver(intent);
                break;
            case RIDE_CANCELLED_BY_RIDER:
                new AlertDialog.Builder(this).setMessage("This ride is rejected by rider.\nReason: " + ride.getReason()).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        session.setValue(ON_DUTY, false);
                        session.setValue(DRIVER_ONLINE, true);
                        Intent intent = new Intent(DriverTrackingActivity.this, DriverMap2Activity.class);
                        disconnectDriver(intent);
                    }
                });
                break;
            case RIDE_REACHED_DESTINATION:
                showAlert(this, "You reached at destination");
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateLocation(location);
//        showToast("updating location from activity");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void registerBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    String msgType = intent.getStringExtra("msg_type");
                    switch (msgType) {
                        case RIDE_CANCELLED_BY_RIDER:
                            ride.setRide_status(RIDE_CANCELLED_BY_DRIVER);
                            hideDialog();
                            String msg = "";
                            msg = "Rider cancelled this ride.";
                            new android.app.AlertDialog.Builder(DriverTrackingActivity.this).setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent;
                                    if (!TextUtils.isEmpty(ride.getStart_time())) {
                                        intent = new Intent(getApplicationContext(), CollectFareActivity.class);
                                        intent.putExtra("totalFare", totalFare);
                                        intent.putExtra("paymentMode", paymentMode);
                                        intent.putExtra(RIDE_REQUEST, gson.toJson(ride));
                                    } else {
                                        session.setValue(ON_DUTY, false);
                                        session.setValue(DRIVER_ONLINE, true);
                                        intent = new Intent(getApplicationContext(), DriverMap2Activity.class);
                                    }
                                    disconnectDriver(intent);
                                }
                            }).setCancelable(false).create().show();
                            break;
                        case TYPE_NOTIFICATION:
                            break;
                    }
                }
            }
        };
    }

    private void showDialog() {
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        } else {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Go back?");
            builder.setMessage("Are you sure want to cancel this ride?");
            builder.setPositiveButton("YES", (dialog, which) -> {
                dialog.dismiss();
                session.setValue(ON_DUTY,false);
                Intent intent = new Intent(DriverTrackingActivity.this,DriverMap2Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
            builder.setNegativeButton("NO", (dialog, which) -> {
                // Do nothing but close the dialog
                dialog.dismiss();
            });
            androidx.appcompat.app.AlertDialog alert = builder.create();
            alert.show();*/
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        session.destroy();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
    }
}