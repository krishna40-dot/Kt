package com.mbsindia.driver.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.taxiappclone.common.activity.CustomActivity;
import com.taxiappclone.common.activity.SelectPhotoActivity;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.Driver;
import com.taxiappclone.common.model.FbDriver;
import com.taxiappclone.common.model.Rider;
import com.taxiappclone.common.model.Vehicle;
import com.taxiappclone.common.model.VehicleType;
import com.taxiappclone.common.utils.PermissionManager;
import com.taxiappclone.common.utils.ServerUtilities;
import com.taxiappclone.common.view.CircleImageView;
import com.mbsindia.driver.R;
import com.mbsindia.driver.app.AppController;
import com.mbsindia.driver.app.ModuleConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;

import static com.taxiappclone.common.activity.SelectPhotoActivity.FREE_SIZE_IMAGE;
import static com.taxiappclone.common.app.AppConfig.CURRENT_USER;
import static com.taxiappclone.common.app.AppConfig.SELECT_DRIVING_LICENSE;
import static com.taxiappclone.common.app.AppConfig.SELECT_INSURANCE;
import static com.taxiappclone.common.app.AppConfig.SELECT_PHOTO;
import static com.taxiappclone.common.app.AppConfig.SELECT_POLICE;
import static com.taxiappclone.common.app.AppConfig.SELECT_RC;
import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS;
import static com.taxiappclone.common.app.AppConfig.TABLE_RIDERS;
import static com.taxiappclone.common.app.AppConfig.USER_VEHICLE;
import static com.taxiappclone.common.app.CommonUtils.bitmapToString;
import static com.taxiappclone.common.app.CommonUtils.showAlert;
import static com.mbsindia.driver.app.CommonUtils.showToast;
import static com.mbsindia.driver.app.ModuleConfig.URL_CREATE_USER;
import static com.mbsindia.driver.app.ModuleConfig.URL_GET_USER;
import static com.mbsindia.driver.app.ModuleConfig.URL_GET_VEHICLE_TYPES;
import static com.mbsindia.driver.app.ModuleConfig.URL_UPDATE_USER;
import static com.mbsindia.driver.app.ModuleConfig.URL_UPLOAD_PATH;

