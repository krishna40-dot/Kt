package com.taxiappclone.driver.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.taxiappclone.common.activity.CustomActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.model.Vehicle;
import com.taxiappclone.common.model.VehicleDetailsResponse;
import com.taxiappclone.common.model.VehicleDetails;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.taxiappclone.common.remote.IGoogleAPI;
import com.taxiappclone.driver.R;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.RideRequest;
import com.taxiappclone.common.notification.SendPushNotification;
import com.taxiappclone.common.notification.model.Data;
import com.taxiappclone.common.notification.model.FCMMessage;
import com.taxiappclone.common.notification.model.FCMResponse;
import com.taxiappclone.common.remote.Common;
import com.taxiappclone.driver.app.AppController;
import com.taxiappclone.driver.service.UpdateLocationService;
import com.taxiappclone.common.utils.ServerUtilities;
import com.taxiappclone.common.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_AVAILABLE;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_ON_DUTY;
import static com.taxiappclone.common.app.AppConfig.TABLE_PICKUP_REQUESTS;
import static com.taxiappclone.common.app.AppConfig.mLastLocation;
import static com.taxiappclone.common.app.Constants.DRIVER_ID;
import static com.taxiappclone.common.app.Constants.DRIVER_ONLINE;
import static com.taxiappclone.common.app.Constants.ON_DUTY;
import static com.taxiappclone.common.app.Constants.RIDER_ID;
import static com.taxiappclone.common.app.Constants.RIDER_TOKEN;
import static com.taxiappclone.common.app.Constants.RIDE_ACCEPTED;
import static com.taxiappclone.common.app.Constants.RIDE_ACCEPT_TIME;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REJECTED;
import static com.taxiappclone.common.app.Constants.RIDE_STATUS;

public class RiderCallActivity extends CustomActivity implements View.OnClickListener {

    private static final String TAG = RiderCallActivity.class.getSimpleName();
    private TextView tvTime, tvDistance, tvAddress;
    MediaPlayer mediaPlayer;
    private LatLng currentPosition;
    private IGoogleAPI mService;
    private ProgressBar progressBar;
    private Handler handler;
    private int delay = 1000; //milliseconds
    private Runnable myRunnable = null;

    private int progressMin = 0, progress = 60;
    private PowerManager.WakeLock wakeLock;
    private CircleImageView imgMap;
    private String riderId, userId, myToken, riderToken, rideId;
    private RideRequest ride;
    private LatLng riderPickup, riderDropOff, riderLocation;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private ProgressDialog pDialog;
    private boolean processing = false;
    private Bundle extras;
    private SessionManager session;
    private Gson gson;
    private DatabaseReference refRideRequest;
    private DatabaseReference driverLocationRef;
    private Button btnAccept, btnReject;

    private APIService apiService;

