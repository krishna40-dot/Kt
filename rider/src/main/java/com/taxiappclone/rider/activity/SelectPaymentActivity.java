package com.taxiappclone.rider.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.model.CreditCardListItem;
import com.taxiappclone.rider.R;
import com.taxiappclone.common.adapter.CreditCardListAdapter;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.utils.RecyclerViewClickListener;
import com.taxiappclone.common.utils.ServerUtilities;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import fr.ganfra.materialspinner.MaterialSpinner;


import static android.view.View.GONE;
import static com.taxiappclone.common.app.AppConfig.SELECT_ADDRESS;
import static com.taxiappclone.common.utils.ServerUtilities.GET;
import static com.taxiappclone.common.utils.ServerUtilities.POST;

public class SelectPaymentActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = SelectPaymentActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ActionBar actionBar;
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private RecyclerView listView;
    private CreditCardListAdapter listAdapter;
    private List<CreditCardListItem> listItems = new ArrayList<>();
    private AsyncTask<Void, Void, String> mRegisterTask;

    private MaterialSpinner spinnerPaymentType, spinnerCardType;
    private MaterialEditText txtName, txtCardNumber, txtExpiry, txtCvv, txtAddress, txtPostalCode;
    private TextView tvCardNumber,tvCardNetwork, tvOriginalAmount, tvReturnAmount, tvTotalAmount;
    private View viewSelectedCard;
    private CheckBox checkSelectedCard, checkSaveCard;
    private boolean selectedCard=false;
    private Button btnBookNow;
    private View viewNewCard, viewReturnTrip;
    private int paymentTypeId, creditCardId=0, paymentTypeIds[] ={99816,99822};
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private double latitude, longitude;
    private String address, city, state, country, countryCode, postalCode;
    private Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Payment Information");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(this);

        bundle = getIntent().getExtras();

        spinnerPaymentType = findViewById(R.id.spinner_payment_type);
        spinnerCardType = findViewById(R.id.spinner_card_type);
        txtName = findViewById(R.id.txt_name);
        txtCardNumber = findViewById(R.id.txt_card_number);
        txtExpiry = findViewById(R.id.txt_expiry);
        txtAddress = findViewById(R.id.txt_address);
        txtPostalCode = findViewById(R.id.txt_postal_code);
        checkSaveCard = findViewById(R.id.check_save_card);
        btnBookNow = findViewById(R.id.btn_book_now);
        viewNewCard = findViewById(R.id.view_new_credit_card);
        listView = findViewById(R.id.list_view);
        viewSelectedCard = findViewById(R.id.view_selected_credit_card);
        tvCardNumber = findViewById(R.id.tv_card_number);
        tvCardNetwork = findViewById(R.id.tv_card_network);
        checkSelectedCard = findViewById(R.id.check_selected_card);
        tvOriginalAmount = findViewById(R.id.tv_original_amount);
        tvReturnAmount = findViewById(R.id.tv_return_trip_amount);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        viewReturnTrip = findViewById(R.id.view_return_trip);

        txtAddress.setOnClickListener(this);
        btnBookNow.setOnClickListener(this);

        viewReturnTrip.setVisibility(GONE);
        tvOriginalAmount.setText("₹"+bundle.getDouble("original_amount"));
        tvTotalAmount.setText("₹"+bundle.getDouble("original_amount"));

        String[] ITEMS1 = {"Credit Card", "Direct Bill/Invoice"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentType.setAdapter(adapter1);

        String[] ITEMS = {"New Credit Card", "Saved Credit Cards"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCardType.setAdapter(adapter2);

        viewNewCard.setVisibility(GONE);
        spinnerCardType.setVisibility(GONE);
        listView.setVisibility(GONE);
        viewSelectedCard.setVisibility(GONE);


        spinnerPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = spinnerPaymentType.getSelectedItemPosition();

                if(pos==0)
                {
                    viewNewCard.setVisibility(GONE);
                    spinnerCardType.setVisibility(GONE);
                    listView.setVisibility(GONE);
                }

                if(pos==1) {
                    paymentTypeId = paymentTypeIds[spinnerPaymentType.getSelectedItemPosition()-1];
                    spinnerCardType.setVisibility(View.VISIBLE);
                    if(spinnerCardType.getSelectedItemPosition()==1)
                        spinnerCardType.setVisibility(View.VISIBLE);
                    if(spinnerCardType.getSelectedItemPosition()==2)
                        listView.setVisibility(View.VISIBLE);
                }
                else if(pos==2)
                {
                    paymentTypeId = paymentTypeIds[spinnerPaymentType.getSelectedItemPosition()-1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                viewNewCard.setVisibility(GONE);
                spinnerCardType.setVisibility(GONE);

            }
        });

        spinnerCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = spinnerCardType.getSelectedItemPosition();
                if(pos==1) {
                    listView.setVisibility(GONE);
                    viewNewCard.setVisibility(View.VISIBLE);
                }
                if(pos==2) {
                    viewNewCard.setVisibility(GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerCardType.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
            }
        });

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
                    Intent intent = new Intent(SelectPaymentActivity.this, SelectPlaceActivity.class);
                    startActivityForResult(intent,SELECT_ADDRESS);
                }
            }
        });

        checkSelectedCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(!checked)
                {
                    listView.setVisibility(View.VISIBLE);
                    viewSelectedCard.setVisibility(GONE);
                    selectedCard = false;
                }
            }
        });

        listAdapter = new CreditCardListAdapter(this, listItems);
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

        listView.addOnItemTouchListener(new RecyclerViewClickListener.RecyclerTouchListener(this, listView, new RecyclerViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final CreditCardListItem item = listItems.get(position);

                creditCardId = item.cardId;
                tvCardNumber.setText(item.cardFirstDigit+"XXXXXXXXXXX"+item.cardLastDigits);
                tvCardNetwork.setText(item.cardNetwork.replace("_"," ").toUpperCase());
                viewSelectedCard.setVisibility(View.VISIBLE);
                listView.setVisibility(GONE);
                viewSelectedCard.setVisibility(View.VISIBLE);
                checkSelectedCard.setChecked(true);
                selectedCard = true;
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        getAllCreditCards();
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
            case R.id.btn_book_now:
                if(attemptBook())
                {
                    try {
                        if(checkSaveCard.isChecked())
                            addPaymentMethod();
                        else
                            bookNow(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
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
                            SQLiteHandler db = new SQLiteHandler(SelectPaymentActivity.this);
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
                            showToast("Card Saved");
                            creditCardId = id;
                            bookNow(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
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

    @SuppressLint("StaticFieldLeak")
    private void bookNow(boolean flag) throws JSONException, ParseException {
        pDialog.setMessage("Booking...");
        showDialog();
        final JSONObject params = new JSONObject();
        params.put("search_result_id",getIntent().getIntExtra("search_result_id",0));
        params.put("payment_type_id",paymentTypeId);

        JSONArray passengerArray = new JSONArray();
        JSONObject passenger = new JSONObject();
        passenger.put("account_id",session.getIntValue("id"));
        passenger.put("account_number",session.getStringValue("number"));
        passenger.put("first_name",session.getStringValue("first_name"));
        passenger.put("last_name",session.getStringValue("last_name"));
        passenger.put("phone",session.getStringValue("cellular_phone1"));
        passenger.put("email",session.getStringValue("email"));
        passengerArray.put(passenger);
        params.put("passengers",passengerArray);
        if(spinnerPaymentType.getSelectedItemPosition()==1){
            if(spinnerCardType.getSelectedItemPosition()==1 && !checkSaveCard.isChecked())
            {
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

                params.put("credit_card_info",param1);
            }
            else
            {
                params.put("credit_card_id",creditCardId);
            }
        }
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "bearer "+session.getStringValue("user_access_token"));

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();
            @Override
            protected void onPreExecute() {

                showDialog();
            }
            @Override
            protected String doInBackground(Void... voids) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getJsonResponse(POST, AppConfig.URL_BOOKING, params, headers);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d(TAG, "Booking Response: " + result.toString());
                //showAlert(true,"Bookinh Response",result);
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        if(jObj.has("reason_phrase"))
                        {

                        }
                        else
                        {
                            Intent intent = new Intent(SelectPaymentActivity.this, BookingConfirmedActivity.class);
                            intent.putExtra("confirmation_number",jObj.getString("confirmation_number"));
                            startActivity(intent);
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

    private Boolean attemptBook() {
        txtName.setError(null);
        txtCardNumber.setError(null);
        txtExpiry.setError(null);

        boolean cancel = false;
        View focusView = null;

        if(spinnerPaymentType.getSelectedItemPosition()==0)
        {
            spinnerPaymentType.setError("Please select payment type");
            focusView = spinnerPaymentType;
            cancel = true;
        }

        if(spinnerPaymentType.getSelectedItemPosition()==1&&spinnerCardType.getSelectedItemPosition()==0) {
            spinnerCardType.setError("Please select card type");
            focusView = spinnerCardType;
            cancel = true;
        }

        if(spinnerPaymentType.getSelectedItemPosition()==1&&spinnerCardType.getSelectedItemPosition()==1) {
            if (TextUtils.isEmpty(txtPostalCode.getText().toString())) {
                txtPostalCode.setError("Please enter postal code");
                focusView = txtPostalCode;
                cancel = true;
            }
            if (TextUtils.isEmpty(txtAddress.getText().toString())) {
                txtAddress.setError("Please enter address on card");
                focusView = txtAddress;
                cancel = true;
            }
            if (TextUtils.isEmpty(txtExpiry.getText().toString())) {
                txtExpiry.setError("Please enter expiry");
                focusView = txtExpiry;
                cancel = true;
            }
            if (TextUtils.isEmpty(txtCardNumber.getText().toString())) {
                txtCardNumber.setError("Please enter card number");
                focusView = txtCardNumber;
                cancel = true;
            }
            if (TextUtils.isEmpty(txtName.getText().toString())) {
                txtName.setError("Please Enter Name on Card");
                focusView = txtName;
                cancel = true;
            }
        }
        if(spinnerPaymentType.getSelectedItemPosition()==1 && spinnerCardType.getSelectedItemPosition()==2) {
            if(!checkSelectedCard.isChecked())
            {
                cancel = true;
                showAlert(false,"","Please select a credit card.");
                focusView = listView;
            }
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void getAllCreditCards()
    {
        listItems.clear();
        Cursor c = db.getCreditCards(1);
        if(c.getCount()>0) {
            while (c.moveToNext()) {
                CreditCardListItem item = new CreditCardListItem(c.getInt(c.getColumnIndex("server_id")),c.getString(c.getColumnIndex("card_holder_name")), c.getString(c.getColumnIndex("card_network")), c.getString(c.getColumnIndex("card_first_digit")), c.getString(c.getColumnIndex("card_last_digits")), c.getString(c.getColumnIndex("billing_address")), c.getString(c.getColumnIndex("expires_at")));
                listItems.add(item);
            }
            listAdapter.notifyDataSetChanged();
        }else{
            try {
                getCollectionFromServer();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getCollectionFromServer() throws JSONException {
        pDialog.setMessage("Loading...");
        showDialog();
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "bearer "+session.getStringValue("user_access_token"));

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();
            @Override
            protected void onPreExecute() {

                showDialog();
            }
            @Override
            protected String doInBackground(Void... voids) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getJsonResponse(GET, AppConfig.URL_GET_CREDIT_CARDS+"/"+session.getIntValue("id")+"/credit_cards", null, headers);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d(TAG, "Address Collection Response: " + result.toString());
                //showToast(result.toString());
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        if(jObj.getInt("total_count")>0)
                        {
                            JSONArray items = jObj.getJSONArray("items");
                            for (int i=0;i<jObj.getInt("total_count");i++) {
                                JSONObject itemObj = items.getJSONObject(i);
                                int id = itemObj.getInt("id");
                                ContentValues values = new ContentValues();
                                values.put("server_id", id);
                                values.put("card_holder_name", itemObj.getString("card_holder_name"));
                                values.put("card_network", itemObj.getString("card_network"));
                                values.put("card_first_digit", itemObj.getString("card_number_first_digit"));
                                values.put("card_last_digits",itemObj.getString("card_number_last_digits"));
                                values.put("billing_address",itemObj.getString("billing_address"));

                                if(!db.checkCreditCardExists(id))
                                    db.addCreditCard(values);
                                else
                                    db.updateCreditCard(id,values);
                                CreditCardListItem item = new CreditCardListItem(id,itemObj.getString("card_holder_name"), itemObj.getString("card_network"), itemObj.getString("card_number_first_digit"), itemObj.getString("card_number_last_digits"), itemObj.getString("billing_address"), itemObj.getString("expires_at"));
                                listItems.add(item);
                            }
                            listAdapter.notifyDataSetChanged();
                        }else
                        {

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

    @Override
    protected void onResume() {
        //getAllCreditCards();
        super.onResume();
    }

    private void showToast(String msg){
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment_methods, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_add_card:
                Intent i = new Intent(this,AddCardActivity.class);
                startActivity(i);
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}