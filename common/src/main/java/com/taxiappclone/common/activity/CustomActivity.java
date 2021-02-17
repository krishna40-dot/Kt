package com.taxiappclone.common.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiappclone.common.app.CommonUtils;



public class CustomActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtils.setStatusBarGradient(this);
    }
}
