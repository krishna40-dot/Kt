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
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.taxiappclone.common.activity.HelpActivity;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.model.Driver;
import com.taxiappclone.common.model.RideRequest;
import com.taxiappclone.common.model.Rider;
import com.taxiappclone.common.model.User;
import com.taxiappclone.common.model.VehicleDetails;
import com.taxiappclone.common.model.VehicleDetailsResponse;
import com.taxiappclone.common.model.VehicleType;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.taxiappclone.common.notification.SendPushNotification;
import com.taxiappclone.common.remote.IFCMService;
import com.taxiappclone.common.remote.IGoogleAPI;
import com.taxiappclone.common.utils.RecyclerViewClickListener;
import com.taxiappclone.common.utils.ServerUtilities;
import com.taxiappclone.common.view.CircleImageView;
import com.taxiappclone.rider.Fragment.VehicleTypeDetailFragment;
import com.taxiappclone.rider.R;
import com.taxiappclone.common.custom.CustomRiderInfoWindow;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.notification.model.Data;
import com.taxiappclone.common.notification.model.FCMMessage;
import com.taxiappclone.common.notification.model.FCMToken;
import com.taxiappclone.rider.adapter.VehicleTypesListAdapter;
import com.taxiappclone.rider.app.AppController;
import com.taxiappclone.rider.notification.NotificationUtils;
import com.taxiappclone.common.remote.Common;
import com.taxiappclone.common.utils.PermissionManager;
import com.travijuu.numberpicker.library.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.taxiappclone.common.app.AppConfig.CURRENT_USER;
import static com.taxiappclone.common.app.AppConfig.SELECT_DESTINATION;
import static com.taxiappclone.common.app.AppConfig.SELECT_SOURCE;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_AVAILABLE;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_ON_DUTY;
import static com.taxiappclone.common.app.AppConfig.TABLE_PICKUP_REQUESTS;
import static com.taxiappclone.common.app.AppConfig.TABLE_RIDERS;
import static com.taxiappclone.common.app.AppConfig.TABLE_RIDERS_LOCATIONS;
import static com.taxiappclone.common.app.AppConfig.VEHICLE_DETAILS;
import static com.taxiappclone.common.app.AppConfig.mLastLocation;
import static com.taxiappclone.common.app.CommonUtils.getDateTimeString;
import static com.taxiappclone.common.app.CommonUtils.setStatusBarGradient;
import static com.taxiappclone.common.app.Constants.CAR_MICRO;
import static com.taxiappclone.common.app.Constants.CAR_MINI;
import static com.taxiappclone.common.app.Constants.CAR_SEDAN;
import static com.taxiappclone.common.app.Constants.MAP_ZOOM;
import static com.taxiappclone.common.app.Constants.RIDE_ACCEPTED;
import static com.taxiappclone.common.app.Constants.RIDE_CANCELLED_BY_DRIVER;
import static com.taxiappclone.common.app.Constants.RIDE_CANCELLED_BY_RIDER;
import static com.taxiappclone.common.app.Constants.RIDE_COMPLETED;
import static com.taxiappclone.common.app.Constants.RIDE_DRIVER_ARRIVED;
import static com.taxiappclone.common.app.Constants.RIDE_FINISHED;
import static com.taxiappclone.common.app.Constants.RIDE_REACHED_DESTINATION;
import static com.taxiappclone.common.app.Constants.RIDE_REJECTED;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;
import static com.taxiappclone.common.app.Constants.RIDE_REQUESTED;
import static com.taxiappclone.common.app.AppConfig.URL_MAIN;
import static com.taxiappclone.common.app.Constants.RIDE_STARTED;
import static com.taxiappclone.rider.app.ModuleConfig.URL_GET_USER;
import static com.taxiappclone.rider.app.ModuleConfig.URL_GET_VEHICLE_TYPES;
import static com.taxiappclone.rider.app.ModuleConfig.URL_CREATE_RIDE_REQUEST;
import static com.taxiappclone.rider.app.ModuleConfig.URL_UPDATE_TOKEN;
import static com.taxiappclone.rider.notification.NotificationUtils.PUSH_NOTIFICATION;
import static com.taxiappclone.rider.notification.NotificationUtils.TYPE_NOTIFICATION;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FragmentDrawer drawerFragment;
    private SessionManager session;
    private ActionBar actionBar;

    private static final String TAG = HomeActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 1;

    private static String SET_PICKUP_LOCATION = "Set PickUp Location";
    private static String SET_DROPOFF_LOCATION = "Set DropOff Location";

    Marker mUserMarker;
    private PermissionManager permissionManager;
    private SupportMapFragment mapFragment;

    //BottomSheet
    ImageButton btnExpandable, btnMyLocation;
    //BottomSheetRiderFragment mBottomSheet;
    Button btnGetFare, btnSetPickupLocation, btnEditProfile, btnSelectTime, btnBookRide, btnNoVehicle;

    //PlaceAutocompleteFragment autoPlaceSource, autoPlaceDestination;

    String mPikcupAddress, mDropoffAddress;
    private List<LatLng> polyLineList;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;
    private IGoogleAPI mService;

    private Boolean mLocationUpdate = false;
    private Boolean mDirection = false;
    private Boolean showDetailsView = false;

    private LatLng latLngPickup, latLngDropOff;

    private Circle mSourceCircle;
    private Marker mSourceMarker, mDestinationMarker;
    private View mapView;

    private View viewDetails, viewAutoDestination, viewSetLocation;
    private TextView tvSource, tvDestination, tvDistance, tvName, tvProfile, tvPikcupTime, tvSelectTimeError;
    private AppCompatTextView tvRideInProgress;
    private LinearLayoutCompat layoutDriverDetail;
    private AppCompatTextView tvDriverName;
    private AppCompatTextView tvDriverMobile;
    private AppCompatTextView tvDestAddress;
    private AppCompatTextView ibCallDriver;
    private AppCompatTextView ibCancelRide;
    private CardView cardView;

    private ProgressBar progressBar;
    private ProgressDialog pDialog;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private View navHeader;

    private EditText inputSource, inputDestination;
    private NumberPicker passengerCountPicker;
    private int mYear = 0, mMonth, mDay, mHour, mMinute, mPassengerCount;
    private String mPickupTime;
    private Double mDistance;
    private int mDuration;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private boolean closeApp = true;

    DatabaseReference ref;
    GeoFire geoFire;
    private int radius = 1, distance = 1; //1km
    private static final int distanceLimit = 10; //km
    private BitmapDescriptor carIcon;

    //Car Animation
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition;
    private Marker carMarker;
    private LocationManager locationManager;
    private Criteria criteria;
    Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
            final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v = valueAnimator.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v) * startPosition.longitude;
                    lat = v * endPosition.latitude + (1 - v) * startPosition.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    carMarker.setPosition(newPos);
                    carMarker.setAnchor(0.5f, 0.5f);
                    carMarker.setRotation(getBearing(startPosition, newPos));
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .zoom(MAP_ZOOM)
                                    .target(newPos)
                                    .build()
                    ));
                }
            });
            valueAnimator.start();
            //handler.postDelayed(this,3000);
        }
    };
    private float start_rotation = 0;
    private String userId;
    private boolean isDriverFound;
    private String driverId = "";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View viewCarMicro, viewCarMini, viewCarSedan;
    private ImageView imgCarMicro, imgCarMini, imgCarSedan;
    private TextView tvCarMicro, tvCarMini, tvCarSedan, tvCarMicroTime, tvCarMiniTime, tvCarSedanTime;
    private String carType;
    private ValueAnimator polyLineAnimator;
    private boolean startPolyLineAnimation;
    private IFCMService mFCMService;
    private String myToken;
    private GeoQuery geoQueryFindDrivers;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    boolean getDriversStarted = false;
    private List<Driver> mDriverList = new ArrayList<Driver>();
    private List<Driver> mNewDriverList = new ArrayList<Driver>();
    private GeoQuery geoQueryFetchDrivers;
    private GeoFire geoFireFetchDrivers;
    private DatabaseReference dbDriversLocationRef;
    private int driverCount;
    private boolean bookingStarted = false;
    private String currentRideKey;
    private List<String> listDriversRejected = new ArrayList<>();
    private CircleImageView imgUser;
    private RecyclerView listViewVehicleTypes;
    private VehicleTypesListAdapter listAdapterVehicleType;
    private List<VehicleType> listItemsVehicleTypes = new ArrayList<>();
    private ImageButton btnSchedule;

    private APIService apiService;
    public static String totalFareForTrip;
    public static String paymentMode;
    public static String walletBalance = "0.1";

    RadioGroup rgPaymentMode;
    RadioButton rbCOD;
    RadioButton rbWallet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        /*actionBar .setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_appbar);*/

        setStatusBarGradient(this);

        apiService = ApiUtils.INSTANCE.getApiService();

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        rgPaymentMode = findViewById(R.id.rgPaymentMode);
        rbCOD = findViewById(R.id.rbCOD);
        rbWallet = findViewById(R.id.rbWallet);

        // Progress dialog
        pDialog = new ProgressDialog(this);
