package com.mbsindia.driver.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.taxiappclone.common.activity.CustomActivity;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.Driver;
import com.taxiappclone.common.utils.ServerUtilities;
import com.mbsindia.driver.R;
import com.mbsindia.driver.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.taxiappclone.common.app.AppConfig.CURRENT_USER;
import static com.taxiappclone.common.app.AppConfig.USER_VEHICLE;
import static com.mbsindia.driver.app.ModuleConfig.URL_GET_USER;

public class AccountStatusActivity extends CustomActivity {

    private static final String TAG = AccountStatusActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ActionBar actionBar;
    @BindView(R.id.imgStatus)
    ImageView imgStatus;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvMessage)
    TextView tvMessage;
    @BindView(R.id.btnResubmit)
    Button btnResubmit;
    @BindView(R.id.btnExit)
    Button btnExit;
    private AsyncTask<Void, Void, String> mRegisterTask;
    SessionManager session;
    private ProgressDialog pDialog;
    private Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_status);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.account_status);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.checking_status));
        session = AppController.getInstance().getSessionManager();

        driver = AppController.getInstance().getCurrentUser();
        handleStatus();
        getUserDetails();
    }

    private void handleStatus() {
        if (driver.getStatus() == -1) {
            btnExit.setVisibility(View.GONE);
            btnResubmit.setVisibility(View.VISIBLE);
            imgStatus.setImageResource(R.drawable.ic_warning_black_24dp);
            tvTitle.setText(getString(R.string.rejected));
            tvMessage.setText(Html.fromHtml(getResources().getString(R.string.msg_profile_rejected)+"<br><br><font color='red'>" + driver.getReason() + "</font>"));
        }else if (driver.getStatus() == -2) {
            btnExit.setVisibility(View.GONE);
            btnResubmit.setVisibility(View.VISIBLE);
            imgStatus.setImageResource(R.drawable.ic_warning_black_24dp);
            tvTitle.setText(getString(R.string.deleted));
            String message = getResources().getString(R.string.msg_account_deleted)+"<br><br><font color='red'>"+driver.getReason()+ "</font>";
            tvMessage.setText(Html.fromHtml(message));
            new AlertDialog.Builder(AccountStatusActivity.this).setTitle(getString(R.string.account_deleted)).setMessage(Html.fromHtml(message))
                    .setPositiveButton(getString(R.string.exit), (dialog, which) -> {
                        dialog.dismiss();
                        session.destroy();
                        session.setLogin(false);
                        Intent intent = new Intent(AccountStatusActivity.this,SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }).setCancelable(false).create().show();
        }
    }

    private void getUserDetails() {
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
                            driver = new Gson().fromJson(jObj.getJSONObject("data").toString(), Driver.class);
                            session.setValue(CURRENT_USER, jObj.getJSONObject("data").toString());
                            session.setValue(USER_VEHICLE,jObj.getJSONObject("vehicle").toString());
                            session.setValue("image", driver.getImage());
                            session.setValue("mobile", driver.getMobile());
                            session.setValue("name", driver.getName());
                            session.setValue("dob", driver.getDob());
                            session.setValue("gender", driver.getGender());
                            session.setValue("status", driver.getStatus());
                            if (driver.getStatus() == 1) {
                                new AlertDialog.Builder(AccountStatusActivity.this).setTitle("Account Activated")
                                        .setMessage("Your Account is activated.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(AccountStatusActivity.this, EnableLocationActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).create().show();
                            } else {
                                handleStatus();
                            }
                        } else {
                            session.setLogin(false);
                            Intent intent = new Intent(AccountStatusActivity.this, SignInActivity.class);
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

    @OnClick({R.id.btnResubmit, R.id.btnExit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnResubmit:
                startActivity(new Intent(this,GetUserInfoActivity.class));
                break;
            case R.id.btnExit:
                finish();
                break;
        }
    }
}