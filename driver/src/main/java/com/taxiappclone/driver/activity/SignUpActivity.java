package com.taxiappclone.driver.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.taxiappclone.common.activity.CustomActivity;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.Driver;
import com.taxiappclone.driver.R;
import com.rengwuxian.materialedittext.MaterialEditText;



import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS;
import static com.taxiappclone.common.app.CommonUtils.setStatusBarGradient;

public class SignUpActivity extends CustomActivity implements View.OnClickListener{
    private static String TAG = SignUpActivity.class.getSimpleName();
    private MaterialEditText txtName, txtMobile, txtEmail, txtPassword;

    private Button btnSignIn,btnSignUp;
    private ProgressDialog pDialog;
    private SessionManager session;

    private String name="", email = "", password = "", mobile = "";
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference users;
    private String myToken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setStatusBarGradient(this);
        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(SignUpActivity.this, DriverMap2Activity.class);
            startActivity(intent);
            finish();
        }

        txtName = findViewById(R.id.input_name);
        txtMobile = findViewById(R.id.input_mobile);
        txtEmail = findViewById(R.id.input_email);
        txtPassword = findViewById(R.id.input_password);
        btnSignUp = findViewById(R.id.btn_signup);
        btnSignIn = findViewById(R.id.btn_signin);

        btnSignUp.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference(TABLE_DRIVERS);

        myToken = FirebaseInstanceId.getInstance().getToken();

    }

    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId())
        {
            case R.id.btn_signin:
                i = new Intent(getApplicationContext(),SignInActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.btn_signup:
                if (attemptSignUp()) {
                    register();
                }

                break;
        }
    }

    private void register() {
        pDialog.setMessage("Registering...");
        pDialog.setCancelable(false);
        showDialog();
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //save user to db
                Driver driver = new Driver();
                driver.setName(name);
                driver.setMobile(mobile);
                driver.setToken(myToken);

                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(driver)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast("Register Successfully!! Please Login.");
                                hideDialog();
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideDialog();
                                showAlert(false, "Registration failed!",e.getMessage());
                            }
                        });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideDialog();
                showAlert(false, "Registration failed!",e.getMessage());
            }
        });
    }

    public Boolean attemptSignUp() {
        txtName.setError(null);
        txtMobile.setError(null);
        txtEmail.setError(null);
        txtPassword.setError(null);


        name = txtName.getText().toString();
        mobile = txtMobile.getText().toString();
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();

        // Store values at the time of the login attempt.
        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            txtPassword.setError(getString(R.string.error_invalid_password));
            focusView = txtPassword;
            cancel = true;
        }

        if(TextUtils.isEmpty(password))
        {
            txtPassword.setError("Please enter a password.");
            focusView = txtPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Please enter your email.");
            focusView = txtEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            txtEmail.setError("Please enter a valid email.");
            focusView = txtEmail;
            cancel = true;
        }

        if(TextUtils.isEmpty(mobile))
        {
            txtMobile.setError("Please enter Phone no.");
            focusView = txtMobile;
            cancel = true;
        }
        if(TextUtils.isEmpty(name))
        {
            txtName.setError("Please enter your Name!");
            focusView = txtName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