    String ride_id;
    String totalFare;
    String paymentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | +WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);*/

        setContentView(R.layout.activity_rider_call);

        apiService = ApiUtils.INSTANCE.getApiService();

        tvTime = findViewById(R.id.tv_time);
        tvDistance = findViewById(R.id.tv_distance);
        tvAddress = findViewById(R.id.tv_address);
        progressBar = findViewById(R.id.progress_bar);
        imgMap = findViewById(R.id.img_map);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);

        btnAccept.setOnClickListener(this);
        btnReject.setOnClickListener(this);

        imgMap.setOnClickListener(this);
        pDialog = new ProgressDialog(this);

        session = new SessionManager(this);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mService = Common.getGoogleAPI();
        gson = new Gson();
        if (getIntent() != null) {
            extras = getIntent().getExtras();
            /*riderPickup = new Gson().fromJson(extras.getString("pickup"), LatLng.class);
            riderDropOff = new Gson().fromJson(extras.getString("dropoff"), LatLng.class);*/
            totalFare = extras.getString("totalFare");
            paymentMode = extras.getString("paymentMode");
            rideId = extras.getString(RIDE_ID);
            riderId = extras.getString(RIDER_ID);
            riderToken = extras.getString(RIDER_TOKEN);
            Log.e(TAG, "onCreate: " + rideId);
            Log.e(TAG, "onCreate: " + riderId);
            refRideRequest = FirebaseDatabase.getInstance().getReference(TABLE_PICKUP_REQUESTS).child(riderId).child(rideId);
            refRideRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ride = dataSnapshot.getValue(RideRequest.class);
                    if (ride != null) {
                        ride_id = ride.getRide_id();
                        riderLocation = gson.fromJson(ride.getRider_location(), LatLng.class);
                        riderPickup = gson.fromJson(ride.getPickup_location(), LatLng.class);
                        riderDropOff = gson.fromJson(ride.getDropoff_location(), LatLng.class);
                    }
                    if (riderLocation != null)
                        getDirection(riderPickup.latitude, riderPickup.longitude);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myToken = FirebaseInstanceId.getInstance().getToken();
        startAnimation();
        startMediaPlayer();
    }

    private void startMediaPlayer() {
        Handler handler = new Handler();
        handler.post(() -> {
            mediaPlayer = MediaPlayer.create(RiderCallActivity.this, R.raw.ringtone3);
            /*try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        });
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startAnimation() {
        progressBar.setMax(60);
        myRunnable = new Runnable() {
            public void run() {
                //do something
                progressBar.setProgress(progress);
                if (progress == 0) {
                    if (!processing)
                        rejectRequest();
                } else {
                    handler.postDelayed(this, delay);
                    progress--;
                }
            }
        };
        handler = new Handler();
        handler.post(myRunnable);
    }

    private void disconnectDriver(Intent intent) {
        if (UpdateLocationService.isRunning())
            stopService(new Intent(this, UpdateLocationService.class));

        session.setValue(DRIVER_ONLINE, false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_AVAILABLE);
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, (key, error) -> {
            hideDialog();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        });
    }

    private Double mDistance;
    private int mDuration;

    @SuppressLint("StaticFieldLeak")
    private void acceptRequest() {
        showDialog();
        stopMediaPlayer();
        processing = true;
        pDialog.setMessage("Accepting request...");
        session.setValue(ON_DUTY, true);
        session.setValue(RIDE_STATUS, "accepted");
        session.setValue(RIDE_ID, rideId);
        session.setValue(RIDER_ID, riderId);
        session.setValue(RIDER_TOKEN, riderToken);
        String jsonLatLng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        Log.d(TAG, "My Location: " + jsonLatLng);
        refRideRequest.child(DRIVER_ID).setValue(userId);
        refRideRequest.child(RIDE_STATUS).setValue("accepted");
        refRideRequest.child(RIDE_ACCEPT_TIME).setValue(CommonUtils.getDateTimeString());

        String requestApi = null;
        try {
            String sourceAddress = riderPickup.latitude + "," + riderPickup.longitude;
            String destinationAddress = riderDropOff.latitude + "," + riderDropOff.longitude;
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
                                JSONArray routes = jObj.getJSONArray("routes");
                                String status = jObj.getString("status");
                                if (!status.equals("NOT_FOUND") && routes.length() != 0) {
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

        Location pickupLocation = new Location("");
        pickupLocation.setLatitude(riderPickup.latitude);
        pickupLocation.setLongitude(riderPickup.longitude);
        Location dropLocation = new Location("");
        dropLocation.setLatitude(riderDropOff.latitude);
        dropLocation.setLongitude(riderDropOff.longitude);

        float distance = pickupLocation.distanceTo(dropLocation);

        Log.e(TAG, "acceptRequest: " + ride_id);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("ride_id", ride_id)
                .addFormDataPart("driver_location", new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())))
                .addFormDataPart("driver_id", String.valueOf(AppController.getInstance().getCurrentUserId()))
                .addFormDataPart("distance_travelled", String.valueOf(distance))
                .addFormDataPart("accept_time", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()))
                .addFormDataPart("ride_status", "accepted")
                .addFormDataPart("fare_details", "")
                .addFormDataPart("fare", totalFare)
                .addFormDataPart("start_time", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())
                )
                .build();

        apiService.updateRideDetail(requestBody).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.e(TAG, "onResponse: Ride accepted");
                Intent intent = new Intent(RiderCallActivity.this, DriverTrackingActivity.class);
                intent.putExtra("totalFare", totalFare);
                intent.putExtra("paymentMode", paymentMode);
                disconnectDriver(intent);
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e(TAG, "onFailure: failed to accept ride");
                hideDialog();
            }
        });

        driverLocationRef = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_ON_DUTY);
        GeoFire geoFire = new GeoFire(driverLocationRef);
        geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Data data = new Data();
                data.setType(RIDE_ACCEPTED);
                data.setTitle(getResources().getString(R.string.ride_accepted));
                data.setMessage(getResources().getString(R.string.driver_coming));
                data.setRide_id(rideId);
                data.setUser_id(userId);
                data.setToken(myToken);
                FCMMessage message = new FCMMessage(data, riderToken);
                JSONObject messageParams = null;
                try {
                    messageParams = new JSONObject(new Gson().toJson(message));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new SendPushNotification(RiderCallActivity.this, messageParams, new SendPushNotification.OnResponseListener() {
                    @Override
                    public void onSuccessListener() {
                        Intent intent = new Intent(RiderCallActivity.this, DriverTrackingActivity.class);
                        intent.putExtra("totalFare", totalFare);
                        intent.putExtra("paymentMode", paymentMode);
                        disconnectDriver(intent);
                    }

                    @Override
                    public void onFailedListener() {
                        hideDialog();
                    }

                    @Override
                    public void onErrorListener() {

                    }
                });
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void rejectRequest() {
        stopMediaPlayer();
        processing = true;
        pDialog.setMessage("Rejecting request...");
//        String jsonLatLng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        Data data = new Data();
        data.setType(RIDE_REJECTED);
        data.setTitle(getResources().getString(R.string.ride_rejected));
        data.setMessage(getResources().getString(R.string.driver_rejected_ride));
        data.setRide_id(rideId);
        data.setUser_id(userId);
        data.setToken(myToken);

        FCMMessage message = new FCMMessage(data, riderToken);
        JSONObject messageParams = null;
        try {
            messageParams = new JSONObject(new Gson().toJson(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONObject finalMessageParams = messageParams;
        mRegisterTask = new AsyncTask<Void, Void, String>() {
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
                    hideDialog();
                    try {
                        FCMResponse response = new Gson().fromJson(result, FCMResponse.class);
                        if (response.getSuccess() == 1) {
                        /*Intent intent = new Intent(RiderCallActivity.this,DriverMap2Activity.class);
                        intent.putExtra("lat",lat);
                        intent.putExtra("lng",lng);
                        intent.putExtra("rider_id",riderId);
                        intent.putExtra("rider_token",riderToken);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);*/
                        }
                    } catch (JsonSyntaxException exp) {

                    }
                    RiderCallActivity.this.finish();
                } else {
                    //showToast("Some error occurred!");
                }
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    private void disconnectDriver() {
        if (UpdateLocationService.isRunning())
            UpdateLocationService.getInstance().stopSelf();
        session.setValue(DRIVER_ONLINE, false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_AVAILABLE);
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
    }

    private void getDirection(double lat, double lng) {
        currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        String requestApi = null;
        String destination = lat + "," + lng;
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
                                JSONArray routes = jObj.getJSONArray("routes");

                                JSONObject object = routes.getJSONObject(0);
                                JSONArray legs = object.getJSONArray("legs");

                                JSONObject legsObject = legs.getJSONObject(0);

                                //Get Distance
                                JSONObject distance = legsObject.getJSONObject("distance");
                                tvDistance.setText(distance.getString("text"));

                                //Get Time
                                JSONObject duration = legsObject.getJSONObject("duration");
                                tvTime.setText(duration.getString("text"));

                                //Get Address
                                String address = legsObject.getString("end_address");
                                tvAddress.setText(address);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            //showToast(t.getMessage());
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
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

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {

        //wakeLock.release();
        super.onStop();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        //wakeLock.release();
        stopMediaPlayer();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        stopMediaPlayer();
        rejectRequest();
        //super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.img_map:
            case R.id.btnAccept:
                acceptRequest();
                break;
            case R.id.btnReject:
                rejectRequest();
                break;

        }
    }

}
