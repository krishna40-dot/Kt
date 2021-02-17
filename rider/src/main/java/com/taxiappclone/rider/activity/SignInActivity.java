package com.taxiappclone.rider.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.taxiappclone.rider.R;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.taxiappclone.common.app.CommonUtils.isConnected;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String REQ_GET_USER = "req_get_user";
    private static String TAG = SignInActivity.class.getSimpleName();
    private EditText inputMobile;
    private Button btnForgotPassword;
    private ProgressDialog pDialog;
    private SessionManager session;

    private String name = "";
    private String mobile = "";
    private Toolbar toolbar;
    private Button btnSignin, btnSignup;
    private View rootView;
    private Snackbar snackbar;
    private FirebaseAuth fbAuth;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        CommonUtils.setStatusBarGradient(this);

        fbAuth = FirebaseAuth.getInstance();

        this.rootView = (ScrollView) findViewById(R.id.rootView);
        this.btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        this.btnSignin = (Button) findViewById(R.id.btnSignin);
        this.btnSignup = (Button) findViewById(R.id.btnSignup);
        this.inputMobile = (EditText) findViewById(R.id.inputMobile);

        apiService = ApiUtils.INSTANCE.getApiService();

        btnForgotPassword.setPaintFlags(btnForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnSignin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        if (!isConnected(this)) {
            showSnackBar("Sorry! You are offline.", rootView);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnSignin:
                CommonUtils.hideKeyboard(this);
                if (attemptLogin()) {
                    if (isConnected(this)) {
                        session.setValue("name", name);
                        session.setValue("mobile", mobile);
                        if (!mobile.contains("+91")) {
                            mobile = "+91 " + mobile;
                        }
                        checkUser(mobile);
//                        checkLogin(name, mobile);
                    } else {
                        showSnackBar("No Internet Connection!.", rootView);
                    }
                }
                break;
            case R.id.btnForgotPassword:
                intent = new Intent(getApplicationContext(), ForgotPassowrdActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnSignup:
                intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void checkUser(String phoneNo) {
        pDialog.setMessage("Please wait...");
        showDialog();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("phone_number", phoneNo)
                .addFormDataPart("is_rider", "1")
                .build();
        apiService.checkRegisteredUser(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.body().get("status").getAsBoolean()) {
                    showToast("This number is already registered as Driver");
                } else {
                    Intent  intent = new Intent(SignInActivity.this,OtpActivity.class);
                    intent.putExtra("mobile",mobile);
                    startActivity(intent);
                    finish();
                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        });
    }

    public Boolean attemptLogin() {
        inputMobile.setError(null);

        mobile = inputMobile.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mobile)) {
            inputMobile.setError("Please enter your Mobile Number");
            focusView = inputMobile;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public void showSnackBar(String string, View view) {
        snackbar = Snackbar
                .make(view, string, Snackbar.LENGTH_INDEFINITE).
                        setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                snackbar.dismiss();
                            }
                        });
        snackbar.show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

