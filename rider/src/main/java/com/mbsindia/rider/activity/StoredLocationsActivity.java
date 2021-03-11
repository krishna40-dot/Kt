/**
 * Author: Adee
 * facebook: http://facebook.com/ideal.adee
 */
package com.mbsindia.rider.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.taxiappclone.common.adapter.StoredLocationItem;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.model.RiderAddress;
import com.taxiappclone.common.model.RiderAddressResponse;
import com.taxiappclone.common.model.StoredLocationsListAdapter;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.mbsindia.rider.R;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.utils.ServerUtilities;
import com.mbsindia.rider.app.AppController;

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

public class StoredLocationsActivity extends AppCompatActivity {
    public static final String TAG = StoredLocationsActivity.class.getSimpleName();
    private Toolbar toolbar;
    private SessionManager session;
    private ActionBar actionBar;

    private RecyclerView listView;
    private StoredLocationsListAdapter listAdapter;
    private List<StoredLocationItem> listItems = new ArrayList<>();
    private AsyncTask<Void, Void, String> mRegisterTask;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private View noLocationView;
    private Button btnAddLocation;

    private int user_id;
    APIService apiService;
    private List<RiderAddress> riderAddresses = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stored_locations);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.title_activity_stored_locations));

        apiService = ApiUtils.INSTANCE.getApiService();

        user_id = AppController.getInstance().getCurrentUserId();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(this);

        noLocationView = findViewById(R.id.view_no_location);
        btnAddLocation = findViewById(R.id.btn_add_location);
        noLocationView.setVisibility(View.GONE);
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StoredLocationsActivity.this, AddLocationActivity.class);
                startActivity(i);
            }
        });
        listView = (RecyclerView) findViewById(R.id.list_view);
        listView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        getAllStoredLocations();
    }

    private void getAllStoredLocations() {
        riderAddresses.clear();
        pDialog.setMessage("Loading...");
        showDialog();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", String.valueOf(user_id))
                .build();

        apiService.getAddress(requestBody).enqueue(new Callback<RiderAddressResponse>() {
            @Override
            public void onResponse(Call<RiderAddressResponse> call, Response<RiderAddressResponse> response) {
                hideDialog();
                riderAddresses = response.body().getData();
                listAdapter = new StoredLocationsListAdapter(StoredLocationsActivity.this, riderAddresses) {
                    @Override
                    public void click(RiderAddress riderAddress) {

                    }

                    @Override
                    public void edit(RiderAddress riderAddress) {
                        Intent i = new Intent(StoredLocationsActivity.this, AddLocationActivity.class);
                        i.putExtra("isFromEdit", true);
                        i.putExtra("addressId", riderAddress.getId());
                        i.putExtra("user_id", riderAddress.getUser_id());
                        i.putExtra("address_type", riderAddress.getAddress_type());
                        i.putExtra("name", riderAddress.getName());
                        i.putExtra("address_one", riderAddress.getAddress_one());
                        i.putExtra("address_two", riderAddress.getAddress_two());
                        i.putExtra("pincode", riderAddress.getPincode());
                        i.putExtra("latitude", riderAddress.getLatitude());
                        i.putExtra("longitude", riderAddress.getLongitude());
                        startActivity(i);
                    }

                    @Override
                    public void delete(RiderAddress riderAddress) {
                        pDialog.setMessage("Deleting...");
                        showDialog();
                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id", riderAddress.getId())
                                .build();
                        apiService.deleteAddress(requestBody).enqueue(new Callback<JSONObject>() {
                            @Override
                            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                getAllStoredLocations();
                            }

                            @Override
                            public void onFailure(Call<JSONObject> call, Throwable t) {
                                Toast.makeText(StoredLocationsActivity.this, "Unable to delete address", Toast.LENGTH_SHORT).show();
                                hideDialog();
                            }
                        });
                    }
                };
                listView.setAdapter(listAdapter);
            }

            @Override
            public void onFailure(Call<RiderAddressResponse> call, Throwable t) {
                Toast.makeText(StoredLocationsActivity.this, "No Address Found", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        noLocationView.setVisibility(View.GONE);
        getAllStoredLocations();
        super.onResume();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stored_locations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_location:
                Intent i = new Intent(StoredLocationsActivity.this, AddLocationActivity.class);
                startActivity(i);
                break;
            case android.R.id.home:
                this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
