package com.mbsindia.rider.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.model.MyRides;
import com.taxiappclone.common.model.MyRidesResponse;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.mbsindia.rider.R;
import com.mbsindia.rider.adapter.MyRidesListAdapter;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;
import com.mbsindia.rider.app.AppController;
import com.mbsindia.rider.model.MyRidesListItem;
import com.taxiappclone.common.utils.ServerUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.taxiappclone.common.utils.ServerUtilities.GET;

public class MyRidesActivity extends AppCompatActivity {
    public static final String TAG = MyRidesActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ActionBar actionBar;
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private RecyclerView listView;
    private MyRidesListAdapter listAdapter;
    private List<MyRidesListItem> listItems = new ArrayList<>();
    private AsyncTask<Void, Void, String> mRegisterTask;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private APIService apiService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("My Rides");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(this);
        db = new SQLiteHandler(this);

        apiService = ApiUtils.INSTANCE.getApiService();

        listView = findViewById(R.id.list_view);
        listAdapter = new MyRidesListAdapter(this, listItems);
        listView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        try {
            listView.setAdapter(listAdapter);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        getCollectionFromServer();
    }

    @SuppressLint("StaticFieldLeak")
    private void getCollectionFromServer() {
        pDialog.setMessage("Loading...");
        showDialog();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", String.valueOf(AppController.getInstance().getCurrentUserId()))
                .build();

        apiService.getRidesRider(requestBody, session.getStringValue("auth_token")).enqueue(new Callback<MyRidesResponse>() {
            @Override
            public void onResponse(Call<MyRidesResponse> call, Response<MyRidesResponse> response) {
                if (response.body().getStatus()) {
                    for (MyRides mr : response.body().getData()) {
                        Log.e(TAG, "onResponse: " + mr);
                        JsonParser parser = new JsonParser();
                        JsonObject pickUpObject = parser.parse(mr.getPickup_location()).getAsJsonObject();
                        JsonObject dropOffObject = parser.parse(mr.getDropoff_location()).getAsJsonObject();

                        Location pickupLocation = new Location("");
                        pickupLocation.setLatitude(pickUpObject.get("latitude").getAsDouble());
                        pickupLocation.setLongitude(pickUpObject.get("longitude").getAsDouble());
                        Location dropLocation = new Location("");
                        dropLocation.setLatitude(dropOffObject.get("latitude").getAsDouble());
                        dropLocation.setLongitude(dropOffObject.get("longitude").getAsDouble());

                        float distance = pickupLocation.distanceTo(dropLocation) / 1000;
                        String distanceInKm = String.format("%.2f", distance) + " KM";
                        Log.e(TAG, "onResponse: " + distanceInKm);
                        MyRidesListItem item = new MyRidesListItem(Integer.parseInt(mr.getRide_id()), mr.getPickup_address(), mr.getDropoff_address(), Double.parseDouble(String.format("%.2f", Double.parseDouble(mr.getFare()))), mr.getRide_status(), mr.getStart_time(), distanceInKm);
                        listItems.add(item);
                    }
                    listAdapter.notifyDataSetChanged();
                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<MyRidesResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                hideDialog();
            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
