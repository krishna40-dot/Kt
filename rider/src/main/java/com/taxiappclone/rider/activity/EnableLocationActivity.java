package com.taxiappclone.rider.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taxiappclone.common.model.RideRequest;
import com.taxiappclone.rider.R;
import com.taxiappclone.common.helper.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.taxiappclone.common.app.AppConfig.LOCATION_PERMISSION_REQUEST_CODE;
import static com.taxiappclone.common.app.AppConfig.TABLE_PICKUP_REQUESTS;
import static com.taxiappclone.rider.app.CommonUtils.showToast;
import static com.taxiappclone.common.app.Constants.ON_RIDE;
import static com.taxiappclone.common.app.Constants.RIDE_COMPLETED;
import static com.taxiappclone.common.app.Constants.RIDE_FINISHED;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;

public class EnableLocationActivity extends AppCompatActivity {


    private static final String TAG = EnableLocationActivity.class.getSimpleName();
        ProgressDialog pDialog;
    @BindView(R.id.btnAllowLocation)
    Button btnAllowLocation;
    SessionManager session;
    private DatabaseReference refRideRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_location);
        ButterKnife.bind(this);

        session = new SessionManager(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        checkGpsStatus();
    }

    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showToast("GPS permission allows us to access location data. Please allow in App Settings for additional functionality.");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    showToast("Please Grant permission");
                }
                break;
        }
    }

    private FusedLocationProviderClient mFusedLocationClient;

    public void checkGpsStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {
            showToast("Please enable location in High Accuracy");
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
        } else {
            getCurrentLocation();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            checkGpsStatus();
        }
    }

    // main method used for get the location of user
    private void getCurrentLocation() {
        showDialog();
        Intent intent;
        // first check the location permission if not give then we will ask for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            pDialog.cancel();
            hideDialog();
            getLocationPermission();
            return;
        }
        if (session.isLoggedIn()) {
            if(session.getBooleanValue(ON_RIDE)) {
                refRideRequest = FirebaseDatabase.getInstance().getReference(TABLE_PICKUP_REQUESTS).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(session.getStringValue(RIDE_ID));
                refRideRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        hideDialog();
                        try{
                            Log.d(TAG, "Ride Request Detail:" + dataSnapshot.getValue().toString());
                            RideRequest ride = dataSnapshot.getValue(RideRequest.class);
                            if(ride.getRide_status().equals(RIDE_COMPLETED) || ride.getRide_status().equals(RIDE_FINISHED)) {
                                Intent intent = new Intent(getApplicationContext(),FareDetailActivity.class);
                                intent.putExtra(RIDE_REQUEST, new Gson().toJson(ride));
                                startActivity(intent);
                                finishAffinity();
                            }else{
                                Intent intent = new Intent(EnableLocationActivity.this, RiderTrackingActivity.class);
                                intent.putExtra(RIDE_REQUEST, new Gson().toJson(ride));
                                //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                finish();
                            }
                        } catch (NullPointerException e){
                            session.setValue(ON_RIDE, false);
                            Intent intent = new Intent(EnableLocationActivity.this, HomeActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage());
                    }
                });
            }else {
                hideDialog();
                new Handler().postDelayed(() -> {
                    Intent i = new Intent(this, HomeActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                    finishAffinity();
                }, 500);

            }
        }else
        {
            hideDialog();
            new Handler().postDelayed(() -> {
                Intent i = new Intent(this, SignInActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
            }, 500);
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

    @OnClick(R.id.btnAllowLocation)
    public void onViewClicked() {
        getLocationPermission();
    }
}
