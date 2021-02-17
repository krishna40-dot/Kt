package com.taxiappclone.river.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.taxiappclone.common.adapter.StoredLocationItem;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.rider.R;
import com.taxiappclone.rider.activity.SelectPlaceActivity;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.taxiappclone.common.app.AppConfig.SELECT_ADDRESS;

public class AddLocationDialog
        extends Dialog implements View.OnClickListener{
    private StoredLocationItem location;
    public Activity activity;
    public Dialog d;
    public int theme;
    public Button btnAddLocation;
    public MaterialEditText txtName, txtAddress, txtAptSuite;
    public MaterialSpinner spinnerAddressType;
    double latitude, longitude;

    private RecyclerView mAutoCompleteListView;
    private PlaceAutocompleteFragment autoPlaceFragment;
    private Place selectedPlace;
    int type = 0;
    private TextView tvDialogTitle;

    public AddLocationDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }
    public AddLocationDialog(Activity activity, StoredLocationItem location) {
        super(activity);
        this.activity = activity;
        this.location  = location;
        this.type = 1;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        boolean bool = requestWindowFeature(1);
        setContentView(R.layout.dialog_add_location);
        btnAddLocation = findViewById(R.id.btn_add_location);
        txtName =  findViewById(R.id.txt_name);
        tvDialogTitle = findViewById(R.id.tv_dialog_title);
        txtAddress = findViewById(R.id.txt_address);
        txtAptSuite =  findViewById(R.id.txt_apt_suite);
        spinnerAddressType =  findViewById(R.id.spinner_address_type);
        mAutoCompleteListView = findViewById(R.id.list_view);

        txtAddress.setInputType(InputType.TYPE_NULL);
        txtAddress.setOnClickListener(this);
        txtAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus)
                {
                    Intent i = new Intent(activity, SelectPlaceActivity.class);
                    activity.startActivityForResult(i, SELECT_ADDRESS);
                }
            }
        });
        btnAddLocation.setOnClickListener(this);
        String[] ITEMS = {"Home", "Office", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddressType.setAdapter(adapter);

        if(type==1)
        {
            tvDialogTitle.setText("Edit Location");
            btnAddLocation.setText(" Update ");
            //Toast.makeText(activity,"pos: "+adapter.getPosition(location.addressType),Toast.LENGTH_SHORT).show();
            spinnerAddressType.setSelection(adapter.getPosition(location.addressType)+1,true);
            txtName.setText(location.name);
            txtAddress.setText(location.address);
        }
    }
    private Boolean attemptSave()
    {
        txtName.setError(null);
        txtAddress.setError(null);

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(txtName.getText().toString()))
        {
            txtName.setError("Please Enter Name");
            focusView = txtName;
            cancel = true;
        }
        if(TextUtils.isEmpty(txtAddress.getText().toString()))
        {
            txtAddress.setError("Please Enter Address");
            focusView = txtAddress;
            cancel = true;
        }
        if (spinnerAddressType.getSelectedItem()==null) {
            spinnerAddressType.setError("Please Select Type");
            focusView = spinnerAddressType;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_add_location) {
            if (attemptSave()) {
                SQLiteHandler db = new SQLiteHandler(activity);
                ContentValues values = new ContentValues();
                values.put("name", txtName.getText().toString());
                values.put("address", txtAddress.getText().toString());
                values.put("address_type", spinnerAddressType.getSelectedItem().toString());

                if (selectedPlace != null) {
                    values.put("latitude", selectedPlace.getLatLng().latitude);
                    values.put("longitude", selectedPlace.getLatLng().longitude);
                }
                if (type == 0) {
                    db.addLocation(values);
                    Toast.makeText(activity, "Location saved", Toast.LENGTH_SHORT).show();
                } else {
                    db.updateLocation(location.id, values);
                    Toast.makeText(activity, "Location updated", Toast.LENGTH_SHORT).show();
                }

                spinnerAddressType.setSelection(-1);
                txtAddress.setText("");
                dismiss();
            }
        } else if (id == R.id.txt_address) {
            Intent i = new Intent(activity, SelectPlaceActivity.class);
            activity.startActivityForResult(i, SELECT_ADDRESS);
        }
    }



    /*@Override
    public void onActivityResult(int reqCode, int resultCode, Intent intent) {
        super.onActivityResult(reqCode, resultCode, intent);
        switch (reqCode) {
            case (Constant.PICK_PLAN) :
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        amount = extras.getString("amount");
                        txtAmount.setText(amount);
                    }
                }
                break;
        }
    }*/


}
