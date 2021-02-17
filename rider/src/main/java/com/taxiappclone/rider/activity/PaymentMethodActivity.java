package com.taxiappclone.rider.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.taxiappclone.common.activity.WebViewActivity;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.model.CreditCardListItem;
import com.taxiappclone.common.model.User;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.taxiappclone.rider.R;
import com.taxiappclone.common.adapter.CreditCardListAdapter;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.utils.ServerUtilities;
import com.taxiappclone.rider.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.taxiappclone.common.utils.ServerUtilities.GET;
import static com.taxiappclone.rider.app.ModuleConfig.URL_GET_USER;

public class PaymentMethodActivity extends AppCompatActivity {
    public static final String TAG = PaymentMethodActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ActionBar actionBar;
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private RecyclerView listView;
    private CreditCardListAdapter listAdapter;
    private List<CreditCardListItem> listItems = new ArrayList<>();
    private AsyncTask<Void, Void, String> mRegisterTask;

    private AppCompatTextView tvWalletBalance;
    private AppCompatButton btnAddMoney;

    private User user;
    APIService apiService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Payment Methods");

        apiService = ApiUtils.INSTANCE.getApiService();

        tvWalletBalance = findViewById(R.id.tvWalletBalance);
        btnAddMoney = findViewById(R.id.btnAddMoney);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(this);

        listView = findViewById(R.id.list_view);
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

        btnAddMoney.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_add_money);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

            MaterialEditText etName = dialog.findViewById(R.id.etName);
            MaterialEditText etMobile = dialog.findViewById(R.id.etMobile);
            MaterialEditText etEmail = dialog.findViewById(R.id.etEmail);
            MaterialEditText etAmount = dialog.findViewById(R.id.etAmount);
            AppCompatButton btnAddMoney = dialog.findViewById(R.id.btnAddMoney);

            etName.setText(user.getName());
            etMobile.setText(user.getMobile());
            etEmail.setText(user.getEmail());
            btnAddMoney.setOnClickListener(v1 -> {
                String userId = user.getId();
                String name = etName.getText().toString();
                String mobile = etMobile.getText().toString();
                String email = etEmail.getText().toString();
                String amount = etAmount.getText().toString();
                String role = "rider";

                if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
                } else {
                    proceedAddMoney(userId, name, mobile, email, amount, role, dialog);
                }
            });

            dialog.show();
        });

        getUserWallet();
        getAllCreditCards();
    }

    private void proceedAddMoney(String userId, String name, String mobile, String email, String amount, String role, Dialog dialog) {
        pDialog.setMessage("Please wait...");
        showDialog();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", userId)
                .addFormDataPart("CUSTOMER_NAME", name)
                .addFormDataPart("CUSTOMER_MOBILE", mobile)
                .addFormDataPart("CUSTOMER_EMAIL", email)
                .addFormDataPart("PAY_AMT", amount)
                .addFormDataPart("role", role)
                .build();
        apiService.addMoney(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String html = null;
                try {
                    html = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "onResponse: " + html);
                if (html == null) {
                    Toast.makeText(PaymentMethodActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Intent intent = new Intent(PaymentMethodActivity.this, WebViewActivity.class);
                    intent.putExtra("htmlContent", html);
                    intent.putExtra("title", "Complete Payment");
                    startActivity(intent);
                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PaymentMethodActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        });
    }

    private void getUserWallet() {
        pDialog.setMessage("Loading...");
        showDialog();
        final Map<String, String> param = new HashMap<String, String>();
        param.put("mobile", session.getStringValue("mobile"));

        final Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", session.getStringValue("auth_token"));

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_GET_USER, param, header);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                hideDialog();
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        Log.d(TAG, "User Detail Response: " + result);
                        if (jObj.optBoolean("status")) {
                            String userData = jObj.getJSONObject("data").toString();
                            session.setValue(AppConfig.USER, userData);
                            user = AppController.getInstance().getUser();
                            tvWalletBalance.setText(user.getWallet());
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                }
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    private void getAllCreditCards() {
        listItems.clear();
        Cursor c = db.getCreditCards(1);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                CreditCardListItem item = new CreditCardListItem(c.getInt(c.getColumnIndex("server_id")), c.getString(c.getColumnIndex("card_holder_name")), c.getString(c.getColumnIndex("card_network")), c.getString(c.getColumnIndex("card_first_digit")), c.getString(c.getColumnIndex("card_last_digits")), c.getString(c.getColumnIndex("billing_address")), c.getString(c.getColumnIndex("expires_at")));
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
        pDialog.setMessage("Loading...");
        showDialog();
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "bearer " + session.getStringValue("user_access_token"));

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected void onPreExecute() {

                showDialog();
            }

            @Override
            protected String doInBackground(Void... voids) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getJsonResponse(GET, AppConfig.URL_GET_CREDIT_CARDS + "/" + session.getIntValue("id") + "/credit_cards", null, headers);
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
                                ContentValues values = new ContentValues();
                                values.put("server_id", id);
                                values.put("card_holder_name", itemObj.getString("card_holder_name"));
                                values.put("card_network", itemObj.getString("card_network"));
                                values.put("card_first_digit", itemObj.getString("card_number_first_digit"));
                                values.put("card_last_digits", itemObj.getString("card_number_last_digits"));
                                values.put("billing_address", itemObj.getString("billing_address"));

                                if (!db.checkCreditCardExists(id))
                                    db.addCreditCard(values);
                                else
                                    db.updateCreditCard(id, values);
                                CreditCardListItem item = new CreditCardListItem(id, itemObj.getString("card_holder_name"), itemObj.getString("card_network"), itemObj.getString("card_number_first_digit"), itemObj.getString("card_number_last_digits"), itemObj.getString("billing_address"), itemObj.getString("expires_at"));
                                listItems.add(item);
                            }
                            listAdapter.notifyDataSetChanged();
                        } else {

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
    protected void onResume() {
        //getAllCreditCards();
        super.onResume();
        getUserWallet();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment_methods, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_card:
                Intent i = new Intent(this, AddCardActivity.class);
                startActivity(i);
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}