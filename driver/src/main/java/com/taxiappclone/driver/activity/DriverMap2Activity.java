package com.taxiappclone.driver.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suke.widget.SwitchButton;
import com.taxiappclone.common.activity.CustomActivity;
import com.taxiappclone.common.activity.HelpActivity;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.model.Rider;
import com.taxiappclone.common.model.VehicleDetailsResponse;
import com.taxiappclone.common.model.VehicleType;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.taxiappclone.common.remote.IGoogleAPI;
import com.taxiappclone.common.utils.ServerUtilities;
import com.taxiappclone.common.view.CircleImageView;
import com.taxiappclone.driver.R;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.custom.CustomRiderInfoWindow;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.remote.Common;
import com.taxiappclone.driver.app.AppController;
import com.taxiappclone.driver.app.ModuleConfig;
import com.taxiappclone.driver.service.UpdateLocationService;
import com.taxiappclone.common.utils.PermissionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
import static com.taxiappclone.common.app.AppConfig.CURRENT_USER;
import static com.taxiappclone.common.app.AppConfig.DISPLACEMENT;
import static com.taxiappclone.common.app.AppConfig.FASTEST_INTERVAL;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_AVAILABLE;
import static com.taxiappclone.common.app.AppConfig.UPDATE_INTERVAL;
import static com.taxiappclone.common.app.AppConfig.URL_MAIN;
import static com.taxiappclone.common.app.AppConfig.USER_VEHICLE;
import static com.taxiappclone.common.app.AppConfig.VEHICLE_DETAILS;
import static com.taxiappclone.common.app.AppConfig.mLastLocation;
import static com.taxiappclone.common.app.CommonUtils.setStatusBarGradient;
import static com.taxiappclone.common.app.CommonUtils.showAlert;
import static com.taxiappclone.common.app.Constants.DRIVER_ONLINE;
import static com.taxiappclone.common.app.Constants.ON_DUTY;
import static com.taxiappclone.common.app.Constants.RIDE_FINISHED;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_STATUS;
import static com.taxiappclone.driver.app.ModuleConfig.URL_GET_USER;
import static com.taxiappclone.driver.app.ModuleConfig.URL_GET_VEHICLE_TYPES;
import static com.taxiappclone.driver.app.ModuleConfig.URL_UPDATE_STATUS;
import static com.taxiappclone.driver.app.ModuleConfig.URL_UPDATE_TOKEN;

public class DriverMap2Activity extends CustomActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, View.OnClickListener {

    private static final String TAG = DriverMap2Activity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FragmentDrawer drawerFragment;
    private SessionManager session;
    private ActionBar actionBar;

    private TextView tvName, tvProfile;
    private Button btnEditProfile;
    private ImageButton btnMyLocation;
    private View navHeader;

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;

    DatabaseReference driverLocationRef;
    GeoFire geoFire;
    private String userId, myToken;

    Marker mCurrent;
    SwitchButton switchOnline;
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
    private int index, next;
    //private Button btnGo;
    private PlaceAutocompleteFragment autoPlaces;
    private String destination;
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
    private boolean isGoogleApiConnected = false;
    private ProgressDialog pDialog;
    private PowerManager.WakeLock mWakeLock;
    private CircleImageView imgUser;
    private AsyncTask<Void, Void, String> mRegisterTask;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle("Offline");
        setStatusBarGradient(this);

        apiService = ApiUtils.INSTANCE.getApiService();

