package com.mbsindia.driver.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.taxiappclone.common.activity.CustomActivity;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.Driver;
import com.taxiappclone.common.model.DriverLoginResponse;
import com.taxiappclone.common.model.Rider;
import com.taxiappclone.common.network.APIService;
import com.taxiappclone.common.network.ApiUtils;
import com.taxiappclone.common.utils.ServerUtilities;
import com.mbsindia.driver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.taxiappclone.common.app.AppConfig.CURRENT_USER;
import static com.taxiappclone.common.app.AppConfig.USER_VEHICLE;
import static com.mbsindia.driver.app.ModuleConfig.URL_GET_USER;
import static com.mbsindia.driver.app.ModuleConfig.URL_LOGIN;

public class OtpActivity extends CustomActivity implements View.OnClickListener {
    public static final String TAG = OtpActivity.class.getSimpleName();
    private static final String REQ_GET_USER = "req_get_user";
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.inputOtp)
    Pinview inputOtp;
    @BindView(R.id.btnResendOtp)
    Button btnResendOtp;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.btnResend)
    Button btnResend;
    @BindView(R.id.btnBack)
    Button btnBack;

    private int userId, time;
    private String mobile, otpCode, redirectActivity, purpose, password;
    private SessionManager session;
    private ProgressDialog pDialog;
    private Timer timer;
    private Handler handler;
    private int delay;
    private Runnable runnable;
    private FirebaseAuth fbAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private String phoneVerificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private AsyncTask<Void, Void, String> mRegisterTask;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        CommonUtils.setStatusBarGradient(this);

        apiService = ApiUtils.INSTANCE.getApiService();

        fbAuth = FirebaseAuth.getInstance();
        session = new SessionManager(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        mobile = bundle.getString("mobile");

        tvPhone.setText(mobile);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        inputOtp.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                CommonUtils.hideKeyboard(OtpActivity.this);
                verifyCode();
            }
        });

        sendOtpViaFirebase(mobile);
        inputOtp.requestPinEntryFocus();
    }

    private void startTimer() {
        btnResendOtp.setEnabled(false);
        handler = new Handler();
        delay = 1000; //milliseconds
        time = 30;
        runnable = new Runnable() {
            public void run() {
                //do something
                time--;
                if (time < 10)
                    tvTime.setText("in  00:0" + time + " minute");
                else if (time == 0)
                    tvTime.setText("Now");
                else
                    tvTime.setText("in  00:" + time + " minute");

                if (time != 0) {
                    handler.postDelayed(this, delay);
                } else {
                    btnResendOtp.setEnabled(true);
                }
            }
        };
        handler.postDelayed(runnable, delay);
    }

    private void stopTimer() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            btnResendOtp.setEnabled(true);
        }
    }

    public void sendOtpViaFirebase(String phoneNumber) {
        pDialog.setMessage("Sending OTP");
        showDialog();
        setUpVerificatonCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    private void setUpVerificatonCallbacks() {
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        hideDialog();
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        hideDialog();
                        Log.d("response", e.toString());
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(OtpActivity.this, "Invalid request, try again later.", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Toast.makeText(OtpActivity.this, "Too many requests, try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        hideDialog();
                        phoneVerificationId = verificationId;
                        resendToken = token;
                        startTimer();
                        showToast("OTP sent!");
                    }
                };
    }

    public void verifyCode() {
        String code = inputOtp.getValue();
        if (!code.equals("")) {
            pDialog.setMessage("Verifying OTP");
            showDialog();
            PhoneAuthCredential credential =
                    PhoneAuthProvider.getCredential(phoneVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        } else {
            Toast.makeText(this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // get the user info to know that user is already login or not
                            loginToServer();
                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                hideDialog();
                                showToast("Incorrect OTP!");
                            }
                        }
                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    private void loginToServer() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fb_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addFormDataPart("mobile", mobile)
                .build();

        apiService.loginDriver(requestBody).enqueue(new Callback<DriverLoginResponse>() {
            @Override
            public void onResponse(Call<DriverLoginResponse> call, Response<DriverLoginResponse> response) {
                Log.e(TAG, "onResponse:" + response.body());
                hideDialog();
                if (response.body().getStatus()) {
                    // if user is already logedin then we will save the user data and go to the enable location screen
                    session.setValue("auth_token", response.body().getToken());
                    session.setValue("auth_time", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    session.setLogin(true);

                    // after all things done we will move the user to enable location screen
                    getUserDetails();
                } else {
                    // if user is first time login then we will get the usser picture and name
                    CommonUtils.hideKeyboard(OtpActivity.this);
                    Intent intent = new Intent(OtpActivity.this, GetUserInfoActivity.class);
                    intent.putExtra("mobile", mobile);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DriverLoginResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: failed to send notification");
                hideDialog();
                showToast("Something went wrong.");
            }
        });
    }

    private void getUserDetails() {
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
                            Driver driver = new Gson().fromJson(jObj.getJSONObject("data").toString(), Driver.class);
                            session.setValue(CURRENT_USER, jObj.getJSONObject("data").toString());
                            session.setValue(USER_VEHICLE, jObj.getJSONObject("vehicle").toString());
                            session.setValue("image", driver.getImage());
                            session.setValue("mobile", driver.getMobile());
                            session.setValue("name", driver.getName());
                            session.setValue("dob", driver.getDob());
                            session.setValue("gender", driver.getGender());
                            session.setValue("status", driver.getStatus());
                            if (driver.getStatus() == 1) {
                                session.setLogin(true);
                                Intent intent = new Intent(OtpActivity.this, EnableLocationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else if (driver.getStatus() == -2) {
                                new AlertDialog.Builder(OtpActivity.this).setTitle(getString(R.string.account_deleted)).setMessage(Html.fromHtml(getResources().getString(R.string.msg_account_deleted) + "<br><br><font color='red'>" + driver.getReason() + "</font>"))
                                        .setPositiveButton(getString(R.string.exit), (dialog, which) -> {
                                            dialog.dismiss();
                                            Intent intent = new Intent(OtpActivity.this, SignInActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }).setCancelable(false).create().show();
                            } else {
                                session.setLogin(true);
                                Intent intent = new Intent(OtpActivity.this, AccountStatusActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            session.setLogin(false);
                            Intent intent = new Intent(OtpActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                }
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("verification"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("verification")) {
                otpCode = intent.getStringExtra("message");
                //txtOtp.setText(otpCode);
                //Toast.makeText(getApplicationContext(),"Message:-"+message,Toast.LENGTH_SHORT).show();
                //Do whatever you want with the code here
            }
        }
    };

    private void resendOtp(String phoneNo) {
        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnResendOtp:
                resendOtp(mobile);
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (mRegisterTask != null)
            mRegisterTask.cancel(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
    }

    @OnClick({R.id.btnResendOtp, R.id.btnResend, R.id.btnBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnResendOtp:
                resendOtp(mobile);
                break;
            case R.id.btnResend:
                resendOtp(mobile);
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }
}