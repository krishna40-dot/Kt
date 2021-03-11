package com.mbsindia.rider.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.utils.ServerUtilities;
import com.mbsindia.rider.R;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

import static com.taxiappclone.common.app.AppConfig.ADD_LOCATION;
import static com.taxiappclone.common.app.AppConfig.SELECT_ADDRESS;
import static com.taxiappclone.common.utils.ServerUtilities.POST;

public class AddCardActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private MaterialEditText txtName, txtCardNumber, txtExpiry, txtAddress, txtPostalCode;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private SessionManager session;
    private Button btnAddCard;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private String TAG = AddCardActivity.class.getSimpleName();
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private double latitude, longitude;
    private String address, city, state, country, countryCode, postalCode;

    /**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add a credit card");


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(this);

        txtName = findViewById(R.id.txt_name);
        txtCardNumber = findViewById(R.id.txt_card_number);
        txtExpiry = findViewById(R.id.txt_expiry);
        txtAddress = findViewById(R.id.txt_address);
        txtPostalCode = findViewById(R.id.txt_postal_code);
        btnAddCard = findViewById(R.id.btn_add_card);

        txtAddress.setOnClickListener(this);
        btnAddCard.setOnClickListener(this);
        txtExpiry.addTextChangedListener(new TextWatcher() {
            int beforeLength = 0;
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (beforeLength == 1 && txtExpiry.getText().length() == 2 && s.charAt(s.length()-1) != '/') {
                    txtExpiry.setText(txtExpiry.getText().toString() + "/");
                }
                if (beforeLength == 3 && txtExpiry.getText().length() == 2) {
                 txtExpiry.setText(txtExpiry.getText().charAt(0)+"");
                }
                if (txtExpiry.getText().length() < 3) {
                    if(txtExpiry.getText().toString().contains("/"))
                        txtExpiry.setText(txtExpiry.getText().toString().replace("/", ""));
                }
                if (txtExpiry.getText().length() > 3 && !txtExpiry.getText().toString().contains("/")) {
                    txtExpiry.setText("");
                }
                txtExpiry.post(new Runnable() {
                    @Override
                    public void run() {
                        txtExpiry.setSelection(txtExpiry.getText().length());
                    }
                });
                beforeLength = txtExpiry.getText().length();
            }
        });

        txtAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    Intent intent = new Intent(AddCardActivity.this, SelectPlaceActivity.class);
                    startActivityForResult(intent,SELECT_ADDRESS);
                }
            }
        });

    }

    private Boolean attemptSave()
    {
        txtName.setError(null);
        txtCardNumber.setError(null);
        txtExpiry.setError(null);

        boolean cancel = false;
        View focusView = null;


        if(TextUtils.isEmpty(txtPostalCode.getText().toString()))
        {
            txtPostalCode.setError("Please enter postal code");
            focusView = txtPostalCode;
            cancel = true;
        }
        if(TextUtils.isEmpty(txtAddress.getText().toString()))
        {
            txtAddress.setError("Please enter address on card");
            focusView = txtAddress;
            cancel = true;
        }
        if(TextUtils.isEmpty(txtExpiry.getText().toString()))
        {
            txtExpiry.setError("Please enter expiry");
            focusView = txtExpiry;
            cancel = true;
        }
        if(TextUtils.isEmpty(txtCardNumber.getText().toString()))
        {
            txtCardNumber.setError("Please enter card number");
            focusView = txtCardNumber;
            cancel = true;
        }
        if(TextUtils.isEmpty(txtName.getText().toString()))
        {
            txtName.setError("Please Enter Name on Card");
            focusView = txtName;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void addPaymentMethod() throws JSONException, ParseException {
        pDialog.setMessage("Saving...");
        showDialog();
        final JSONObject param1 = new JSONObject();
        param1.put("card_number", txtCardNumber.getText().toString());
        param1.put("card_holder_name", txtName.getText().toString());

        JSONObject paramBillingAddress = new JSONObject();
        paramBillingAddress.put("name",txtName.getText().toString());
        paramBillingAddress.put("phone",session.getStringValue("cellular_phone1"));
        paramBillingAddress.put("city",city);
        paramBillingAddress.put("country",country);
        paramBillingAddress.put("country_code",countryCode);
        paramBillingAddress.put("postal_code",txtPostalCode.getText().toString());
        paramBillingAddress.put("address_line1",txtAddress.getText().toString());
        paramBillingAddress.put("latitude",latitude);
        paramBillingAddress.put("longitude",longitude);

        String expiresAt = txtExpiry.getText().toString();
        SimpleDateFormat parser = new SimpleDateFormat("MM/yy");
        // output format: yyyy-MM-dd
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        formatter.setTimeZone(TimeZone.getTimeZone("US/Central"));

        String expireDate = formatter.format(parser.parse(expiresAt));

        param1.put("billing_address",paramBillingAddress);
        param1.put("expires_at",expireDate);

        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "bearer "+session.getStringValue("user_access_token"));

        showAlert(true,"Request Params",param1.toString());

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();
            @Override
            protected void onPreExecute() {
                showDialog();
            }
            @Override
            protected String doInBackground(Void... voids) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getJsonResponse(POST, AppConfig.URL_ADD_CREDIT_CARD, param1, headers);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d(TAG, "Add Card Response: " + result.toString());
                showAlert(true,"Response",result);
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        if(jObj.has("reason_phrase"))
                        {

                        }
                        else
                        {
                            SQLiteHandler db = new SQLiteHandler(AddCardActivity.this);
                            JSONObject itemObj = new JSONObject(result);
                            int id = itemObj.getInt("id");
                            ContentValues values = new ContentValues();
                            values.put("server_id", id);
                            values.put("card_holder_name", itemObj.getString("card_holder_name"));
                            values.put("card_network", itemObj.getString("card_network"));
                            values.put("card_first_digit", itemObj.getString("card_number_first_digit"));
                            values.put("card_last_digits",itemObj.getString("card_number_last_digit"));
                            values.put("billing_address",itemObj.getString("billing_address"));
                            if(!db.checkCreditCardExists(id))
                                db.addCreditCard(values);
                            else
                                db.updateCreditCard(id,values);
                            showToast("Saved");
                            Intent intent = new Intent();
                            intent.putExtra("referesh",true);
                            setResult(ADD_LOCATION,intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideDialog();
                } else {
                    showToast("Some error occurred!");
                }
                hideDialog();
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Bundle extras = null;
        if(resultCode == RESULT_OK ) {
            switch (requestCode) {
                case SELECT_ADDRESS:
                    extras = intent.getExtras();
                    if (extras != null) {
                        address = extras.getString("address");
                        city = extras.getString("city");
                        state = extras.getString("state");
                        country = extras.getString("country");
                        countryCode = extras.getString("country_code");
                        postalCode = extras.getString("postal_code");
                        latitude = extras.getDouble("latitude");
                        longitude = extras.getDouble("longitude");

                        txtAddress.setText(address);
                    }
                    break;
            }
        }
    }

    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showAlert(boolean status, String title, String msg)
    {
        if(builder==null) {
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
    public void onBackPressed() {
        //saveData(f_phone_no);
        super.onBackPressed();
        finish();
        //overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId())
        {
            case R.id.txt_address:
                intent = new Intent(this,SelectPlaceActivity.class);
                startActivityForResult(intent,SELECT_ADDRESS);
                break;
            case R.id.btn_add_card:
                if(attemptSave())
                {
                    try {
                        addPaymentMethod();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            break;
        }
    }
}