//        pDialog.setCancelable(false);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }
        setupNavigationDrawerContent(navigationView);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            startActivity(new Intent(this, EditRiderProfileActivity.class));
        });

        setUserDetails();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionManager = new PermissionManager(this, this);
            permissionManager.getLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //init view

        //autoPlaceSource = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_source);
        //autoPlaceDestination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_destination);
        viewAutoDestination = findViewById(R.id.view_auto_destination);
        viewDetails = findViewById(R.id.view_details);
        //viewGetFare = (View)findViewById(R.id.layout_get_fare);
        viewSetLocation = findViewById(R.id.layout_set_location);
        btnExpandable = findViewById(R.id.img_expandable);
        btnMyLocation = findViewById(R.id.btn_my_location);
        tvSource = findViewById(R.id.tv_source);
        tvDestination = findViewById(R.id.tv_destination);
        tvDistance = findViewById(R.id.tv_distance);
        btnGetFare = findViewById(R.id.btn_get_fare);
        progressBar = findViewById(R.id.progress_bar);
        tvRideInProgress = findViewById(R.id.tvRideInProgress);
        layoutDriverDetail = findViewById(R.id.layoutDriverDetail);
        tvDriverName = findViewById(R.id.tvDriverName);
        tvDriverMobile = findViewById(R.id.tvDriverMobile);
        tvDestAddress = findViewById(R.id.tvDestAddress);
        ibCallDriver = findViewById(R.id.ibCallDriver);
        ibCancelRide = findViewById(R.id.ibCancelRide);
        cardView = findViewById(R.id.cardView);
        inputSource = findViewById(R.id.input_source);
        inputDestination = findViewById(R.id.input_destination);
        tvPikcupTime = findViewById(R.id.tv_pickup_time);
        tvSelectTimeError = findViewById(R.id.tv_select_time_error);
        btnSelectTime = findViewById(R.id.btn_select_time);
        passengerCountPicker = findViewById(R.id.passanger_count_picker);
        btnBookRide = findViewById(R.id.btn_book_ride);
        btnNoVehicle = findViewById(R.id.btn_no_vehicle);
        btnSchedule = findViewById(R.id.btn_schedule);


        passengerCountPicker.setMax(10);
        passengerCountPicker.setMin(1);
        passengerCountPicker.setUnit(1);
        passengerCountPicker.setValue(1);

        //viewDetails.setTranslationY(viewDetails.getHeight());
        btnMyLocation.setOnClickListener(this);
        btnExpandable.setOnClickListener(this);
        btnGetFare.setOnClickListener(this);
        btnSelectTime.setOnClickListener(this);
        btnBookRide.setOnClickListener(this);
        btnSchedule.setOnClickListener(this);

        inputSource.setInputType(InputType.TYPE_NULL);
        inputDestination.setInputType(InputType.TYPE_NULL);

        inputSource.setOnClickListener(this);
        inputDestination.setOnClickListener(this);

        inputSource.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(HomeActivity.this, SelectPlaceActivity.class);
                    startActivityForResult(intent, SELECT_SOURCE);
                }
            }
        });
        inputDestination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                Intent intent = new Intent(HomeActivity.this, SelectPlaceActivity.class);
                startActivityForResult(intent, SELECT_DESTINATION);
            }
        });

        btnSetPickupLocation = findViewById(R.id.btn_set_pickup_location);
        btnSetPickupLocation.setOnClickListener(this);

        int height = 100;
        int width = 100;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_new);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        carIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myToken = FirebaseInstanceId.getInstance().getToken();

        ref = FirebaseDatabase.getInstance().getReference(TABLE_RIDERS_LOCATIONS);
        geoFire = new GeoFire(ref);

        dbDriversLocationRef = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_AVAILABLE);
        geoFireFetchDrivers = new GeoFire(dbDriversLocationRef);

        mService = Common.getGoogleAPI();
        mFCMService = Common.getFCMService();

        getUserWallet();
        setUpVehicleTypes();
