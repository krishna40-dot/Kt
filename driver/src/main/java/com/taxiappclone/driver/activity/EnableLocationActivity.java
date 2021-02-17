package com.taxiappclone.driver.activity;

import android.Manifest;
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
;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taxiappclone.common.activity.CustomActivity;
import com.taxiappclone.common.model.RideRequest;
import com.taxiappclone.driver.R;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.driver.utils.LocationChecker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.taxiappclone.common.app.AppConfig.LOCATION_PERMISSION_REQUEST_CODE;
import static com.taxiappclone.common.app.AppConfig.TABLE_PICKUP_REQUESTS;
import static com.taxiappclone.common.app.Constants.ON_RIDE;
import static com.taxiappclone.common.app.Constants.RIDE_COMPLETED;
import static com.taxiappclone.common.app.Constants.RIDE_FINISHED;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;
import static com.taxiappclone.driver.app.CommonUtils.showToast;


public class EnableLocationActivity extends CustomActivity {
//    ProgressDialog pDialog;
    @BindView(R.id.btnAllowLocation)
    Button btnAllowLocation;
    SessionManager session;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_location);
        ButterKnife.bind(this);

        /*pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Please wait...");*/

        session = new SessionManager(this);
        checkGpsStatus();
    }

    public void checkGpsStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus)
        {
            showToast("Please enable location in High Accuracy");
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),2);
        }else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        Intent intent;
        // first check the location permission if not give then we will ask for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            pDialog.cancel();
            getLocationPermission();
            return;
        }
        LocationChecker.checkLocationPermissions(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            checkGpsStatus();
        }
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
                    LocationChecker.checkLocationPermissions(this);
                } else {
                    showToast("Please Grant permission");
                }
                break;
        }
    }

    @OnClick(R.id.btnAllowLocation)
    public void onViewClicked() {
        getLocationPermission();
    }
}
