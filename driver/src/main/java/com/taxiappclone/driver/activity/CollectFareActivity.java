package com.taxiappclone.driver.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.google.android.gms.maps.model.LatLng;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.taxiappclone.common.activity.CustomActivity;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.taxiappclone.driver.R;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.driver.app.AppController;
import com.taxiappclone.common.model.RideRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.taxiappclone.common.app.AppConfig.mLastLocation;
import static com.taxiappclone.common.app.CommonUtils.setStatusBarGradient;
import static com.taxiappclone.common.app.Constants.DRIVER_ONLINE;
import static com.taxiappclone.common.app.Constants.ON_DUTY;
import static com.taxiappclone.common.app.Constants.ON_RIDE;
import static com.taxiappclone.common.app.Constants.RIDE_FINISHED;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;
import static com.taxiappclone.common.app.Constants.RIDE_STATUS;

public class CollectFareActivity extends CustomActivity {

    private static final String TAG = CollectFareActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnFinish)
    Button btnFinish;
    @BindView(R.id.ratingBar)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.tvFare)
    TextView tvFare;
    @BindView(R.id.etReview)
    MaterialEditText etReview;
    private ActionBar actionBar;
    private SessionManager session;
    private RideRequest ride;
    private ProgressDialog pDialog;

    private APIService apiService;
    private String paymentMode = "cod";
    private String totalFare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_fare);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.collect_fare);
        setStatusBarGradient(this);

        if (getIntent().hasExtra("paymentMode")) {
            paymentMode = getIntent().getStringExtra("paymentMode");
        }
        if (getIntent().hasExtra("totalFare")) {
            totalFare = getIntent().getStringExtra("totalFare");
            tvFare.setText("Rs. " + totalFare + "\n(" + String.format("%.2f", (Double.parseDouble(totalFare) / 123) * 100) + "FARE + " + String.format("%.2f", (Double.parseDouble(totalFare) / 123) * 23) + " GST)");
        }
        session = AppController.getInstance().getSessionManager();
        Log.d(TAG, "Ride detail: " + getIntent().getStringExtra(RIDE_REQUEST));
        ride = new Gson().fromJson(getIntent().getStringExtra(RIDE_REQUEST), RideRequest.class);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");

        apiService = ApiUtils.INSTANCE.getApiService();

        btnFinish.setOnClickListener(v -> {
            String rideId = ride.getRide_id();
            String rating = String.valueOf(ratingBar.getRating());
            String review = etReview.getText().toString();

            /*if (!rating.equals("0")) {
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("ride_id", rideId)
                        .addFormDataPart("rating", rating)
                        .addFormDataPart("review", review)
                        .build();
                apiService.rateRider(requestBody).enqueue(new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        Toast.makeText(CollectFareActivity.this, "Rating submitted", Toast.LENGTH_SHORT).show();
                        showDialog();
                        FirebaseDatabase.getInstance().getReference(AppConfig.TABLE_PICKUP_REQUESTS)
                                .child(ride.getRider_id()).child(session.getStringValue(RIDE_ID)).child(RIDE_STATUS)
                                .setValue(RIDE_FINISHED).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finishRide(rating, review);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        Toast.makeText(CollectFareActivity.this, "Failed to Add Rating", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {*/
            showDialog();
            FirebaseDatabase.getInstance().getReference(AppConfig.TABLE_PICKUP_REQUESTS)
                    .child(ride.getRider_id()).child(session.getStringValue(RIDE_ID)).child(RIDE_STATUS)
                    .setValue(RIDE_FINISHED).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finishRide(rating, review);
                }
            });
//            }
        });
    }

    private void finishRide(String rating, String review) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("ride_id", session.getStringValue(RIDE_ID))
                .addFormDataPart("driver_rating", rating)
                .addFormDataPart("driver_review", review)
                .addFormDataPart("ride_status", "finished")
                .addFormDataPart("payment_type", paymentMode)
                .addFormDataPart("end_time", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()))
                .build();

        apiService.updateRideDetail(requestBody).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.e(TAG, "onResponse: Ride finished");
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e(TAG, "onFailure: failed to finish ride");
            }
        });
        hideDialog();
        session.setValue(ON_DUTY, false);
        session.setValue(DRIVER_ONLINE, true);
        Intent intent = new Intent(CollectFareActivity.this, DriverMap2Activity.class);
        startActivity(intent);
        finish();
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
    public void onBackPressed() {
//        super.onBackPressed();
    }
}