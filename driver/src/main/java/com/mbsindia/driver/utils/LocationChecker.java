package com.mbsindia.driver.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.RideRequest;
import com.mbsindia.driver.activity.AccountStatusActivity;
import com.mbsindia.driver.activity.CollectFareActivity;
import com.mbsindia.driver.activity.DriverMap2Activity;
import com.mbsindia.driver.activity.DriverTrackingActivity;
import com.mbsindia.driver.activity.EnableLocationActivity;
import com.mbsindia.driver.activity.SignInActivity;
import com.mbsindia.driver.activity.SplashScreenActivity;
import com.mbsindia.driver.app.AppController;
import com.mbsindia.driver.service.UpdateLocationService;

import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_ON_DUTY;
import static com.taxiappclone.common.app.AppConfig.TABLE_PICKUP_REQUESTS;
import static com.taxiappclone.common.app.Constants.ON_DUTY;
import static com.taxiappclone.common.app.Constants.RIDER_ID;
import static com.taxiappclone.common.app.Constants.RIDE_COMPLETED;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;
import static com.mbsindia.driver.app.AppController.TAG;
import static com.mbsindia.driver.app.CommonUtils.showToast;

public class LocationChecker {
    private static FusedLocationProviderClient mFusedLocationClient;
    private static String userId;

    public static void checkLocationPermissions(Activity activity) {
        SessionManager session = AppController.getInstance().getSessionManager();
        Intent intent;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.startActivity(new Intent(activity, EnableLocationActivity.class));
            activity.finishAffinity();
        }else{
            if (session.isLoggedIn()) {
                if(AppController.getInstance().getCurrentUser().getStatus()!=1){
                    new Handler().postDelayed(() -> {
                        openActivity(activity,AccountStatusActivity.class);
                    }, 500);
                    return;
                }
                if(session.getBooleanValue(ON_DUTY)) {
                    DatabaseReference refRideRequest = FirebaseDatabase.getInstance().getReference(TABLE_PICKUP_REQUESTS).child(session.getStringValue(RIDER_ID)).child(session.getStringValue(RIDE_ID));
                    refRideRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                Log.d(TAG, "Ride Request Detail:" + dataSnapshot.getValue().toString());
                                RideRequest ride = dataSnapshot.getValue(RideRequest.class);
                                if(ride.getRide_status().equals(RIDE_COMPLETED)){
                                    if(UpdateLocationService.isRunning())
                                        activity.stopService(new Intent(activity, UpdateLocationService.class));
                                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_ON_DUTY);
                                    GeoFire geoFire = new GeoFire(ref);
                                    geoFire.removeLocation(userId, (key, error) -> {
                                        Intent intent = new Intent(activity, CollectFareActivity.class);
                                        intent.putExtra(RIDE_REQUEST, new Gson().toJson(ride));
                                        activity.startActivity(intent);
                                        activity.finishAffinity();
                                    });
                                }else {
                                    Intent intent = new Intent(activity, DriverTrackingActivity.class);
                                    intent.putExtra(RIDE_REQUEST, new Gson().toJson(ride));

                                    activity.startActivity(intent);
                                    activity.finishAffinity();
                                }
                            }catch (NullPointerException e){
                                openActivity(activity,DriverMap2Activity.class);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    new Handler().postDelayed(() -> {
                        openActivity(activity,DriverMap2Activity.class);
                    }, 500);
                }
            }else
            {
                new Handler().postDelayed(() -> {
                    Intent intent1 = new Intent(activity, SignInActivity.class);
                    
                    activity.startActivity(intent1);
                    activity.finishAffinity();
                }, 500);
            }
        }
    }

    private static void openActivity(Activity activity,Class cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        activity.finishAffinity();
        return;
    }

    private static void callHomeScreen(Activity activity){
        Intent intent = new Intent(activity, DriverMap2Activity.class);
        
        activity.startActivity(intent);
        activity.finishAffinity();
    }
}
