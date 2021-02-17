package com.taxiappclone.driver.activity;

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
;
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
import com.taxiappclone.common.activity.CustomActivity;
import com.taxiappclone.driver.R;
import com.taxiappclone.driver.adapter.StoredLocationsListAdapter;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.driver.app.AppController;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.driver.model.StoredLocationItem;
import com.taxiappclone.driver.placesautocomplete.PlaceAutoCompleteItem;
import com.taxiappclone.driver.placesautocomplete.PlaceAutoCompleteListAdapter;
import com.taxiappclone.driver.placesautocomplete.PlacePredictions;
import com.taxiappclone.common.utils.RecyclerViewClickListener;
import com.taxiappclone.common.utils.ServerUtilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

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



import static com.taxiappclone.common.app.AppConfig.mLastLocation;
import static com.taxiappclone.driver.app.AppController.TAG;
import static com.taxiappclone.common.utils.ServerUtilities.GET;

public class SelectPlaceActivity extends CustomActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

        txtAddress = findViewById(R.id.txt_address);
        mAutoCompleteListView = findViewById(R.id.list_view);
        listView = findViewById(R.id.list_view_2);
        overlay = findViewById(R.id.overlay);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(this);

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

        txtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars

                if (txtAddress.getText().length() > 0) {
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

        listView.addOnItemTouchListener(new RecyclerViewClickListener.RecyclerTouchListener(this, listView, new RecyclerViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                overlay.setVisibility(View.VISIBLE);
                final StoredLocationItem item = listItems.get(position);

                Intent intent = new Intent();
                intent.putExtra("address", item.address);
                intent.putExtra("city", item.city);
                intent.putExtra("country_code", item.countryCode);
                intent.putExtra("postal_code", item.postalCode);
                intent.putExtra("latitude", item.latitude);
                intent.putExtra("longitude", item.longitude);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //getAllStoredLocations();

        buildGoogleApiClient();
    }

    private void getAllStoredLocations() {
        listItems.clear();
        Cursor c = db.getStoredLocations(1);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                StoredLocationItem item = new StoredLocationItem(c.getInt(c.getColumnIndex("server_id")), c.getString(c.getColumnIndex("address_type")), c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("address")), c.getString(c.getColumnIndex("city")), c.getString(c.getColumnIndex("country_code")), c.getString(c.getColumnIndex("postal_code")), c.getDouble(c.getColumnIndex("latitude")), c.getDouble(c.getColumnIndex("longitude")));
                listItems.add(item);
            }
            listAdapter.notifyDataSetChanged();
        } else {
            try {
                getCollectionFromServer();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getCollectionFromServer() throws JSONException {
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "bearer " + session.getStringValue("user_access_token"));

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected void onPreExecute() {
                //showDialog();
            }

            @Override
            protected String doInBackground(Void... voids) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getJsonResponse(GET, AppConfig.URL_USER_ADDRESS, null, headers);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d(TAG, "Address Collection Response: " + result.toString());
                //showToast(result.toString());
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        if (jObj.getInt("total_count") > 0) {
                            JSONArray items = jObj.getJSONArray("items");
                            for (int i = 0; i < jObj.getInt("total_count"); i++) {
                                JSONObject itemObj = items.getJSONObject(i);
                                int id = itemObj.getInt("id");
                                String type = itemObj.getString("type");
                                JSONObject addressObj = itemObj.getJSONObject("address");
                                ContentValues values = new ContentValues();
                                values.put("server_id", id);
                                values.put("address_type", type);
                                values.put("name", addressObj.getString("name"));
                                values.put("address", addressObj.getString("address_line1"));
                                values.put("city", addressObj.getString("city"));
                                //values.put("country",addressObj.getString("county"));
                                values.put("country_code", addressObj.getString("country_code"));
                                values.put("postal_code", addressObj.getString("postal_code"));
                                values.put("latitude", addressObj.getDouble("latitude"));
                                values.put("longitude", addressObj.getDouble("longitude"));
                                if (!db.checkLocationExists(id))
                                    db.addLocation(values);
                                else
                                    db.updateLocation(id, values);
                                StoredLocationItem item = new StoredLocationItem(id, type, addressObj.getString("name"), addressObj.getString("address_line1"), addressObj.getString("city"), addressObj.getString("country_code"), addressObj.getString("postal_code"), addressObj.getDouble("latitude"), addressObj.getDouble("longitude"));
                                listItems.add(item);
                            }
                            listAdapter.notifyDataSetChanged();
                        } else {
                            //noLocationView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //hideDialog();
                } else {
                    //showToast("Some error occurred!");
                }
                //hideDialog();
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
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
