package com.taxiappclone.driver.activity;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import com.taxiappclone.common.activity.CustomActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.taxiappclone.driver.R;
import com.taxiappclone.common.helper.SessionManager;



public class BookingConfirmedActivity extends CustomActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TextView tvConfirmationNumber;
    private SessionManager session;
    private Button btnBookAnotherTrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmed);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Payment Information");

        session = new SessionManager(this);

        tvConfirmationNumber = findViewById(R.id.tv_confirmation_number);
        btnBookAnotherTrip = findViewById(R.id.btn_book_another_trip);

        tvConfirmationNumber.setText(getIntent().getStringExtra("confirmation_number"));

        btnBookAnotherTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookingConfirmedActivity.this, DriverMap2Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(this, DriverMap2Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DriverMap2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //super.onBackPressed();
    }
}