        if (SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        } else {
            final Window win = getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionManager = new PermissionManager(this, this);
            permissionManager.getLocationPermission();
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }
        setupNavigationDrawerContent(navigationView);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, drawerLayout, toolbar);

        navHeader = navigationView.getHeaderView(0);
        imgUser = navHeader.findViewById(R.id.imgUser);
        tvProfile = navHeader.findViewById(R.id.tvProfile);
        tvName = navHeader.findViewById(R.id.tvName);
        btnEditProfile = navHeader.findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            startActivity(new Intent(this, EditDriverProfileActivity.class));
        });

        setUserDetails();

        tvProfile.setText(CommonUtils.getNameLetters(session.getStringValue("name")));
        tvName.setText(session.getStringValue("name"));

        btnMyLocation = findViewById(R.id.btn_my_location);
        switchOnline = findViewById(R.id.location_switch);

        btnMyLocation.setOnClickListener(this);
        btnMyLocation.setVisibility(View.GONE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "My Token: " + myToken);


        switchOnline.setOnCheckedChangeListener((compoundButton, isOnline) -> {
            if (isOnline && mGoogleApiClient.isConnected()) {
                connectDriver();
            } else {
                if (mGoogleApiClient.isConnected())
                    disconnectDriver(false, false);
            }
        });
        polyLineList = new ArrayList<>();
        carIcon = BitmapDescriptorFactory.fromResource(R.drawable.car);

        //Places API
        autoPlaces = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autoPlaces.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (switchOnline.isChecked()) {
                    destination = place.getAddress().toString();
                    destination = destination.replace(" ", "+");
                    getDirection();
                } else {
                    showToast("Please change your status to ONLINE");
                }
            }

            @Override
            public void onError(Status status) {
                showToast(status.toString());
            }
        });

        driverLocationRef = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_AVAILABLE);
        geoFire = new GeoFire(driverLocationRef);

        setUpLocation();
        mService = Common.getGoogleAPI();
        updateTokenToServer();
        //setupSensorManager();
        getAppOnTopPermission();

        fetchVehicleDetails();
    }

    private void fetchVehicleDetails() {
        final Map<String, String> header = new HashMap<String, String>();
        header.put("SECURE-API-KEY", ModuleConfig.DRIVER_API_KEY);
        mRegisterTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_GET_VEHICLE_TYPES, null, header);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    session.setValue(VEHICLE_DETAILS, result);
                }
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    private void getAppOnTopPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this).setTitle("Grant Permission")
                    .setMessage(Html.fromHtml("Please enable <b>App On Top</b> option so you can see calling screen when rider request for a ride."))
                    .setPositiveButton("OK", (dialog, which) -> {
                        if (SDK_INT < 29) {
                            return;
                        }
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }).create().show();
        }
    }

    private void setUserDetails() {
        tvProfile.setText(CommonUtils.getNameLetters(session.getStringValue("name")));
        tvName.setText(session.getStringValue("name"));
        Glide.with(getApplicationContext()).load(URL_MAIN + "uploads/drivers/" + session.getStringValue("image")).into(imgUser);
    }

    private void updateTokenToServer() {
        /*FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(TABLE_TOKENS);
        FCMToken token = new FCMToken(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getUid())
                .setValue(token);*/

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference token = db.getReference(TABLE_DRIVERS).child(userId).child("token");
        token.setValue(myToken);

        session.setValue("token", myToken);
        final Map<String, String> param = new HashMap<String, String>();
        param.put("token", myToken);

        final Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", session.getStringValue("auth_token"));

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_UPDATE_TOKEN, param, header);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        Log.d(TAG, "Update Token Response: " + result);
                        if (jObj.optBoolean("status") && jObj.getJSONObject("data").getString("status").equals("1")) {
                            Rider rider = new Gson().fromJson(jObj.getJSONObject("data").toString(), Rider.class);
                            session.setValue(CURRENT_USER, jObj.getJSONObject("data").toString());
                            session.setValue("image", jObj.getJSONObject("data").getString("image"));
                            session.setValue("mobile", rider.getMobile());
                            session.setValue("name", rider.getName());
                            session.setValue("dob", rider.getDob());
                            session.setValue("gender", rider.getGender());
                            session.setLogin(true);

                            setUserDetails();
                        } else {
                            session.setLogin(false);
                            Intent intent = new Intent(DriverMap2Activity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                }
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
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
                            startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                            return true;
                        case R.id.nav_log_out:
                            //menuItem.setChecked(true);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            logOut();
                            return true;
                    }
                    return true;
                });
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
        displayLocation(null, true);
        FirebaseDatabase.getInstance().goOnline();
        updateStatus(DRIVER_ONLINE, "1");
        actionBar.setSubtitle("Online");
        /*actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.cpb_blue)));*/
        session.setValue(DRIVER_ONLINE, true);
        Snackbar.make(mapFragment.getView(), "You are online", Snackbar.LENGTH_SHORT).show();
        hideDialog();
    }

    private void updateStatus(String field, String value) {
        final Map<String, String> param = new HashMap<String, String>();
        param.put("field", field);
        param.put("value", value);

        final Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", session.getStringValue("auth_token"));

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_UPDATE_STATUS, param, header);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        Log.d(TAG, "Update Status Response: " + result);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    private void disconnectDriver(boolean exitApp, boolean logout) {
        showDialog();
        if (UpdateLocationService.isRunning())
            stopService(new Intent(this, UpdateLocationService.class));

        session.setValue(DRIVER_ONLINE, false);
        mRequestingLocationUpdates = false;
        stopLocationUpdates();
        updateStatus(DRIVER_ONLINE, "0");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_AVAILABLE);
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, (key, error) -> {
            hideDialog();
            if (exitApp) {
                finishAffinity();
            } else if (logout) {
                FirebaseAuth.getInstance().signOut();
                session.destroy();
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finishAffinity();
            } else {
                mMap.clear();
                actionBar.setSubtitle("Offline");
            /*actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.colorPrimary)));*/
                Snackbar.make(mapFragment.getView(), "You are offline", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void getDirection() {
        currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + destination + "&" +
                    "key=" + getResources().getString(R.string.google_api_browser_key);
            Log.d("request API", requestApi);
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jObj = new JSONObject(response.body().toString());
                                JSONArray jArr = jObj.getJSONArray("routes");
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
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyline = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1))
                                        .title("Pickup Location"));

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

                                carMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                                handler = new Handler();
                                index = -1;
                                next = 1;
                                handler.postDelayed(drawPathRunnable, 3000);

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
            LatLng endPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endPosition, 18.0f));
        } else {
            showToast("Please unable location on your phone.");
            Log.d("ERROR", "Can not get your location");
        }
        btnMyLocation.setVisibility(View.GONE);
    }

    /*
     * This method will show location & save on GeoFire
     * */
    private void displayLocation(Location location, Boolean animate) {
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionManager.getLocationPermission();
            }
            location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        final Location mLastLocation = location;
        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            final LatLng endPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if (animate) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endPosition, 18.0f));
            }
            if (switchOnline.isChecked()) {
                //Save driver location firebase
                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
//                        showToast("updating location from activity");
                    }
                });
            }
            changeDriverLocation(latitude, longitude);
        } else {
            showToast("Please unable location on your phone.");
            Log.d("ERROR", "Can not get your location");
        }
    }

    private void changeDriverLocation(double latitude, double longitude) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", String.valueOf(AppController.getInstance().getCurrentUserId()))
                .addFormDataPart("lat", String.valueOf(latitude))
                .addFormDataPart("lng", String.valueOf(longitude))
                .build();

        apiService.updateDriverLocation(requestBody).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.e(TAG, "onResponse: location updated");
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e(TAG, "onFailure: failed to update location");
            }
        });
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
            valueAnimator.setDuration(2000); // duration 2 second
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            //btnStartLocationUpdates.setText("Stop updates");
            mRequestingLocationUpdates = true;
            startLocationUpdates();
            Snackbar.make(mapFragment.getView(), "You are online", Snackbar.LENGTH_SHORT).show();
            Log.d(TAG, "Periodic location updates started!");
        } else {
            //btnStartLocationUpdates.setText("Start updates");
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
            Snackbar.make(mapFragment.getView(), "You are offline", Snackbar.LENGTH_SHORT).show();
            Log.d(TAG, "Periodic location updates stopped!");
        }
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        setUpMap();
        isGoogleApiConnected = true;
        if (session.getBooleanValue(DRIVER_ONLINE)) {
            switchOnline.setChecked(true);
        } else {
            switchOnline.setChecked(false);
        }
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation(location, false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_my_location:
                displayLocation();
                break;
            default:
                break;
        }
    }

    private void logOut() {
        new AlertDialog.Builder(this).setTitle("Logout").setMessage("Are you sure you want to logout?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disconnectDriver(false, true);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
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
//        showToast("Service: "+UpdateLocationService.isRunning());
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && session.getBooleanValue("driver_connected")) {
            startLocationUpdates();
        } else {
            mGoogleApiClient.connect();
        }
        setUserDetails();
    }

    @Override
    public void onBackPressed() {
        if (session.getBooleanValue(DRIVER_ONLINE)) {
            new AlertDialog.Builder(this).setTitle("Exit App").setMessage("Are you sure you want to exit?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            disconnectDriver(true, false);
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            super.onBackPressed();
        }
    }
}