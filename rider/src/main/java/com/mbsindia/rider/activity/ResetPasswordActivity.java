package com.mbsindia.rider.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.utils.ServerUtilities;
import com.dd.CircularProgressButton;
import com.mbsindia.rider.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {
    private static String TAG = ResetPasswordActivity.class.getSimpleName();
    private CircularProgressButton btnSubmit;
    private TextView txtPassword;
    private TextView txtConfirmPassword;
    String phoneNo,password;
    int userId;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        session = new SessionManager(this);
        txtPassword = (TextView)findViewById(R.id.txt_password);
        txtConfirmPassword = (TextView)findViewById(R.id.txt_confrim_password);
        btnSubmit = (CircularProgressButton) findViewById(R.id.btn_submit);
        btnSubmit.setIndeterminateProgressMode(true);
        Bundle bundle = getIntent().getExtras();
        phoneNo = bundle.getString("phone_no");
        userId = bundle.getInt("user_id");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btnSignUp.setProgress(0);
                if(btnSubmit.getProgress()==100) {
                    //showToast("Registered Successfully");
                }
                if(btnSubmit.getProgress()==0) {
                    if (attemptSubmit()) {
                        resetPassword(phoneNo,password);
                        //register(fullName, email, password, phoneNo, "ABCDEFGHI");
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
        txtPassword.setError(null);
        txtConfirmPassword.setError(null);
        password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();
        // Store values at the time of the login attempt.
        boolean cancel = false;
        View focusView = null;
        if(TextUtils.isEmpty(password))
        {
            txtPassword.setError("Please Enter Password");
            focusView = txtPassword;
            cancel = true;
        }
        if(!password.equals(confirmPassword))
        {
            txtPassword.setError("Password not matching!");
            focusView = txtConfirmPassword;
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
    private void resetPassword(final String email, final String password)
    {
        btnSubmit.setProgress(50);
        final Map<String, String> param = new HashMap<String, String>();
        param.put("email", email);
        param.put("password", password);
        mRegisterTask = new AsyncTask<Void, Void, String>() {
            long startTime = System.currentTimeMillis();
            @Override
            protected String doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                String result = ServerUtilities.getServerResponse(getApplicationContext(), AppConfig.URL_RESET_PASS, param);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if(result!=null){
                    try {
                        JSONObject jObj = new JSONObject(result);
                        boolean status = jObj.getBoolean("status");
                        if (status) {
                            btnSubmit.setProgress(100);
                            Log.d(TAG,result);
                            JSONObject userDetails = jObj.getJSONObject("user_details");
                            int userId = userDetails.getInt("u_id");
                            String userPhone = userDetails.getString("u_phone_no");
                            session.setLogin(true);
                            session.setValue("u_id",userId);
                            session.setValue("phone_no",userPhone);
                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                            i.putExtra("phone_no",phoneNo);
                            i.putExtra("user_id",userId);
                            startActivity(i);
                            ResetPasswordActivity.this.finish();
                            // User successfully stored in MySQL
                            // Now store the user in sqlite
                        /*String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String mob_no = user.getString("mob_no");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        //db.addUser(name, mob_no, uid, created_at);

                        // Launch login activity
                        session.setLogin(true);
                        Intent intent = new Intent(getApplicationContext(),
                                MainActivity.class);
                        startActivity(intent);
                        finish();*/
                        } else {

                            // Error occurred in registration. Get the error
                            // message
                            String errorMsg = jObj.getString("message");
                            btnSubmit.setProgress(-1);
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
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
}
