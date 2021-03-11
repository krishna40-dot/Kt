package com.mbsindia.rider.app;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.Driver;
import com.taxiappclone.common.model.User;
import com.taxiappclone.common.model.Vehicle;
import com.taxiappclone.common.model.VehicleDetailsResponse;
import com.mbsindia.rider.R;


import in.myinnos.customfontlibrary.TypefaceUtil;

import static com.taxiappclone.common.app.AppConfig.CURRENT_USER;
import static com.taxiappclone.common.app.AppConfig.USER;
import static com.taxiappclone.common.app.AppConfig.USER_VEHICLE;
import static com.taxiappclone.common.app.AppConfig.VEHICLE_DETAILS;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;
    private SessionManager session;

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "Montserrat", "fonts/Montserrat-Medium.otf");
        mInstance = this;

        // register to be informed of activities starting up
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {

                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

        });
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public SessionManager getSessionManager() {
        if (session == null) {
            session = new SessionManager(this);
        }
        return session;
    }

    public Driver getCurrentUser() {
        String userData = getSessionManager().getStringValue(CURRENT_USER);
        if (!TextUtils.isEmpty(userData)) {
            return new Gson().fromJson(userData, Driver.class);
        }
        return null;
    }

    public User getUser() {
        String userData = getSessionManager().getStringValue(USER);
        if (!TextUtils.isEmpty(userData)) {
            return new Gson().fromJson(userData, User.class);
        }
        return null;
    }

    public int getCurrentUserId() {
        Driver driver = getCurrentUser();
        return driver.getId();
    }

    public VehicleDetailsResponse getVehicleDetails() {
        String vehicleDetails = getSessionManager().getStringValue(VEHICLE_DETAILS);
        if (!TextUtils.isEmpty(vehicleDetails)) {
            return new Gson().fromJson(vehicleDetails, VehicleDetailsResponse.class);
        }
        return null;
    }
}