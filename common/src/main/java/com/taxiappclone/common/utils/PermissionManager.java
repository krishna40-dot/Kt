package com.taxiappclone.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.taxiappclone.common.R;
import com.taxiappclone.common.app.AppConfig;

import java.util.ArrayList;
import java.util.List;

import static com.taxiappclone.common.app.AppConfig.MULTIPLE_PERMISSION_REQUEST_CODE;

/**
 * Created by Mac on 5/2/2017.
 */
public class PermissionManager {
    private Context context;
    private Activity activity;

    public PermissionManager(Context context, Activity activity)
    {
        this.context = context;
        this.activity = activity;
    }
    public void getCameraMediaPermissions()
    {
        try {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            List<String> list = new ArrayList<String>();

            for (int i = 0; i < permissions.length; i++) {
                int hasPermission = ContextCompat.checkSelfPermission(activity, permissions[i]);
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    list.add(permissions[i]);
                }
            }
            final String[] listPermissionsNeeded = list.toArray(new String[0]);
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)||ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)||ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(context).setTitle("Grant Permissions").setMessage("Please Grant Permissions to select or capture a photo")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,listPermissionsNeeded,MULTIPLE_PERMISSION_REQUEST_CODE);
                            }
                        }).create().show();
            } else {
                try {
                    ActivityCompat.requestPermissions(activity, listPermissionsNeeded, MULTIPLE_PERMISSION_REQUEST_CODE);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    public void getDeviceIdPermission()
    {

    }



    public void getLocationPermission()
    {

        try {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            List<String> list = new ArrayList<String>();

            for (int i = 0; i < permissions.length; i++) {
                int hasPermission = ContextCompat.checkSelfPermission(activity, permissions[i]);
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    list.add(permissions[i]);
                }
            }
            String[] listPermissionsNeeded = list.toArray(new String[0]);
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                //Toast.makeText(context,"GPS permission allows us to access location data.
                // Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
            } else {
                try {
                    ActivityCompat.requestPermissions(activity, listPermissionsNeeded, MULTIPLE_PERMISSION_REQUEST_CODE);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }


}
