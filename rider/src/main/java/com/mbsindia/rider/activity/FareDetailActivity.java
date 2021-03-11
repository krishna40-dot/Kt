package com.mbsindia.rider.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.taxiappclone.common.model.RideRequest;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.mbsindia.rider.R;
import com.mbsindia.rider.app.AppController;
import com.taxiappclone.common.helper.SessionManager;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.taxiappclone.common.app.CommonUtils.setStatusBarGradient;
import static com.taxiappclone.common.app.Constants.ON_RIDE;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;

public class FareDetailActivity extends AppCompatActivity {

    private static final String TAG = FareDetailActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnFinish)
    Button btnFinish;
    @BindView(R.id.ratingBar)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.etReview)
    MaterialEditText etReview;
    private ActionBar actionBar;
    private SessionManager session;
    private RideRequest ride;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_detail);
        ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.fare_detail);

        setStatusBarGradient(this);

        session = AppController.getInstance().getSessionManager();
        Log.d(TAG, "Ride detail: " + getIntent().getStringExtra(RIDE_REQUEST));
        ride = new Gson().fromJson(getIntent().getStringExtra(RIDE_REQUEST), RideRequest.class);

        apiService = ApiUtils.INSTANCE.getApiService();

        btnFinish.setOnClickListener(v -> {
            String rideId = ride.getRide_id();
            String rating = String.valueOf(ratingBar.getRating());
            String review = etReview.getText().toString();

            if (!rating.equals("0")) {
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("ride_id", rideId)
                        .addFormDataPart("rating", rating)
                        .addFormDataPart("review", review)
                        .build();
                apiService.rateDriver(requestBody).enqueue(new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        Toast.makeText(FareDetailActivity.this, "Rating submitted", Toast.LENGTH_SHORT).show();
                        session.setValue(ON_RIDE, false);
                        startActivity(new Intent(FareDetailActivity.this, HomeActivity.class));
                        finishAffinity();
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        Toast.makeText(FareDetailActivity.this, "Failed to Add Rating", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                session.setValue(ON_RIDE, false);
                startActivity(new Intent(FareDetailActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });
    }
}
