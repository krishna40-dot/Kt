package com.mbsindia.rider.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.utils.ServerUtilities;
import com.dd.CircularProgressButton;
import com.mbsindia.rider.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassowrdActivity extends AppCompatActivity {
    private static String TAG = ForgotPassowrdActivity.class.getSimpleName();
    private EditText txtPhoneNo;
    private CircularProgressButton btnSubmit;
    private String phoneNo;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_passowrd);

        txtPhoneNo = (EditText)findViewById(R.id.txt_phone);
        btnSubmit = (CircularProgressButton)findViewById(R.id.btn_submit);
        btnSubmit.setIndeterminateProgressMode(true);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btnSignUp.setProgress(0);
                if(btnSubmit.getProgress()==100) {
                    //showToast("Registered Successfully");
                    btnSubmit.setProgress(0);
                }
                if(btnSubmit.getProgress()==0) {
                    if (attemptSubmit()) {
                        requestForgotPassword(phoneNo);
                    } else {
                        btnSubmit.setProgress(-1);
                    }
                }
                if(btnSubmit.getProgress()==-1) {
                    btnSubmit.setProgress(0);
                }
            }
        });
    }
    public Boolean attemptSubmit() {
        txtPhoneNo.setError(null);
        phoneNo = txtPhoneNo.getText().toString();

        // Store values at the time of the login attempt.
        boolean cancel = false;
        View focusView = null;
        if(TextUtils.isEmpty(phoneNo)||phoneNo.length()<10||phoneNo.length()>10)
        {
            txtPhoneNo.setError("Enter a valid Mobile Number");
            focusView = txtPhoneNo;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            focusView.requestFocus();
            return false;
        } else {
            return true;
            //checkLogin(email, password);

        }
    }
    private void requestForgotPassword(final String phoneNo)
    {
        btnSubmit.setProgress(50);
        final Map<String, String> param = new HashMap<String, String>();
        param.put("mobile_no", phoneNo);
        param.put("action", "pin");

        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();
            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), AppConfig.URL_SERVER, param);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if(result!=null){
                    try {
                        JSONObject jObj = new JSONObject(result);
                        int status = jObj.getInt("status_code");
                        switch (status)
                        {
                            case 0:
                                btnSubmit.setProgress(0);
                                showAlert(false,"Invalid Mobile Number","Please enter a registered mobile number.");
                                break;
                            case 1:
                                btnSubmit.setProgress(100);
                                showAlert(true,"Success","We have sent your pin at your registered mobile number.");

                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        btnSubmit.setProgress(-1);
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    btnSubmit.setProgress(0);
//					c.setStatus(Conversation.STATUS_FAILED);
//					db.changeMsgStatus(c.getMsgId(), Conversation.STATUS_FAILED);
                }
//				adp.notifyDataSetChanged();
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    private void showAlert(final boolean status, String title, String msg)
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
                if(status)
                {
                    startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                    finish();
                }
            }
        });
        alertDialog = builder.create();
        // Showing Alert Message
        alertDialog.show();
    }
    private void hideAlert()
    {
        if(alertDialog.isShowing())
        {
            alertDialog.dismiss();
        }
    }
}
