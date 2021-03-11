package com.mbsindia.rider.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.taxiappclone.common.adapter.StoredLocationItem;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.RiderAddress;
import com.taxiappclone.common.model.RiderAddressResponse;
import com.taxiappclone.common.model.StoredLocationsListAdapter;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.taxiappclone.common.placesautocomplete.PlaceAutoCompleteItem;
import com.taxiappclone.common.placesautocomplete.PlaceAutoCompleteListAdapter;
import com.taxiappclone.common.placesautocomplete.PlacePredictions;
import com.taxiappclone.common.utils.RecyclerViewClickListener;
import com.taxiappclone.common.utils.ServerUtilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.mbsindia.rider.R;
import com.mbsindia.rider.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


import static com.taxiappclone.common.app.AppConfig.mLastLocation;
import static com.taxiappclone.common.app.CommonUtils.setStatusBarGradient;

public class SelectPlaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SelectPlaceActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ActionBar actionBar;

    private EditText txtAddress;
    double latitude, longitude;
    private String GETPLACESHIT = "places_hit";
    private GoogleApiClient mGoogleApiClient;

    private PlacePredictions predictions;
    private PlaceAutoCompleteListAdapter mAutoCompleteAdapter;
    private RecyclerView mAutoCompleteListView;
    private View overlay;

    //Saved location
    private RecyclerView listView;
    private StoredLocationsListAdapter listAdapter;
    private List<StoredLocationItem> listItems = new ArrayList<>();
    private AsyncTask<Void, Void, String> mRegisterTask;
    private SQLiteHandler db;
    private SessionManager session;
    private PlacesClient placesClient;

    private int user_id;
    APIService apiService;
    private List<RiderAddress> riderAddresses = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_place);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Search Address");
        setStatusBarGradient(this);

        String str = getResources().getString(com.mbsindia.rider.R.string.google_maps_key);

        txtAddress = findViewById(R.id.txt_address);
        mAutoCompleteListView = findViewById(R.id.list_view);
        listView = findViewById(R.id.list_view_2);
        overlay = findViewById(R.id.overlay);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(this);


        apiService = ApiUtils.INSTANCE.getApiService();
        user_id = AppController.getInstance().getCurrentUserId();

        listView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        txtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars

                if (txtAddress.getText().length() > 1) {
                    getPlacesJson();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mAutoCompleteListView.addOnItemTouchListener(new RecyclerViewClickListener.RecyclerTouchListener(this, mAutoCompleteListView, new RecyclerViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                overlay.setVisibility(View.VISIBLE);
                final PlaceAutoCompleteItem item = predictions.getPlaces().get(position);
                StringTokenizer st = new StringTokenizer(item.getPlaceDesc(), ",");
                final String address = item.getPlaceDesc();
                final String name = item.getTitle();
                try {
                    // Specify the fields to return.
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                    // Construct a request object, passing the place ID and fields array.
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(item.getPlaceID(), placeFields);

                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse response) {
                            Place place = response.getPlace();
                            Log.i(TAG, "Place found: " + place.getName());
                            LatLng queriedLocation = place.getLatLng();

                            double lat = queriedLocation.latitude;
                            double lng = queriedLocation.longitude;

                            Geocoder geocoder = new Geocoder(SelectPlaceActivity.this, Locale.getDefault());
                            try {
                                List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
                                if (addressList != null && addressList.size() > 0) {
                                    Address addr = addressList.get(0);
                                    String address_one = addr.getAddressLine(0);
                                    String address_two = addr.getAddressLine(1);
                                    Log.e(TAG, "onSuccess: " + addr.toString());
                                    Intent intent = new Intent();
                                    intent.putExtra("address_one", address_one != null ? address_one : "");
                                    intent.putExtra("address_two", address_two != null ? address_two : "");
                                    intent.putExtra("pincode", addr.getPostalCode() != null ? addr.getPostalCode() : "");
                                    intent.putExtra("latitude", lat);
                                    intent.putExtra("longitude", lng);
                                    intent.putExtra("address", address_one != null ? address_one : "");
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Unable connect to Geocoder", e);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                int statusCode = apiException.getStatusCode();
                                // Handle error with given status code.
                                Log.e(TAG, "Place not found: " + exception.getMessage());
                            }
                        }
                    });
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

//        getAllStoredLocations();

        buildGoogleApiClient();
    }


    private void getAllStoredLocations() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", String.valueOf(user_id))
                .build();

        apiService.getAddress(requestBody).enqueue(new Callback<RiderAddressResponse>() {
            @Override
            public void onResponse(Call<RiderAddressResponse> call, retrofit2.Response<RiderAddressResponse> response) {
                riderAddresses = response.body().getData();
                listAdapter = new StoredLocationsListAdapter(SelectPlaceActivity.this, riderAddresses) {
                    @Override
                    public void click(RiderAddress riderAddress) {
                        overlay.setVisibility(View.VISIBLE);
                        Log.e(TAG, "onSuccess: " + riderAddress.toString());
                        Intent intent = new Intent();
                        intent.putExtra("address_one", riderAddress.getAddress_one());
                        intent.putExtra("address_two", riderAddress.getAddress_two());
                        intent.putExtra("pincode", riderAddress.getPincode());
                        intent.putExtra("latitude", riderAddress.getLatitude());
                        intent.putExtra("longitude", riderAddress.getLongitude());
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void edit(RiderAddress riderAddress) {
                    }

                    @Override
                    public void delete(RiderAddress riderAddress) {
                    }
                };
                listView.setAdapter(listAdapter);
            }

            @Override
            public void onFailure(Call<RiderAddressResponse> call, Throwable t) {
                Toast.makeText(SelectPlaceActivity.this, "No Address Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPlacesJson() {
        AppController.getInstance().cancelPendingRequests(GETPLACESHIT);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                getPlaceAutoCompleteUrl(txtAddress.getText().toString()), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        Gson gson = new Gson();
                        predictions = gson.fromJson(response.toString(), PlacePredictions.class);

                        if (mAutoCompleteAdapter == null) {
                            mAutoCompleteAdapter = new PlaceAutoCompleteListAdapter(getApplicationContext(), predictions.getPlaces());
                            mAutoCompleteListView.setAdapter(mAutoCompleteAdapter);

                            mAutoCompleteListView.setHasFixedSize(true);
                            final LinearLayoutManager mLayoutManager;
                            mLayoutManager = new LinearLayoutManager(SelectPlaceActivity.this);
                            mAutoCompleteListView.setLayoutManager(mLayoutManager);
                            mAutoCompleteListView.setItemAnimator(new DefaultItemAnimator());
                            try {
                                mAutoCompleteListView.setAdapter(mAutoCompleteAdapter);
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }
                        } else {
                            mAutoCompleteAdapter.clear();
                            mAutoCompleteAdapter.updateList(predictions.getPlaces());
                            mAutoCompleteAdapter.notifyDataSetChanged();
                            mAutoCompleteListView.invalidate();
                        }
                        listView.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Access Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, GETPLACESHIT);
    }

    public String getPlaceAutoCompleteUrl(String input) {

        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude + "," + longitude); // append lat long of current location to show nearby results.
        urlString.append("&radius=500&language=en");
        urlString.append("&key=" + getResources().getString(R.string.google_api_browser_key));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

    protected synchronized void buildGoogleApiClient() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));

        // Create a new Places client instance
        placesClient = Places.createClient(this);
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                /*.enableAutoManage(this, 0, this)*/
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)/*
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)*/
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
