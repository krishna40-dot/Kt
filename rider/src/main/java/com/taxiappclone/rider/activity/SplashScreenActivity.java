package com.taxiappclone.rider.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.model.RideRequest;
import com.taxiappclone.common.utils.ServerUtilities;
import com.taxiappclone.rider.R;
import com.taxiappclone.common.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.taxiappclone.common.app.AppConfig.TABLE_PICKUP_REQUESTS;
import static com.taxiappclone.rider.app.CommonUtils.showToast;
import static com.taxiappclone.common.app.Constants.ON_RIDE;
import static com.taxiappclone.common.app.Constants.RIDE_COMPLETED;
import static com.taxiappclone.common.app.Constants.RIDE_FINISHED;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;
import static com.taxiappclone.rider.app.ModuleConfig.URL_LOGIN;

public class SplashScreenActivity extends Activity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    private SessionManager session;

    /** Called when the activity is first created. */
    Thread splashTread;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private FusedLocationProviderClient mFusedLocationClient;
    private DatabaseReference refRideRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        CommonUtils.hideNavigationBar(this);
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            long authTime = session.getLongValue("auth_time");
            long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            if((currentTime - authTime)>60*60*23)
                loginToServer();
            else
                checkGpsStatus();
        }else{
            checkGpsStatus();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loginToServer() {
        final Map<String, String> param = new HashMap<String, String>();
        param.put("mobile", session.getStringValue("mobile"));
        param.put("fb_id",FirebaseAuth.getInstance().getCurrentUser().getUid());

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();
            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_LOGIN, param);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d(TAG,"Login Response : "+result);
                if(result!=null){
                    try {
                        JSONObject jObj = new JSONObject(result);
                        if (jObj.optBoolean("status")) {
                            session.setValue("auth_token",jObj.getString("token"));
                            session.setValue("auth_time", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                            session.setLogin(true);

                            checkGpsStatus();
                        } else {
                            session.setLogin(false);
                            Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                }
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            checkGpsStatus();
        }
    }

    private void getCurrentLocation() {
        Intent intent;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(SplashScreenActivity.this, EnableLocationActivity.class));
            finish();
        }else{
            if (session.isLoggedIn()) {
                if(session.getBooleanValue(ON_RIDE)) {
                    refRideRequest = FirebaseDatabase.getInstance().getReference(TABLE_PICKUP_REQUESTS).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(session.getStringValue(RIDE_ID));
                    refRideRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try{
                                Log.d(TAG, "Ride Request Detail:" + dataSnapshot.getValue().toString());
                                RideRequest ride = dataSnapshot.getValue(RideRequest.class);
                                if(ride.getRide_status().equals(RIDE_COMPLETED) || ride.getRide_status().equals(RIDE_FINISHED)) {
                                    Intent intent = new Intent(getApplicationContext(),FareDetailActivity.class);
                                    intent.putExtra(RIDE_REQUEST, new Gson().toJson(ride));
                                    startActivity(intent);
                                    finishAffinity();
                                }else{
                                    Intent intent = new Intent(SplashScreenActivity.this, RiderTrackingActivity.class);
                                    intent.putExtra(RIDE_REQUEST, new Gson().toJson(ride));
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (NullPointerException e){
                                session.setValue(ON_RIDE, false);
                                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
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
                    new Handler().postDelayed(() -> {
                        Intent i = new Intent(this, HomeActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                        finishAffinity();
                    }, 500);

                }
            }else
            {
                new Handler().postDelayed(() -> {
                    Intent i = new Intent(this, SignInActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                    finish();
                }, 500);
            }
        }
    }
}