public class GetUserInfoActivity extends CustomActivity {

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
    @BindView(R.id.spinnerVehicleType)
    MaterialSpinner spinnerVehicleType;
    @BindView(R.id.inputModel)
    MaterialEditText inputModel;
    @BindView(R.id.inputRegistrationNumber)
    MaterialEditText inputRegistrationNumber;
    @BindView(R.id.imgDrivingLicense)
    ImageView imgDrivingLicense;
    @BindView(R.id.viewUploadDrivingLicense)
    RelativeLayout viewUploadDrivingLicense;
    @BindView(R.id.inputExpiry)
    MaterialEditText inputExpiry;
    @BindView(R.id.imgRegistrationCard)
    ImageView imgRegistrationCard;
    @BindView(R.id.viewUploadRC)
    RelativeLayout viewUploadRC;
    @BindView(R.id.imgInsurance)
    ImageView imgInsurance;
    @BindView(R.id.viewInsurance)
    RelativeLayout viewInsurance;
    @BindView(R.id.imgPolice)
    ImageView imgPolice;
    @BindView(R.id.viewPolice)
    RelativeLayout viewPolice;
    @BindView(R.id.rootView)
    LinearLayout rootView;
    @BindView(R.id.tvUploadLicense)
    TextView tvUploadLicense;
    @BindView(R.id.tvUploadRc)
    TextView tvUploadRc;
    @BindView(R.id.tvUploadInsurance)
    TextView tvUploadInsurance;
    @BindView(R.id.tvUploadPolice)
    TextView tvUploadPolice;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private ProgressDialog pDialog;
    private SessionManager session;
    private int mYear = 0, mMonth, mDay, mHour, mMinute, vehicleType;
    private String name, dob, gender, mobile, email,
            myToken, profileImage, drivingLicenseImage, rcImage, insuranceImage, policeImage;
    private List<VehicleType> listVehicleTypes = new ArrayList();
    private DatePickerDialog expiryDatePickerDialog;
    private Driver driver;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_info);
        ButterKnife.bind(this);

        CommonUtils.setStatusBarGradient(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        session = AppController.getInstance().getSessionManager();

        inputDob.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                setBithDate(inputDob);
        });
        inputExpiry.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                setExpiryDate(inputExpiry);
        });
        inputEmail.setVisibility(View.GONE);

        if (session.getStringValue("vehicle_types").equals("")) {
            getVehicleTypes();
        } else {
            listVehicleTypes = new Gson().fromJson(session.getStringValue("vehicle_types"), new TypeToken<List<VehicleType>>() {
            }.getType());
            setVehicleTypes();
        }
        driver = AppController.getInstance().getCurrentUser();
        vehicle = AppController.getInstance().getVehicleInfo();

        if (driver != null) {
            mobile = driver.getMobile();
            Glide.with(this).load(URL_UPLOAD_PATH + "drivers/" + driver.getImage()).into(imageView);
            viewUploadPhoto.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            inputName.setText(driver.getName());
            inputDob.setText(driver.getDob());
            List<String> listGender = new ArrayList<>(Arrays.asList("Male", "Female", "Other"));
            spinnerGender.setSelection(listGender.indexOf(CommonUtils.capitalize(driver.getGender())) + 1);

            for (VehicleType vehicleType : listVehicleTypes) {
                if (vehicleType.getId() == vehicle.getVehicle_type()) {
                    Log.d(TAG, "vehicle index: " + listVehicleTypes.indexOf(vehicleType));
                    spinnerVehicleType.setSelection(listVehicleTypes.indexOf(vehicleType) + 1);
                }
            }
            inputModel.setText(vehicle.getVehicle_model());
            inputRegistrationNumber.setText(vehicle.getVehicle_number());
            inputExpiry.setText(vehicle.getExpiry_date());

            Glide.with(this).load(URL_UPLOAD_PATH + "drivers_docs/" + vehicle.getDriving_license()).into(imgDrivingLicense);
            Glide.with(this).load(URL_UPLOAD_PATH + "drivers_docs/" + vehicle.getRegistration_card()).into(imgRegistrationCard);
            Glide.with(this).load(URL_UPLOAD_PATH + "drivers_docs/" + vehicle.getInsurance()).into(imgInsurance);
            Glide.with(this).load(URL_UPLOAD_PATH + "drivers_docs/" + vehicle.getPolice()).into(imgPolice);
            imgDrivingLicense.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgDrivingLicense.setPadding(0, 0, 0, 0);
            imgRegistrationCard.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgRegistrationCard.setPadding(0, 0, 0, 0);
            imgInsurance.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgInsurance.setPadding(0, 0, 0, 0);
            imgPolice.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgPolice.setPadding(0, 0, 0, 0);

            btnSubmit.setText("UPDATE");
        } else {
            mobile = getIntent().getStringExtra("mobile");
        }
    }

    private void setVehicleTypes() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < listVehicleTypes.size(); i++) {
            String name = listVehicleTypes.get(i).getName();
            if (!listVehicleTypes.get(i).getType().equals("")) {
                name += " - " + listVehicleTypes.get(i).getType();
            }
            list.add(name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleType.setAdapter(adapter);
    }

    private void getVehicleTypes() {
        showDialog();
        final Map<String, String> header = new HashMap<String, String>();
        header.put("SECURE-API-KEY", ModuleConfig.DRIVER_API_KEY);

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), URL_GET_VEHICLE_TYPES, null, header);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                hideDialog();
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        Log.e(TAG, "Vehicle Types Response: " + result);
                        if (jObj.optBoolean("status")) {
                            session.setValue("vehicle_types", jObj.getJSONArray("data").toString());
                            listVehicleTypes = new Gson().fromJson(jObj.getJSONArray("data").toString(), new TypeToken<List<VehicleType>>() {
                            }.getType());
                            setVehicleTypes();
                            // if user is already logedin then we will save the user data and go to the enable location screen
                        } else {
                            // if user is first time login then we will get the usser picture and name
                            showAlert(GetUserInfoActivity.this, 0, "", jObj.getString("message"));
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

    private void setBithDate(final EditText editText) {
        // Get Current Date
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -18);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
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
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    private void setExpiryDate(final EditText editText) {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        expiryDatePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    String strDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = format.parse(strDate);
                        editText.setText(format.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }, mYear, mMonth, mDay);

        expiryDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + (24 * 60 * 60));
        expiryDatePickerDialog.show();
    }

    private boolean attemptSubmit() {
        name = inputName.getText().toString();
        dob = inputDob.getText().toString();
        email = inputEmail.getText().toString();

        inputName.setError(null);
        inputEmail.setError(null);
        inputDob.setError(null);
        tvUploadInsurance.setTextColor(getResources().getColor(R.color.color_gray_dark));
        tvUploadPolice.setTextColor(getResources().getColor(R.color.color_gray_dark));
        tvUploadRc.setTextColor(getResources().getColor(R.color.color_gray_dark));
        tvUploadLicense.setTextColor(getResources().getColor(R.color.color_gray_dark));
        tvImageError.setVisibility(View.GONE);
        boolean cancel = false;
        View view = null;

        if (driver == null && TextUtils.isEmpty(profileImage)) {
            tvImageError.setTextColor(getResources().getColor(R.color.colorRed));
            tvImageError.setVisibility(View.VISIBLE);
            cancel = true;
        }

        if (driver == null && TextUtils.isEmpty(insuranceImage)) {
            tvUploadInsurance.setTextColor(getResources().getColor(R.color.colorRed));
            cancel = true;
        }
        if (driver == null && TextUtils.isEmpty(policeImage)) {
            tvUploadPolice.setTextColor(getResources().getColor(R.color.colorRed));
            cancel = true;
        }
        if (driver == null && TextUtils.isEmpty(rcImage)) {
            tvUploadRc.setTextColor(getResources().getColor(R.color.colorRed));
            cancel = true;
        }
        if (driver == null && TextUtils.isEmpty(drivingLicenseImage)) {
            tvUploadLicense.setTextColor(getResources().getColor(R.color.colorRed));
            cancel = true;
        }

        if (TextUtils.isEmpty(inputExpiry.getText().toString())) {
            inputExpiry.setError("Please select License Expiry Date");
            cancel = true;
            view = inputExpiry;
        }

        if (TextUtils.isEmpty(inputRegistrationNumber.getText().toString())) {
            inputRegistrationNumber.setError("Please select Vehicle Registration Number");
            cancel = true;
            view = inputRegistrationNumber;
        }

        if (TextUtils.isEmpty(inputModel.getText().toString())) {
            inputModel.setError("Please enter Vehicle Model (Ex. Tata Indica v2)");
            cancel = true;
            view = inputModel;
        }

        if (spinnerVehicleType.getSelectedItemPosition() <= 0) {
            spinnerVehicleType.setError("Please select vehicle type");
            cancel = true;
            view = spinnerVehicleType;
        } else {
            vehicleType = listVehicleTypes.get(spinnerVehicleType.getSelectedItemPosition() - 1).getId();
        }

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

        if (driver == null && TextUtils.isEmpty(profileImage)) {
            tvImageError.setVisibility(View.VISIBLE);
            cancel = true;
            view = tvImageError;
        } else {
            tvImageError.setVisibility(View.GONE);
        }

        if (driver != null) {
            int change = 0;
            if (!name.equals(driver.getName()))
                change = 1;
            if (!dob.equals(driver.getDob()))
                change = 2;
            if (!gender.equalsIgnoreCase(driver.getGender()))
                change = 3;
            if (vehicleType != vehicle.getVehicle_type())
                change = 4;
            if (!inputModel.getText().toString().equals(vehicle.getVehicle_model()))
                change = 5;
            if (!inputRegistrationNumber.getText().toString().equals(vehicle.getVehicle_number()))
                change = 6;
            if (!inputExpiry.getText().toString().equals(vehicle.getExpiry_date()))
                change = 7;

            if (!TextUtils.isEmpty(profileImage))
                change = 8;
            if (!TextUtils.isEmpty(drivingLicenseImage))
                change = 9;
            if (!TextUtils.isEmpty(insuranceImage))
                change = 10;
            if (!TextUtils.isEmpty(rcImage))
                change = 11;
            if (!TextUtils.isEmpty(policeImage))
                change = 12;

            if (change == 0) {
                cancel = true;
                showAlert(GetUserInfoActivity.this, "You have not changed anything.\nPlease make required changes & then submit.");
            } else {
                showAlert(GetUserInfoActivity.this, "change=" + change + ", selected vehicleType=" + vehicleType + ", vehicle_type saved=" + vehicle.getVehicle_type());
            }
        }

        if (cancel) {
            if (view != null)
                view.requestFocus();
            return false;
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private void saveUserInfo() {
        showDialog();
        myToken = FirebaseInstanceId.getInstance().getToken();
        FbDriver driver = new FbDriver();
        driver.setName(name);
        driver.setMobile(mobile);
        driver.setGender(gender.toLowerCase());
        driver.setToken(myToken);
        driver.setVehicle_type(vehicleType);
        driver.setModel(inputModel.getText().toString());

        FirebaseDatabase.getInstance().getReference(TABLE_DRIVERS).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(driver)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveToServer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideDialog();
                        showAlert(GetUserInfoActivity.this, 0, "Registration failed!", e.getMessage());
                    }
                });
    }

    private void saveToServer() {
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
        if (profileImage != null)
            param.put("image", profileImage);

        param.put("vehicle_type", String.valueOf(vehicleType));
        param.put("vehicle_model", inputModel.getText().toString());
        param.put("vehicle_number", inputRegistrationNumber.getText().toString().toUpperCase());

        param.put("expiry_date", inputExpiry.getText().toString());
        if (drivingLicenseImage != null)
            param.put("driving_license", drivingLicenseImage);
        if (rcImage != null)
            param.put("registration_card", rcImage);
        if (insuranceImage != null)
            param.put("insurance", insuranceImage);
        if (policeImage != null)
            param.put("police_verification", policeImage);

        final Map<String, String> header = new HashMap<String, String>();
        header.put("SECURE-API-KEY", ModuleConfig.DRIVER_API_KEY);

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();

            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String url = URL_CREATE_USER;
                if (driver != null)
                    url = URL_UPDATE_USER;
                String result = ServerUtilities.getServerResponse(getApplicationContext(), url, param, header);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                hideDialog();
                if (result != null) {
                    Log.d(TAG, "Register Response: " + result);
                    try {
                        JSONObject jObj = new JSONObject(result);
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
                            Driver driver = new Gson().fromJson(jObj.getJSONObject("data").toString(), Driver.class);
                            session.setValue(CURRENT_USER, jObj.getJSONObject("data").toString());
                            session.setValue(USER_VEHICLE, jObj.getJSONObject("vehicle").toString());
                            session.setValue("image", driver.getImage());
                            session.setValue("mobile", mobile);
                            session.setValue("name", name);
                            session.setValue("dob", dob);
                            session.setValue("gender", gender);
                            session.setValue("status", driver.getStatus());
                            session.setLogin(true);
                            if (driver.getStatus() == 1) {
                                Intent intent = new Intent(GetUserInfoActivity.this, DriverMap2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                Intent intent = new Intent(GetUserInfoActivity.this, AccountStatusActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finishAffinity();
                            }
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

    @OnClick({R.id.btnBack, R.id.btnSubmit, R.id.inputDob, R.id.imageView, R.id.viewUploadPhoto, R.id.viewUploadDrivingLicense, R.id.viewUploadRC, R.id.viewInsurance, R.id.viewPolice, R.id.inputExpiry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.inputDob:
                setBithDate(inputDob);
                break;
            case R.id.inputExpiry:
                setExpiryDate(inputExpiry);
                break;
            case R.id.btnBack:
                exitApp();
                break;
            case R.id.btnSubmit:
                if (attemptSubmit()) {
                    saveUserInfo();
                }
                break;
            case R.id.imageView:
            case R.id.viewUploadPhoto:
                selectPhoto(SELECT_PHOTO, false);
                break;
            case R.id.viewUploadDrivingLicense:
                selectPhoto(SELECT_DRIVING_LICENSE, true);
                break;
            case R.id.viewUploadRC:
                selectPhoto(SELECT_RC, true);
                break;
            case R.id.viewInsurance:
                selectPhoto(SELECT_INSURANCE, true);
                break;
            case R.id.viewPolice:
                selectPhoto(SELECT_POLICE, true);
                break;
        }
    }

    private void exitApp() {
        if (driver == null) {
            new AlertDialog.Builder(this).setTitle("Abort SignUp?")
                    .setMessage("Are you sure want to abort signup.")
                    .setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).create().show();
        } else {
            finish();
        }
    }

    private void selectPhoto(int selectPhoto, boolean freeSize) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            new PermissionManager(this, this).getCameraMediaPermissions();
        } else {
            Intent intent = new Intent(this, SelectPhotoActivity.class);
            if (freeSize)
                intent.putExtra(FREE_SIZE_IMAGE, true);
            startActivityForResult(intent, selectPhoto);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_PHOTO:
                    if (data.hasExtra("image")) {
                        byte[] byteArray = data.getByteArrayExtra("image");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        imageView.setImageBitmap(bitmap);
                        viewUploadPhoto.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        profileImage = bitmapToString(bitmap);
                    }
                    break;
                case SELECT_DRIVING_LICENSE:
                    if (data.hasExtra("image")) {
                        byte[] byteArray = data.getByteArrayExtra("image");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        imgDrivingLicense.setImageBitmap(bitmap);
                        imgDrivingLicense.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgDrivingLicense.setPadding(0, 0, 0, 0);
                        drivingLicenseImage = bitmapToString(bitmap);
                    }
                    break;
                case SELECT_RC:
                    if (data.hasExtra("image")) {
                        byte[] byteArray = data.getByteArrayExtra("image");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        imgRegistrationCard.setImageBitmap(bitmap);
                        imgRegistrationCard.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgRegistrationCard.setPadding(0, 0, 0, 0);
                        rcImage = bitmapToString(bitmap);

                    }
                    break;
                case SELECT_INSURANCE:
                    if (data.hasExtra("image")) {
                        byte[] byteArray = data.getByteArrayExtra("image");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        imgInsurance.setImageBitmap(bitmap);
                        imgInsurance.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgInsurance.setPadding(0, 0, 0, 0);
                        insuranceImage = bitmapToString(bitmap);
                    }
                    break;
                case SELECT_POLICE:
                    if (data.hasExtra("image")) {
                        byte[] byteArray = data.getByteArrayExtra("image");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        imgPolice.setImageBitmap(bitmap);
                        imgPolice.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgPolice.setPadding(0, 0, 0, 0);
                        policeImage = bitmapToString(bitmap);
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }
}
