package com.taxiappclone.driver.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.driver.notification.NotificationUtils;

import static com.taxiappclone.common.app.AppConfig.DISPLACEMENT;
import static com.taxiappclone.common.app.AppConfig.FASTEST_INTERVAL;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_AVAILABLE;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS_ON_DUTY;
import static com.taxiappclone.common.app.AppConfig.UPDATE_INTERVAL;
import static com.taxiappclone.common.app.Constants.DRIVER_ONLINE;
import static com.taxiappclone.common.app.Constants.ON_DUTY;

/**
 * Created by Mac on 4/16/2018.
 */

public class UpdateLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    private SessionManager session;
    private boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;
    private DatabaseReference driverLocationRef;
    private GeoFire geoFire;
    private Location mLastLocation;

    private static UpdateLocationService instance = null;
    private static boolean restart = false;

    public static boolean isRunning() {
        return instance != null;
    }

    public static UpdateLocationService getInstance() {
        return instance;
    }
    public static void stopService()
    {
        instance.stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        disconnectDriver();
        //super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        session = new SessionManager(this);
        if(session.getBooleanValue(ON_DUTY))
            driverLocationRef = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_ON_DUTY);
        else
            driverLocationRef = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_AVAILABLE);
        geoFire = new GeoFire(driverLocationRef);
        buildGoogleApiClient();
    }

    public void setDatabase(){
        if(session.getBooleanValue(ON_DUTY))
            driverLocationRef = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_ON_DUTY);
        else
            driverLocationRef = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_AVAILABLE);
        geoFire = new GeoFire(driverLocationRef);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void connectDriver() {
        mRequestingLocationUpdates = true;
        startLocationUpdates();
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
            updateLocation(null);
    }

    private void disconnectDriver() {
        mRequestingLocationUpdates = false;
        stopLocationUpdates();
        session.setValue(DRIVER_ONLINE,false);
        String userId = FirebaseAuth.getInstance().getUid();
        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS_LOCATION);
        GeoFire geoFire = new GeoFire(ref);*/
        geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                UpdateLocationService.this.stopSelf();
            }
        });
    }

    private void updateLocation(Location location) {
        if(location==null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        if (mLastLocation != null) {
                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();

                //Save driver location firebase
                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
//                        showToast("updating location from service");
                    }
                });
        }
        else {
            showToast("Please unable location on your phone.");
            Log.d("ERROR","Can not get your location");
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        showToast("Service started");
        if(session.getBooleanValue(DRIVER_ONLINE))
        {
            connectDriver();
        }else
        {
            disconnectDriver();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            updateLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
    }

    public static void restart(Context context){
        restart = true;
        context.stopService(new Intent(context, UpdateLocationService.class));
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        instance = null;
        if (restart)
        {
            restart = false;
            startService(new Intent(this, UpdateLocationService.class));
        }
        super.onDestroy();
    }
}