//        setUpCarSelection();
        setUpLocation();
        updateTokenToServer();

        setRideStatus();
    }

    private void setUpVehicleTypes() {
        listViewVehicleTypes = findViewById(R.id.list_vehicle_types);
        listAdapterVehicleType = new VehicleTypesListAdapter(this, listItemsVehicleTypes) {
            @Override
            public void setFare(TextView tvCost, VehicleType vehicleType) {
                if (latLngPickup != null && latLngDropOff != null) {
                    String totalFare = String.format("%.2f", vehicleType.getBase_fare() + (mDistance * vehicleType.getPer_km_charge()) + ((mDuration / 60) * vehicleType.getPer_min_charge()));
                    Double driver_gst = Double.parseDouble(totalFare) * 0.05;
                    Double mbs_gst = Double.parseDouble(totalFare) * 0.18;
                    totalFare =  String.format("%.2f", Double.parseDouble(totalFare) + driver_gst + mbs_gst);
                    tvCost.setText("RS. " + totalFare);
                } else {
                    tvCost.setText("N/A");
                }
            }
        };
        listViewVehicleTypes.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listViewVehicleTypes.setLayoutManager(mLayoutManager);
        listViewVehicleTypes.setItemAnimator(new DefaultItemAnimator());
        try {
            listViewVehicleTypes.setAdapter(listAdapterVehicleType);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        listViewVehicleTypes.setNestedScrollingEnabled(false);
        if (!session.getStringValue("vehicle_types").equals("")) {

            listItemsVehicleTypes.addAll(new Gson().fromJson(session.getStringValue("vehicle_types"), new TypeToken<List<VehicleType>>() {
            }.getType()));

            listItemsVehicleTypes.get(0).setSelected(true);
            listAdapterVehicleType.setSelectedItem(0);
            listAdapterVehicleType.notifyDataSetChanged();
        }
        listViewVehicleTypes.addOnItemTouchListener(new RecyclerViewClickListener.RecyclerTouchListener(this, listViewVehicleTypes, new RecyclerViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                VehicleType vehicleType = listItemsVehicleTypes.get(position);
                if (listAdapterVehicleType.getSelectedItemPosition() != position) {
                    listDriversRejected.clear();
                    vehicleType.setSelected(true);
                    if (listAdapterVehicleType.getSelectedItemPosition() != -1)
                        listItemsVehicleTypes.get(listAdapterVehicleType.getSelectedItemPosition()).setSelected(false);
                    listAdapterVehicleType.setSelectedItem(position);
                    listAdapterVehicleType.notifyDataSetChanged();

                    if (vehicleType.isAvailable()) {
                        btnBookRide.setVisibility(View.VISIBLE);
                        btnNoVehicle.setVisibility(View.GONE);
                    } else {
                        btnBookRide.setVisibility(View.GONE);
                        btnNoVehicle.setVisibility(View.VISIBLE);
                    }
                    totalFareForTrip = String.format("%.2f", vehicleType.getBase_fare() + (mDistance * vehicleType.getPer_km_charge()) + ((mDuration / 60) * vehicleType.getPer_min_charge()));
                    Log.e(TAG, "onClick: " + walletBalance);
                    Log.e(TAG, "onClick: " + totalFareForTrip);
                    if (Double.parseDouble(walletBalance) >= Double.parseDouble(totalFareForTrip)) {
                        rbWallet.setEnabled(true);
                    } else {
                        rbWallet.setEnabled(false);
                        rbCOD.setChecked(true);
                    }
                } else {
                    VehicleTypeDetailFragment vehicleTypeDetailFragment = VehicleTypeDetailFragment.newInstance(new Gson().toJson(vehicleType), mDistance, mDuration);
                    vehicleTypeDetailFragment.show(getSupportFragmentManager(), "vehicle_type_detail");
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        getVehicleTypes();
    }

    private void setUserDetails() {
        tvProfile.setText(CommonUtils.getNameLetters(session.getStringValue("name")));
        tvName.setText(session.getStringValue("name"));
        Glide.with(this).load(URL_MAIN + "uploads/users/" + session.getStringValue("image")).into(imgUser);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_get_fare:

                break;
            case R.id.btn_my_location:
                displayLocation(true);
                btnMyLocation.setVisibility(View.GONE);
                break;
            case R.id.btn_set_pickup_location:
                setLocationByCameraPosition();
                break;
            case R.id.img_expandable:
                if (showDetailsView) {
                    showDetailsView = false;
                    viewDetails.animate().translationY(0);
                } else {
                    showDetailsView = true;
                    viewDetails.animate().translationY(viewDetails.getHeight() - 80);
                }
                break;
            case R.id.input_source:
                intent = new Intent(HomeActivity.this, SelectPlaceActivity.class);
                startActivityForResult(intent, SELECT_SOURCE);
                break;
            case R.id.input_destination:
                intent = new Intent(HomeActivity.this, SelectPlaceActivity.class);
                startActivityForResult(intent, SELECT_DESTINATION);
                break;
            case R.id.btn_select_time:
                setDateTime();
                break;
            case R.id.btn_book_ride:
                VehicleType vehicleType = listItemsVehicleTypes.get(listAdapterVehicleType.getSelectedItemPosition());
                createPickUpRequest(String.valueOf(vehicleType.getId()), userId, false);
                break;
            case R.id.btn_schedule:
                setDateTime();
                break;
        }
    }

    private void registerBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    String msgType = intent.getStringExtra("msg_type");
                    switch (msgType) {
                        case RIDE_ACCEPTED:
                            hideDialog();
                            showToast("Request Accepted");
                            Intent i = new Intent(HomeActivity.this, RiderTrackingActivity.class);
                            startActivity(i);
                            finish();
                            break;
                        case RIDE_REJECTED:
//                            showAlert(false,"","Ride request rejected by driver");
                            listDriversRejected.add(driverId);
                            findDriver();
                            break;
                        case RIDE_REQUESTED:
                            hideDialog();
                            showToast("Request Rejected");
                            break;
                        case TYPE_NOTIFICATION:
                            break;
                    }
                }
            }
        };
    }

    private void updateTokenToServer() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference token = db.getReference(TABLE_RIDERS).child(userId).child("token");
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
                            Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
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

    private void getVehicleTypes() {
        final Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", session.getStringValue("auth_token"));

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_GET_VEHICLE_TYPES, null, header);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        Log.d(TAG, "Get Vehicle Types Response: " + result);
                        if (jObj.optBoolean("status")) {
                            listItemsVehicleTypes.clear();
                            session.setValue("vehicle_types", jObj.getJSONArray("data").toString());
                            listItemsVehicleTypes.addAll(new Gson().fromJson(jObj.getJSONArray("data").toString(), new TypeToken<List<VehicleType>>() {
                            }.getType()));
                            listItemsVehicleTypes.get(0).setSelected(true);
                            listAdapterVehicleType.setSelectedItem(0);
                            listAdapterVehicleType.notifyDataSetChanged();
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


    private void setUpCarSelection() {
        viewCarMicro = findViewById(R.id.view_car_micro);
        viewCarMini = findViewById(R.id.view_car_mini);
        viewCarSedan = findViewById(R.id.view_car_sedan);

        imgCarMicro = findViewById(R.id.img_car_micro);
        imgCarMini = findViewById(R.id.img_car_mini);
        imgCarSedan = findViewById(R.id.img_car_sedan);

        tvCarMicro = findViewById(R.id.tv_car_micro);
        tvCarMini = findViewById(R.id.tv_car_mini);
        tvCarSedan = findViewById(R.id.tv_car_sedan);

        tvCarMicroTime = findViewById(R.id.tv_car_micro_time);
        tvCarMiniTime = findViewById(R.id.tv_car_mini_time);
        tvCarSedanTime = findViewById(R.id.tv_car_sedan_time);

        selectMini();

        viewCarMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMicro();
            }
        });
        viewCarMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMini();
            }
        });
        viewCarSedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSedan();
            }
        });
    }

    private void selectMicro() {
        carType = CAR_MICRO;
        imgCarMicro.setImageDrawable(getResources().getDrawable(R.drawable.ic_car_micro_white));
        imgCarMicro.setBackground(getResources().getDrawable(R.drawable.bg_circle_theme_color));
        tvCarMicro.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvCarMicroTime.setTextColor(getResources().getColor(R.color.text_color_dark));

        imgCarMini.setImageDrawable(getResources().getDrawable(R.drawable.ic_car_mini));
        imgCarMini.setBackground(getResources().getDrawable(R.drawable.bg_circle_white_with_border));
        tvCarMini.setTextColor(getResources().getColor(R.color.text_color_normal));
        tvCarMiniTime.setTextColor(getResources().getColor(R.color.text_color_light));

        imgCarSedan.setImageDrawable(getResources().getDrawable(R.drawable.ic_car_sedan));
        imgCarSedan.setBackground(getResources().getDrawable(R.drawable.bg_circle_white_with_border));
        tvCarSedan.setTextColor(getResources().getColor(R.color.text_color_normal));
        tvCarSedanTime.setTextColor(getResources().getColor(R.color.text_color_light));
    }

    private void selectMini() {
        carType = CAR_MINI;
        imgCarMini.setImageDrawable(getResources().getDrawable(R.drawable.ic_car_mini_white));
        imgCarMini.setBackground(getResources().getDrawable(R.drawable.bg_circle_theme_color));
        tvCarMini.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvCarMiniTime.setTextColor(getResources().getColor(R.color.text_color_dark));

        imgCarMicro.setImageDrawable(getResources().getDrawable(R.drawable.ic_car_micro));
        imgCarMicro.setBackground(getResources().getDrawable(R.drawable.bg_circle_white_with_border));
        tvCarMicro.setTextColor(getResources().getColor(R.color.text_color_normal));
        tvCarMicroTime.setTextColor(getResources().getColor(R.color.text_color_light));

        imgCarSedan.setImageDrawable(getResources().getDrawable(R.drawable.ic_car_sedan));
        imgCarSedan.setBackground(getResources().getDrawable(R.drawable.bg_circle_white_with_border));
        tvCarSedan.setTextColor(getResources().getColor(R.color.text_color_normal));
        tvCarSedanTime.setTextColor(getResources().getColor(R.color.text_color_light));
    }

    private void selectSedan() {
        carType = CAR_SEDAN;
        imgCarSedan.setImageDrawable(getResources().getDrawable(R.drawable.ic_car_sedan_white));
        imgCarSedan.setBackground(getResources().getDrawable(R.drawable.bg_circle_theme_color));
        tvCarSedan.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvCarSedanTime.setTextColor(getResources().getColor(R.color.text_color_dark));

        imgCarMini.setImageDrawable(getResources().getDrawable(R.drawable.ic_car_mini));
        imgCarMini.setBackground(getResources().getDrawable(R.drawable.bg_circle_white_with_border));
        tvCarMini.setTextColor(getResources().getColor(R.color.text_color_normal));
        tvCarMiniTime.setTextColor(getResources().getColor(R.color.text_color_light));

        imgCarMicro.setImageDrawable(getResources().getDrawable(R.drawable.ic_car_micro));
        imgCarMicro.setBackground(getResources().getDrawable(R.drawable.bg_circle_white_with_border));
        tvCarMicro.setTextColor(getResources().getColor(R.color.text_color_normal));
        tvCarMicroTime.setTextColor(getResources().getColor(R.color.text_color_light));
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

    private void updateCamera() {
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (criteria == null) {
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionManager.getLocationPermission();
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));

        float bearing = location.getBearing();
        CameraPosition newCamPos = new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()),
                18f,
                65.5f,
                bearing);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 3000, null);
    }

    private void createPickUpRequest(String vehicleType, String uId, boolean schedule) {
        DatabaseReference refRider = FirebaseDatabase.getInstance().getReference(TABLE_PICKUP_REQUESTS).child(userId);
        refRider.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String prevRideStatus = dataSnapshot.getChildren().iterator().next().child("ride_status").getValue().toString();
                String prevRideRequestedTime = dataSnapshot.getChildren().iterator().next().child("request_time").getValue().toString();
                Log.e(TAG, "onDataChange: " + prevRideStatus);
                boolean requestedOneMinuteAgo = false;
                if (prevRideStatus.equals(RIDE_REQUESTED)) {
                    long timeInMilliseconds = 0;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date mDate = sdf.parse(prevRideRequestedTime);
                        timeInMilliseconds = mDate.getTime();
                        System.out.println("Date in milli :: " + timeInMilliseconds);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (System.currentTimeMillis() - timeInMilliseconds >= 60 * 1000) {
                        requestedOneMinuteAgo = true;
                    }
                }

                if (requestedOneMinuteAgo ||
                        prevRideStatus.equals(RIDE_CANCELLED_BY_DRIVER) ||
                        prevRideStatus.equals(RIDE_CANCELLED_BY_RIDER) ||
                        prevRideStatus.equals(RIDE_FINISHED) ||
                        prevRideStatus.equals(RIDE_REACHED_DESTINATION) ||
                        prevRideStatus.equals(RIDE_COMPLETED)) {
                    if (schedule)
                        pDialog.setMessage("Booking...");
                    else
                        pDialog.setMessage("Finding your driver...");
                    showDialog();

                    VehicleType vehicleType1 = listItemsVehicleTypes.get(listAdapterVehicleType.getSelectedItemPosition());
                    totalFareForTrip = String.format("%.2f", vehicleType1.getBase_fare() + (mDistance * vehicleType1.getPer_km_charge()) + ((mDuration / 60) * vehicleType1.getPer_min_charge()));
                    Double driver_gst = Double.parseDouble(totalFareForTrip) * 0.05;
                    Double mbs_gst = Double.parseDouble(totalFareForTrip) * 0.18;

                    RideRequest ride = new RideRequest();
                    ride.setRider_id(uId);
                    ride.setRequest_time(getDateTimeString());
                    ride.setRider_location(new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
                    ride.setPickup_location(new Gson().toJson(new LatLng(latLngPickup.latitude, latLngPickup.longitude)));
                    ride.setDropoff_location(new Gson().toJson(new LatLng(latLngDropOff.latitude, latLngDropOff.longitude)));
                    ride.setPickup_address(mPikcupAddress);
                    ride.setDropoff_address(mDropoffAddress);
                    ride.setMbs_gst(mbs_gst.toString());
                    ride.setDriver_gst(driver_gst.toString());
                    ride.setRide_status(RIDE_REQUESTED);

                    if (schedule) {
                        ride.setScheduled("1");
                        ride.setScheduled_time(mPickupTime);
                    }

                    final Map<String, String> param = new HashMap<String, String>();
                    param.put("data", new Gson().toJson(ride));

                    final Map<String, String> header = new HashMap<String, String>();
                    header.put("Authorization", session.getStringValue("auth_token"));

                    mRegisterTask = new AsyncTask<Void, Void, String>() {
                        long startTime = System.currentTimeMillis();

                        @Override
                        protected String doInBackground(Void... params) {
                            startTime = System.currentTimeMillis();
                            String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_CREATE_RIDE_REQUEST, param, header);
                            return result;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            if (result != null) {
                                try {
                                    JSONObject jObj = new JSONObject(result);
                                    Log.d(TAG, "Create Ride Request Response: " + result);
                                    if (jObj.optBoolean("status")) {
                                        String ride_id = String.valueOf(jObj.getInt("ride_id"));
                                        if (schedule) {
                                            hideDialog();
                                            clearLocations();
                                            showAlert(false, "Booking Successful", "Your booking is successful.");
                                        } else {
                                            DatabaseReference rideRequests = FirebaseDatabase.getInstance().getReference(TABLE_PICKUP_REQUESTS);
//                                currentRideKey = rideRequests.push().getKey();
                                            ride.setRide_id(ride_id);
                                            rideRequests.child(uId).child(ride_id).setValue(ride).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    fetchNearbyDrivers(vehicleType, ride_id);
                                                    pDialog.dismiss();
                                                    clearLocations();
                                                    Toast toast = Toast.makeText(HomeActivity.this, "Ride requested successfully", Toast.LENGTH_LONG);
                                                    View view = toast.getView();
                                                    view.setBackgroundResource(R.color.colorPrimary);
                                                    toast.show();

//                                        findDriver();
                                                }
                                            });
                                        }
                                    } else {
                                        showToast("Ride request failed");
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
                } else {
                    showToast("Please finish previous ride first");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showToast("something went wrong, try again later.");
            }
        });
    }

    private void fetchNearbyDrivers(String vehicleType, String currentRideKey) {
        Location riderLocation = new Location("");
        riderLocation.setLatitude(latLngPickup.latitude);
        riderLocation.setLongitude(latLngPickup.longitude);

        DatabaseReference driverLocations = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_AVAILABLE);
        driverLocations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String dId = postSnapshot.getKey();
                    Double lat = (Double) postSnapshot.child("l").child("0").getValue();
                    Double lon = (Double) postSnapshot.child("l").child("1").getValue();
                    Location driverLocation = new Location("");
                    driverLocation.setLatitude(lat);
                    driverLocation.setLongitude(lon);

                    float distance = riderLocation.distanceTo(driverLocation);
                    if (distance < 5000) {
                        DatabaseReference drivers = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS);
                        drivers.child(dId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String token = dataSnapshot.child("token").getValue().toString();
                                String vehicle_type = dataSnapshot.child("vehicle_type").getValue().toString();
                                if (vehicleType.equals(vehicle_type)) {
                                    Log.e(TAG, "onDataChange: " + vehicleType + "\t" + vehicle_type + "\t" + token);
                                    notifyDrivers(currentRideKey, token);
                                } else if (Integer.parseInt(vehicleType) <= 4 && Integer.parseInt(vehicle_type) <= 4) {
                                    if (Integer.parseInt(vehicle_type) > Integer.parseInt(vehicleType)) {
                                        Log.e(TAG, "onDataChange: " + vehicleType + "\t" + vehicle_type + "\t" + token);
                                        notifyDrivers(currentRideKey, token);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                /*required*/
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                /*required*/
            }
        });
    }

    private void notifyDrivers(String ride_id, String token) {
        VehicleType vehicleType = listItemsVehicleTypes.get(listAdapterVehicleType.getSelectedItemPosition());
        totalFareForTrip = String.format("%.2f", vehicleType.getBase_fare() + (mDistance * vehicleType.getPer_km_charge()) + ((mDuration / 60) * vehicleType.getPer_min_charge()));
        int selectedId = rgPaymentMode.getCheckedRadioButtonId();
        AppCompatRadioButton selectedRB = findViewById(selectedId);
        paymentMode = selectedRB.getText().toString().toLowerCase();
        Double driver_gst = Double.parseDouble(totalFareForTrip) * 0.05;
        Double mbs_gst = Double.parseDouble(totalFareForTrip) * 0.18;
        totalFareForTrip =  String.format("%.2f", Double.parseDouble(totalFareForTrip) + driver_gst + mbs_gst);
        Log.e(TAG, "message: " + totalFareForTrip);
        Log.e(TAG, "message: " + paymentMode);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", "ride_request")
                .addFormDataPart("totalFare", totalFareForTrip)
                .addFormDataPart("paymentMode", paymentMode)
                .addFormDataPart("ride_id", ride_id)
                .addFormDataPart("token", token)
                .addFormDataPart("user_id", String.valueOf(AppController.getInstance().getCurrentUser().getFb_id()))
                .addFormDataPart("rider_token", myToken)
                .build();

        apiService.sendNotification(requestBody).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.e(TAG, "onResponse: notification sent");
                totalFareForTrip = "";
                paymentMode = "";
                rbCOD.setChecked(true);
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e(TAG, "onFailure: failed to send notification");
            }
        });
    }

    private void findDriver() {
        if (latLngPickup == null)
            return;
        isDriverFound = false;
        DatabaseReference driversAvailable = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_AVAILABLE);
        GeoFire gfDrivers = new GeoFire(driversAvailable);
        geoQueryFindDrivers = gfDrivers.queryAtLocation(new GeoLocation(latLngPickup.latitude, latLngPickup.longitude), radius);
        geoQueryFindDrivers.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //If driver found
                if (!listDriversRejected.contains(key)) {
                    DatabaseReference drivers = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS);
                    drivers.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Driver driver = dataSnapshot.getValue(Driver.class);
                            if (driver.getVehicle_type() == listItemsVehicleTypes.get(listAdapterVehicleType.getSelectedItemPosition()).getId()) {
                                if (!isDriverFound) {
                                    isDriverFound = true;
                                    driverId = key;
                                    Log.d(TAG, "Driver Found: " + key);
                                    pDialog.setMessage("Requesting Driver...");
                                    callDriver(key);
                                    geoQueryFindDrivers.removeAllListeners();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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
                //If driver not found
                if (!isDriverFound) {
                    geoQueryFindDrivers.removeAllListeners();
                    if (radius > 10) {
                        hideDialog();
                        showAlert(false, "Driver not found!", "No drivers near by you.");
                    } else {
                        radius++;
                        findDriver();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void callDriver(String driverId) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS);
        tokens.orderByKey().equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            FCMToken token = postSnapshot.getValue(FCMToken.class);
                            Data data = new Data();
                            data.setType(RIDE_REQUEST);
                            data.setRide_id(currentRideKey);
                            data.setUser_id(userId);
                            data.setToken(myToken);
                            FCMMessage message = new FCMMessage(data, token.getToken());
                            JSONObject messageParams = null;
                            try {
                                messageParams = new JSONObject(new Gson().toJson(message));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new SendPushNotification(HomeActivity.this, messageParams, new SendPushNotification.OnResponseListener() {
                                @Override
                                public void onSuccessListener() {

                                }

                                @Override
                                public void onFailedListener() {

                                }

                                @Override
                                public void onErrorListener() {

                                }
                            });
                            /*mRegisterTask = new AsyncTask<Void, Void, String>() {
                                long startTime = System.currentTimeMillis();
                                @Override
                                protected void onPreExecute() {
                                    showDialog();
                                }
                                @Override
                                protected String doInBackground(Void... voids) {
                                    startTime = System.currentTimeMillis();
                                    String result = ServerUtilities.sendFCMMessage(finalMessageParams);
                                    return result;
                                }
                                @Override
                                protected void onPostExecute(String result) {
                                    Log.d(TAG, "FCM Response: " + result.toString());
                                    if (result != null) {
                                        FCMResponse response = new Gson().fromJson(result,FCMResponse.class);

                                        *//*if(response.getSuccess() == 1)
                                            showToast("Request Sent!");
                                        else
                                            showToast("Request Failed!");*//*
                                    } else {
                                        showToast("Some error occurred!");
                                    }
                                    //hideDialog();
                                    mRegisterTask = null;
                                }
                            };
                            mRegisterTask.execute(null, null, null);*/
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setDateTime() {
        // Get Current Date
        if (mYear == 0) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear + 1;
                        mDay = dayOfMonth;
                        //tvPikcupTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        mHour = hourOfDay;
                                        mMinute = minute;
                                        String strDateTime = mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMinute + ":00";
                                        tvPikcupTime.setText(mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMinute);
                                        mPickupTime = CommonUtils.convertDateFormat(strDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
                                        VehicleType vehicleType = listItemsVehicleTypes.get(listAdapterVehicleType.getSelectedItemPosition());
                                        createPickUpRequest(String.valueOf(vehicleType.getId()), userId, true);
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showDetails(String mSource, String mDestination) {

        boolean is4Available = false;
        boolean is3Available = false;
        boolean is2Available = false;
        boolean is1Available = false;
        for (VehicleType v : listItemsVehicleTypes) {
            if (v.getId() == 4 && v.isAvailable()) {
                is4Available = true;
            } else if (v.getId() == 3 && v.isAvailable()) {
                is3Available = true;
            } else if (v.getId() == 2 && v.isAvailable()) {
                is2Available = true;
            } else if (v.getId() == 1 && v.isAvailable()) {
                is1Available = true;
            }
        }

        List<VehicleType> tempVehicleList = new ArrayList<>();
        for (VehicleType item : listItemsVehicleTypes) {
            if (is4Available) {
                if (item.getId() == 4 || item.getId() == 3 || item.getId() == 2 || item.getId() == 1) {
                    item.setAvailable(true);
                }
            } else if (is3Available) {
                if (item.getId() == 3 || item.getId() == 2 || item.getId() == 1) {
                    item.setAvailable(true);
                }
            } else if (is2Available) {
                if (item.getId() == 2 || item.getId() == 1) {
                    item.setAvailable(true);
                }
            } else if (is1Available) {
                if (item.getId() == 2 || item.getId() == 1) {
                    item.setAvailable(true);
                }
            }
            tempVehicleList.add(item);
        }
        listItemsVehicleTypes.clear();
        listItemsVehicleTypes.addAll(tempVehicleList);

        tvSource.setText(mSource);
        tvDestination.setText(mDestination);
        //viewDetails.setVisibility(View.VISIBLE);
        viewDetails.animate().translationY(0);
        listAdapterVehicleType.notifyDataSetChanged();
        closeApp = false;
        if (!listItemsVehicleTypes.isEmpty()) {
            if (listItemsVehicleTypes.get(listAdapterVehicleType.getSelectedItemPosition()).isAvailable()) {
                btnBookRide.setVisibility(View.VISIBLE);
                btnNoVehicle.setVisibility(View.GONE);
            }
        }
        VehicleType vehicleType = listItemsVehicleTypes.get(0);
        totalFareForTrip = String.format("%.2f", vehicleType.getBase_fare() + (mDistance * vehicleType.getPer_km_charge()) + ((mDuration / 60) * vehicleType.getPer_min_charge()));
        if (Double.parseDouble(walletBalance) >= Double.parseDouble(totalFareForTrip)) {
            rbWallet.setEnabled(true);
        } else {
            rbWallet.setEnabled(false);
            rbCOD.setChecked(true);
        }
    }

    private void setLocationByCameraPosition() {
        progressBar.setVisibility(View.VISIBLE);
        //stopLocationUpdates();
        LatLng camPos = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        String requestApi = null;
        final Double centerLat = camPos.latitude;
        final Double centerLng = camPos.longitude;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/geocode/json?" +
                    "latlng=" + centerLat + "," + centerLng +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.google_api_browser_key);
            Log.d("request API", requestApi);
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("results");

                                JSONObject jObj = jsonArray.getJSONObject(0);
                                JSONObject locationObject = jObj.getJSONObject("geometry").getJSONObject("location");

                                if (btnSetPickupLocation.getText().equals(SET_PICKUP_LOCATION)) {
                                    mPikcupAddress = jObj.getString("formatted_address");
                                    /*latLngPickup = new LatLng(locationObject.getDouble("lat")
                                            ,locationObject.getDouble("lng"));*/
                                    latLngPickup = new LatLng(centerLat, centerLng);
                                    bookingStarted = true;
//                                    showToast("Pick-Up Address:- "+mSourceAddress);
                                    //autoPlaceSource.setText(mSourceAddress);
                                    inputSource.setText(mPikcupAddress);
                                    if (mSourceMarker != null)
                                        mSourceMarker.remove();
                                    mSourceMarker = mMap.addMarker(new MarkerOptions().position(latLngPickup)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_dot))
                                            .title("PickUp Location"));
                                    btnSetPickupLocation.setText(SET_DROPOFF_LOCATION);
                                    viewAutoDestination.setVisibility(View.VISIBLE);
                                } else {
                                    mDropoffAddress = jObj.getString("formatted_address");
                                    /*latLngDropOff = new LatLng(locationObject.getDouble("lat")
                                            ,locationObject.getDouble("lng"));*/
                                    latLngDropOff = new LatLng(centerLat, centerLng);
//                                    showToast("Drop-Off Address:- "+mDestinationAddress);
                                    //autoPlaceDestination.setText(mDropOffAddress);
                                    inputDestination.setText(mDropoffAddress);
                                    bookingStarted = true;
                                    if (mDestinationMarker != null)
                                        mDestinationMarker.remove();
                                    mDestinationMarker = mMap.addMarker(new MarkerOptions().position(latLngDropOff)
                                            .icon(BitmapDescriptorFactory.defaultMarker(0000000f))
                                            .title("DropOff Location"));
                                    getDirection(latLngPickup, latLngDropOff);
                                    //autoPlaceDestination.setText(mDropOffAddress);
                                    inputDestination.setText(mDropoffAddress);
                                    btnSetPickupLocation.setText(SET_PICKUP_LOCATION);
                                }
                                progressBar.setVisibility(View.GONE);
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

    private void getDirection(final LatLng latLngSource, final LatLng latLngDestination) {
        startPolyLineAnimation = false;
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
                                    btnSetPickupLocation.setText(SET_PICKUP_LOCATION);
                                    bookingStarted = false;
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

                                    latLngPickup = polyLineList.get(0);
                                    latLngDropOff = polyLineList.get(polyLineList.size() - 1);
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
                                    mSourceMarker = mMap.addMarker(new MarkerOptions()
                                            .position(latLngPickup)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_dot))
                                            .title("PickUp Location"));
                                    //Animation
                                    startPolyLineAnimation = true;
                                    startPolyLineAnimation();

                                    if (mDestinationMarker != null)
                                        mDestinationMarker.remove();
                                    mDestinationMarker = mMap.addMarker(new MarkerOptions()
                                            .position(latLngDropOff)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place_black))
                                            .title("DropOff Location"));

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

                                    viewSetLocation.setVisibility(View.GONE);
                                    showDetails(mPikcupAddress, mDropoffAddress);

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

    private void startPolyLineAnimation() {
        if (startPolyLineAnimation) {
            polyLineAnimator = ValueAnimator.ofInt(0, 100);
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
                    if (((int) valueAnimator.getAnimatedValue()) == 100) {
                        startPolyLineAnimation();
                    }
                }
            });
            polyLineAnimator.start();
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
            tvDistance.setText("Distance: " + distance_text + " (Time: " + duration_text + ")");
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
            else if (latLngPickup == null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), MAP_ZOOM));

            /*if(!getDriversStarted)
                getAllDrivers();*/
        } else {
            showToast("Please enable location on your device");
            Log.d("ERROR", "Can not get your location");
        }
        btnMyLocation.setVisibility(View.GONE);
    }

    private void getAllDrivers() {
        mNewDriverList.clear();

        mMap.setOnMarkerClickListener(marker -> true);

//        showToast("size : "+mDriverList.size());
        /*if(mDriverList.size()>0) {
            for (Driver driver : mDriverList) {
                driver.getMarker().remove();
                mDriverList.remove(driver);
            }
        }*/

        LatLng camPos = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        for (Iterator<Driver> iterator = mDriverList.iterator(); iterator.hasNext(); ) {
            Driver driver = iterator.next();
            double distance = CommonUtils.getDistanceByLatLng(camPos, driver.getMarker().getPosition(), 'K');
//            showToast("distance : "+distance);
            if (distance > distanceLimit) {
                driver.getMarker().remove();
                iterator.remove();
            }
        }
        /*for(Driver driver : mDriverList)
        {
            double distance = CommonUtility.getDistanceByLatLng(camPos,driver.getMarker().getPosition(),'K');
//            showToast("distance : "+distance);
            if(distance>distanceLimit)
            {
                driver.getMarker().remove();
                mDriverList.remove(driver);
            }
        }*/

        driverCount = mDriverList.size();
        getDriversStarted = true;
        if (geoQueryFetchDrivers != null)
            geoQueryFetchDrivers.removeAllListeners();

        geoQueryFetchDrivers = geoFireFetchDrivers.queryAtLocation(new GeoLocation(camPos.latitude, camPos.longitude), distanceLimit);
        geoQueryFetchDrivers.removeAllListeners();

        geoQueryFetchDrivers.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                //Use key to get email from table divers
                //Table drivers contain all registered drivers' information
                Boolean driverMatched = false;
                for (Driver driver : mDriverList) {
                    try {
                        if (driver.getMarker().getTag().equals(key)) {
                            driverMatched = true;
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (driverCount < 8 && !driverMatched) {
                    FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS)
                            .child(key)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //get Driver information
                                    Log.d("HomeActivity", "Driver Detail " + dataSnapshot.toString());
                                    Driver newDriver = dataSnapshot.getValue(Driver.class);
                                    LatLng driverLatLng = new LatLng(location.latitude, location.longitude);
                                    Marker mDriverMarker = null;
                                    mDriverMarker = mMap.addMarker(new MarkerOptions()
                                            .position(driverLatLng)
                                            .flat(true)
                                            .title(newDriver.getName())
                                            .snippet(newDriver.getMobile())
                                            .icon(carIcon));
                                    mDriverMarker.setTag(key);
                                    driverCount++;
                                    mDriverList.add(newDriver);
                                    enableVehicle();
                                    newDriver.setMarker(mDriverMarker);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onKeyExited(String key) {
                for (Driver driver : mDriverList) {
                    try {
                        if (driver.getMarker() != null && driver.getMarker().getTag().equals(key)) {
                            driver.getMarker().remove();
                            mDriverList.remove(driver);
                            driverCount--;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Driver driver : mDriverList) {
                    try {
                        if (driver.getMarker().getTag().equals(key)) {
                            startPosition = driver.getMarker().getPosition();
                            endPosition = new LatLng(location.latitude, location.longitude);
                            animateMarkerNew(endPosition, driver.getMarker());
                            //checkDriverOnDuty(key);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

                /*if(distance <= distanceLimit)
                {
                    distance++;
                    loadAllDrivers();
                }*/
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void enableVehicle() {
        for (VehicleType vehicleType : listItemsVehicleTypes) {
            boolean available = false;
            for (Driver driver : mDriverList) {
                if (driver.getVehicle_type() == vehicleType.getId()) {
                    available = true;
                }
            }
            vehicleType.setAvailable(available);
        }
        listAdapterVehicleType.notifyDataSetChanged();
        if (!listItemsVehicleTypes.isEmpty()) {
            if (listItemsVehicleTypes.get(listAdapterVehicleType.getSelectedItemPosition()).isAvailable()) {
                btnBookRide.setVisibility(View.VISIBLE);
                btnNoVehicle.setVisibility(View.GONE);
            }
        }
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
                //showToast("Camera started");
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
//                if(latLngPickup==null)
                getAllDrivers();
            }
        });
    }

    private void setRideStatus() {
        DatabaseReference refRider = FirebaseDatabase.getInstance().getReference(TABLE_PICKUP_REQUESTS).child(userId);
        refRider.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String prevRideId = dataSnapshot.getChildren().iterator().next().child("ride_id").getValue().toString();
                    String prevRideStatus = dataSnapshot.getChildren().iterator().next().child("ride_status").getValue().toString();

                    if (prevRideStatus.equals(RIDE_REQUESTED) ||
                            prevRideStatus.equals(RIDE_CANCELLED_BY_DRIVER) ||
                            prevRideStatus.equals(RIDE_CANCELLED_BY_RIDER) ||
                            prevRideStatus.equals(RIDE_FINISHED) ||
                            prevRideStatus.equals(RIDE_REACHED_DESTINATION) ||
                            prevRideStatus.equals(RIDE_COMPLETED)
                    ) {
                        tvRideInProgress.setVisibility(View.INVISIBLE);
                        layoutDriverDetail.setVisibility(View.INVISIBLE);
                        cardView.setVisibility(View.VISIBLE);
                        viewSetLocation.setVisibility(View.VISIBLE);
                        mMap.clear();
                    } else {
                        tvDestAddress.setText("");
                        tvRideInProgress.setVisibility(View.VISIBLE);
                        layoutDriverDetail.setVisibility(View.VISIBLE);
                        cardView.setVisibility(View.INVISIBLE);
                        viewSetLocation.setVisibility(View.INVISIBLE);

                        JsonParser parser = new JsonParser();
                        JsonObject pickUpObject = parser.parse(dataSnapshot.getChildren().iterator().next().child("pickup_location").getValue().toString()).getAsJsonObject();

                        Location pickupLocation = new Location("");
                        pickupLocation.setLatitude(pickUpObject.get("latitude").getAsDouble());
                        pickupLocation.setLongitude(pickUpObject.get("longitude").getAsDouble());

                        String driverId = dataSnapshot.getChildren().iterator().next().child("driver_id").getValue().toString();
                        if (prevRideStatus.equals(RIDE_ACCEPTED)) {
                            FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_ON_DUTY)
                                    .child(driverId).child("l").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Double lat = (Double) dataSnapshot.child("0").getValue();
                                    Double lon = (Double) dataSnapshot.child("1").getValue();
                                    if (lat == null || lon == null) {
                                        return;
                                    }

                                    LatLng latLng = new LatLng(lat, lon);

                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.icon(bitmapDescriptorFromVector(HomeActivity.this, R.drawable.ic_driver_location));
                                    markerOptions.position(latLng);
                                    markerOptions.title("Driver");
                                    mMap.clear();
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.addMarker(markerOptions);

                                    try {
                                        String requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                                                "mode=driving&" +
                                                "transit_routing_preference=less_driving&" +
                                                "origin=" + lat + "," + lon + "&" +
                                                "destination=" + pickupLocation.getLatitude() + "," + pickupLocation.getLongitude() + "&" +
                                                "key=" + getResources().getString(R.string.google_api_browser_key);
                                        Log.d("request API", requestApi);
                                        mService.getPath(requestApi)
                                                .enqueue(new Callback<String>() {
                                                    @Override
                                                    public void onResponse(Call<String> call, Response<String> response) {
                                                        try {
                                                            JSONObject jObj = new JSONObject(response.body().toString());
                                                            JSONArray routes = jObj.getJSONArray("routes");

                                                            JSONObject object = routes.getJSONObject(0);
                                                            JSONArray legs = object.getJSONArray("legs");

                                                            JSONObject legsObject = legs.getJSONObject(0);

                                                            //Get Distance
                                                            JSONObject distance = legsObject.getJSONObject("distance");
                                                            JSONObject duration = legsObject.getJSONObject("duration");
                                                            tvDestAddress.setText("Driver is " + distance.getString("text") + " away. You will be picked up in " + duration.getString("text") + ".");
                                                            tvDistance.setText(distance.getString("text"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                    @Override
                                                    public void onFailure(Call<String> call, Throwable t) {
                                                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else if (prevRideStatus.equals(RIDE_DRIVER_ARRIVED)) {
                            tvDestAddress.setText("Driver arrived at pick up location.");
                        } else if (prevRideStatus.equals(RIDE_STARTED)) {
                            tvDestAddress.setText("Destination: " + dataSnapshot.getChildren().iterator().next().child("dropoff_address").getValue().toString());
                        }
                        if (!driverId.isEmpty()) {
                            FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS)
                                    .child(driverId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Driver driver = dataSnapshot.getValue(Driver.class);
                                            tvDriverName.setText("Driver Name: " + driver.getName());
                                            tvDriverMobile.setText("Mobile Number: " + driver.getMobile());
                                            ibCallDriver.setOnClickListener(v -> {
                                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", driver.getMobile(), null));
                                                startActivity(intent);
                                            });
                                            ibCancelRide.setOnClickListener(v -> {
                                                refRider.child(prevRideId).child("ride_status").setValue(RIDE_CANCELLED_BY_RIDER).addOnCompleteListener(task -> {
                                                    showToast("Ride cancelled.");
                                                });
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
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

                        mPikcupAddress = extras.getString("address");
                        inputSource.setText(mPikcupAddress);
                        latLngPickup = new LatLng(latitude, longitude);
                        bookingStarted = true;

                        if (mSourceMarker != null)
                            mSourceMarker.remove();
                        mSourceMarker = mMap.addMarker(new MarkerOptions().position(latLngPickup)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_dot))
                                .title("PickUp Location"));
                        btnSetPickupLocation.setText(SET_DROPOFF_LOCATION);
                        btnSetPickupLocation.setText(SET_DROPOFF_LOCATION);
                        viewAutoDestination.setVisibility(View.VISIBLE);
                        if (mDropoffAddress != null)
                            getDirection(latLngPickup, latLngDropOff);
                        else
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPickup, MAP_ZOOM));
                    }
                    break;
                case SELECT_DESTINATION:
                    extras = intent.getExtras();
                    if (extras != null) {
                        double latitude = extras.getDouble("latitude");
                        double longitude = extras.getDouble("longitude");

                        mDropoffAddress = extras.getString("address");
                        inputDestination.setText(mDropoffAddress);
                        latLngDropOff = new LatLng(latitude, longitude);
                        bookingStarted = true;
                        if (mDestinationMarker != null)
                            mDestinationMarker.remove();
                        mDestinationMarker = mMap.addMarker(new MarkerOptions().position(latLngDropOff)
                                .icon(BitmapDescriptorFactory.defaultMarker(0000000f))
                                .title("DropOff Location"));
                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngDropOff, MAP_ZOOM));

                        //mBottomSheet = BottomSheetRiderFragment.newInstance(mSourceAddress, mDropOffAddress);
                        //mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());
                        if (mPikcupAddress != null)
                            getDirection(latLngPickup, latLngDropOff);
                    }
                    break;
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showAlert(boolean status, String title, String msg) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this);
        }
        // Setting Dialog Title
        builder.setTitle(title);

        // Setting Dialog Message
        builder.setMessage(msg);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        // Showing Alert Message
        alertDialog.show();
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
        setUserDetails();
        getUserWallet();
    }

    private void getUserWallet() {
        final Map<String, String> param = new HashMap<String, String>();
        param.put("mobile", session.getStringValue("mobile"));

        final Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", session.getStringValue("auth_token"));

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_GET_USER, param, header);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        Log.d(TAG, "User Detail Response: " + result);
                        if (jObj.optBoolean("status")) {
                            String userData = jObj.getJSONObject("data").toString();
                            session.setValue(AppConfig.USER, userData);
                            User user = AppController.getInstance().getUser();
                            walletBalance = user.getWallet();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        } else {
            if (closeApp) {
                super.onBackPressed();
            }/*else if(viewDetails.getTranslationY()<viewDetails.getHeight()-80)
            {
                viewDetails.animate().translationY(viewDetails.getHeight()-80);
            }*/ else {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("Go back?");
                builder.setMessage("This will clear your selected locations ?");
                builder.setPositiveButton("GO BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //clear all details               
                        clearLocations();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("KEEP BOOKING", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                    }
                });
                androidx.appcompat.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void clearLocations() {
        inputSource.setText("");
        inputDestination.setText("");
        mSourceMarker.remove();
        mDestinationMarker.remove();
        if (polyLineList != null)
            polyLineList.clear();
        if (greyPolyline != null)
            greyPolyline.remove();
        if (blackPolyline != null)
            blackPolyline.remove();
        viewDetails.animate().translationY(viewDetails.getHeight());
        viewAutoDestination.setVisibility(View.GONE);
        viewSetLocation.setVisibility(View.VISIBLE);
        btnSetPickupLocation.setText(SET_PICKUP_LOCATION);
        startPolyLineAnimation = false;
        closeApp = true;
        latLngPickup = null;
        latLngDropOff = null;
        listDriversRejected.clear();
    }

    @Override
    protected void onPause() {
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
