package com.taxiappclone.driver.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.taxiappclone.common.activity.CustomActivity;

import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.taxiappclone.driver.R;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.driver.app.AppController;
import com.taxiappclone.driver.model.StoredLocationItem;
import com.taxiappclone.common.utils.ServerUtilities;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.taxiappclone.common.app.AppConfig.ADD_LOCATION;
import static com.taxiappclone.common.app.AppConfig.SELECT_ADDRESS;
import static com.taxiappclone.common.utils.ServerUtilities.POST;

public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialEditText txtName, txtAddress;
    private MaterialSpinner spinnerAddressType;
    private String addressId;
    private int user_id;
    private String address_type;
    private String name;
    private String address_one;
    private String address_two;
    private String pincode;
    private double latitude;
    private double longitude;
    int type = 0;
    private ProgressDialog pDialog;
    private SessionManager session;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private TextView tvDialogTitle;
    private String TAG = AddLocationActivity.class.getSimpleName();

    boolean isFromEdit = false;
    APIService apiService;
    private Button btnAddLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        apiService = ApiUtils.INSTANCE.getApiService();
        if (getIntent().hasExtra("isFromEdit")) {
            isFromEdit = getIntent().getBooleanExtra("isFromEdit", false);
            addressId = getIntent().getStringExtra("addressId");
            user_id = getIntent().getIntExtra("user_id", 0);
            address_type = getIntent().getStringExtra("address_type");
            name = getIntent().getStringExtra("name");
            address_one = getIntent().getStringExtra("address_one");
            address_two = getIntent().getStringExtra("address_two");
            pincode = getIntent().getStringExtra("pincode");
            latitude = getIntent().getDoubleExtra("latitude", 0.1);
            longitude = getIntent().getDoubleExtra("longitude", 0.1);
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // Session manager
        session = new SessionManager(getApplicationContext());

        user_id = AppController.getInstance().getCurrentUserId();

        tvDialogTitle = findViewById(R.id.tv_dialog_title);
        Button btnAddLocation = findViewById(R.id.btn_add_location);
        txtName = findViewById(R.id.txt_name);
        txtAddress = findViewById(R.id.txt_address);
        tvDialogTitle = findViewById(R.id.tv_dialog_title);
        spinnerAddressType = findViewById(R.id.spinner_address_type);


        txtAddress.setInputType(InputType.TYPE_NULL);
        txtAddress.setOnClickListener(this);
        txtAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Intent i = new Intent(AddLocationActivity.this, SelectPlaceActivity.class);
                    startActivityForResult(i, SELECT_ADDRESS);
                }
            }
        });
        btnAddLocation.setOnClickListener(this);
        String[] ITEMS = {"Home", "Office", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddressType.setAdapter(adapter);

        if (isFromEdit) {
            tvDialogTitle.setText("Edit address");
            btnAddLocation.setText("Update");
            int position = 0;
            if (address_type.equalsIgnoreCase("Home")) {
                position = 0;
            } else if (address_type.equalsIgnoreCase("Office")) {
                position = 1;
            } else if (address_type.equalsIgnoreCase("Other")) {
                position = 2;
            }
            spinnerAddressType.setSelection(position);
            txtName.setText(name);
            txtAddress.setText(address_one);
        } else {
            tvDialogTitle.setText("Add a address");
            btnAddLocation.setText("Add");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_location:
                if (attemptSave()) {
                    if (isFromEdit) {
                        updateLocation();
                    } else {
                        addLocation();
                    }
                }
                break;
            case R.id.txt_address:
                Intent i = new Intent(AddLocationActivity.this, SelectPlaceActivity.class);
                startActivityForResult(i, SELECT_ADDRESS);
                break;

        }
    }


    private Boolean attemptSave() {
        txtName.setError(null);
        txtAddress.setError(null);

        name = txtName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(txtName.getText().toString())) {
            txtName.setError("Please Enter Name");
            focusView = txtName;
            cancel = true;
        }
        if (TextUtils.isEmpty(txtAddress.getText().toString())) {
            txtAddress.setError("Please Enter Address");
            focusView = txtAddress;
            cancel = true;
        }
        if (spinnerAddressType.getSelectedItem() == null) {
            spinnerAddressType.setError("Please Select Type");
            focusView = spinnerAddressType;
            cancel = true;
        } else {
            address_type = spinnerAddressType.getSelectedItem().toString().toLowerCase();
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void addLocation() {
        pDialog.setMessage("Saving...");
        showDialog();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", String.valueOf(user_id))
                .addFormDataPart("address_type", address_type)
                .addFormDataPart("name", name)
                .addFormDataPart("address_one", address_one)
                .addFormDataPart("address_two", address_two)
                .addFormDataPart("pincode", pincode)
                .addFormDataPart("latitude", String.valueOf(latitude))
                .addFormDataPart("longitude", String.valueOf(longitude))
                .addFormDataPart("country_id", String.valueOf(1))
                .addFormDataPart("state_id", String.valueOf(1))
                .addFormDataPart("city_id", String.valueOf(1))
                .build();

        apiService.addDriverAddress(requestBody).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.toString());
                    Toast.makeText(AddLocationActivity.this, "Address added Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("referesh", true);
                    setResult(ADD_LOCATION, intent);
                    finish();
                } else {
                    Toast.makeText(AddLocationActivity.this, "Failed to add Address", Toast.LENGTH_SHORT).show();
                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(AddLocationActivity.this, "Failed to add Address", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void updateLocation() {
        pDialog.setMessage("Saving...");
        showDialog();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", addressId)
                .addFormDataPart("user_id", String.valueOf(user_id))
                .addFormDataPart("address_type", address_type)
                .addFormDataPart("name", name)
                .addFormDataPart("address_one", address_one)
                .addFormDataPart("address_two", address_two)
                .addFormDataPart("pincode", pincode)
                .addFormDataPart("latitude", String.valueOf(latitude))
                .addFormDataPart("longitude", String.valueOf(longitude))
                .addFormDataPart("country_id", String.valueOf(1))
                .addFormDataPart("state_id", String.valueOf(1))
                .addFormDataPart("city_id", String.valueOf(1))
                .build();

        apiService.updateDriverAddress(requestBody).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.toString());
                    Toast.makeText(AddLocationActivity.this, "Address updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("referesh", true);
                    setResult(ADD_LOCATION, intent);
                    finish();
                } else {
                    Toast.makeText(AddLocationActivity.this, "Failed to update Address", Toast.LENGTH_SHORT).show();
                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(AddLocationActivity.this, "Failed to update Address", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK && requestCode == SELECT_ADDRESS) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                address_one = extras.getString("address_one");
                address_two = extras.getString("address_two");
                pincode = extras.getString("pincode");
                latitude = extras.getDouble("latitude");
                longitude = extras.getDouble("longitude");
                txtAddress.setText(address_one);
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
        //saveData(f_phone_no);
        super.onBackPressed();
        finish();
        //overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
        //overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
