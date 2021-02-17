package com.taxiappclone.rider.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.Rider;
import com.taxiappclone.common.utils.PermissionManager;
import com.taxiappclone.common.activity.SelectPhotoActivity;
import com.taxiappclone.common.utils.ServerUtilities;
import com.taxiappclone.common.view.CircleImageView;
import com.taxiappclone.rider.R;
import com.taxiappclone.rider.app.AppController;
import com.taxiappclone.rider.app.ModuleConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;

import static com.taxiappclone.common.app.AppConfig.CURRENT_USER;
import static com.taxiappclone.common.app.AppConfig.SELECT_PHOTO;
import static com.taxiappclone.common.app.AppConfig.TABLE_RIDERS;
import static com.taxiappclone.common.app.CommonUtils.bitmapToString;
import static com.taxiappclone.common.app.CommonUtils.showAlert;
import static com.taxiappclone.rider.app.ModuleConfig.URL_CREATE_USER;
import static com.taxiappclone.rider.app.ModuleConfig.URL_GET_USER;


public class GetUserInfoActivity extends AppCompatActivity {

    private static final String TAG = GetUserInfoActivity.class.getSimpleName();
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.inputName)
    EditText inputName;
    @BindView(R.id.inputDob)
    EditText inputDob;
    @BindView(R.id.spinnerGender)
    MaterialSpinner spinnerGender;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    String name, dob, gender, mobile, email;
    @BindView(R.id.inputEmail)
    MaterialEditText inputEmail;
    @BindView(R.id.imageView)
    CircleImageView imageView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.viewUploadPhoto)
    RelativeLayout viewUploadPhoto;
    @BindView(R.id.tvImageError)
    TextView tvImageError;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private ProgressDialog pDialog;
    private SessionManager session;
    private int mYear = 0, mMonth, mDay, mHour, mMinute;
    private String myToken, userChoosenTask, profileImage;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_info);
        ButterKnife.bind(this);

        CommonUtils.setStatusBarGradient(this);

        mobile = getIntent().getStringExtra("mobile");
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        session = AppController.getInstance().getSessionManager();

        inputDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    setDateTime(inputDob);
            }
        });


        inputEmail.setVisibility(View.GONE);
    }

    private void setDateTime(final EditText editText) {
        // Get Current Date
        if (mYear == 0) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        String strDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date = format.parse(strDate);
                            dob = format.format(date);
                            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
                            editText.setText(format2.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private boolean attemptSubmit() {
        name = inputName.getText().toString();
        dob = inputDob.getText().toString();
        email = inputEmail.getText().toString();

        inputName.setError(null);
        inputEmail.setError(null);
        inputDob.setError(null);
        boolean cancel = false;
        View view = null;
        if (spinnerGender.getSelectedItemPosition() <= 0) {
            spinnerGender.setError("Please select your gender");
            cancel = true;
            view = spinnerGender;
        } else {
            gender = spinnerGender.getSelectedItem().toString();
        }
        if (TextUtils.isEmpty(inputDob.getText().toString())) {
            inputDob.setError("Please select your Date of Birth");
            cancel = true;
            view = inputDob;
        }
        /*if (TextUtils.isEmpty(inputEmail.getText().toString())) {
            inputDob.setError("Please enter your email");
            cancel = true;
            view = inputDob;
        }*/
        if (TextUtils.isEmpty(inputName.getText().toString())) {
            inputName.setError("Please enter your full name");
            cancel = true;
            view = inputName;
        }

        if (TextUtils.isEmpty(profileImage)) {
            tvImageError.setVisibility(View.VISIBLE);
            cancel = true;
            view = tvImageError;
        } else {
            tvImageError.setVisibility(View.GONE);
        }
        if (cancel) {
            view.requestFocus();
            return false;
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private void saveUserInfo() {
        Log.e(TAG, "saveUserInfo: ");
        showDialog();
        myToken = FirebaseInstanceId.getInstance().getToken();
        Rider rider = new Rider();
        rider.setName(name);
        rider.setMobile(mobile);
        rider.setGender(gender.toLowerCase());
        rider.setToken(myToken);
        FirebaseDatabase.getInstance().getReference(TABLE_RIDERS).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(rider)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "onSuccess: ");
                        saveToServer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        hideDialog();
                        showAlert(GetUserInfoActivity.this, 0, "Registration failed!", e.getMessage());
                    }
                });
    }

    private void saveToServer() {
        Log.e(TAG, "saveToServer: ");
        final Map<String, String> param = new HashMap<String, String>();
        String mobileEncoded = mobile;
        try {
            mobileEncoded = URLEncoder.encode(mobileEncoded, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        param.put("mobile", mobileEncoded);
        param.put("fb_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        param.put("name", name);
        param.put("dob", dob);
        param.put("gender", gender.toLowerCase());
        param.put("token", myToken);
        param.put("image", profileImage);

        final Map<String, String> header = new HashMap<String, String>();
        header.put("SECURE-API-KEY", ModuleConfig.RIDER_API_KEY);

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_CREATE_USER, param, header);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                hideDialog();
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        Log.d(TAG, "Register Response: " + result);
                        if (jObj.optBoolean("status")) {
                            // if user is already logedin then we will save the user data and go to the enable location screen
                            session.setValue("auth_token", jObj.getString("token"));
                            session.setValue("auth_time", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                            session.setLogin(true);

                            getUserDetails();
                        } else {
                            // if user is first time login then we will get the usser picture and name
                            showAlert(GetUserInfoActivity.this, 0, "Registration failed!", jObj.getString("message"));
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
                            session.setValue(CURRENT_USER, jObj.getJSONObject("data").toString());
                            session.setValue("image", jObj.getJSONObject("data").getString("image"));
                            session.setValue("mobile", mobile);
                            session.setValue("name", name);
                            session.setValue("dob", dob);
                            session.setValue("gender", gender);
                            session.setLogin(true);

                            Intent intent = new Intent(GetUserInfoActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            session.setLogin(false);
                            Intent intent = new Intent(GetUserInfoActivity.this, SignInActivity.class);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @OnClick({R.id.btnBack, R.id.btnSubmit, R.id.inputDob, R.id.imageView, R.id.viewUploadPhoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.inputDob:
                setDateTime(inputDob);
                break;
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnSubmit:
                CommonUtils.hideKeyboard(this);
                if (attemptSubmit()) {
                    saveUserInfo();
                }
                break;
            case R.id.imageView:
                CommonUtils.hideKeyboard(this);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    new PermissionManager(this, this).getCameraMediaPermissions();
                } else {
                    Intent intent = new Intent(this, SelectPhotoActivity.class);
                    startActivityForResult(intent, SELECT_PHOTO);
                }
            case R.id.viewUploadPhoto:
                CommonUtils.hideKeyboard(this);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    new PermissionManager(this, this).getCameraMediaPermissions();
                } else {
                    Intent intent = new Intent(this, SelectPhotoActivity.class);
                    startActivityForResult(intent, SELECT_PHOTO);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PHOTO) {
                if (data.hasExtra("image")) {
                    byte[] byteArray = data.getByteArrayExtra("image");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imageView.setImageBitmap(bitmap);
                    viewUploadPhoto.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    profileImage = bitmapToString(bitmap);
                }
            }
        }
    }
}
