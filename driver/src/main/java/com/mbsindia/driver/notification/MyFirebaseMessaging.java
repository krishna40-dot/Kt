package com.mbsindia.driver.notification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mbsindia.driver.activity.DriverMap2Activity;
import com.mbsindia.driver.activity.DriverTrackingActivity;
import com.mbsindia.driver.activity.RiderCallActivity;
import com.mbsindia.driver.app.AppController;
import com.taxiappclone.common.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;


import static com.taxiappclone.common.app.AppConfig.TABLE_DRIVERS;
import static com.taxiappclone.common.app.Constants.MSG_TYPE;
import static com.taxiappclone.common.app.Constants.RIDER_ID;
import static com.taxiappclone.common.app.Constants.RIDER_TOKEN;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REQUEST;
import static com.mbsindia.driver.notification.NotificationUtils.PUSH_NOTIFICATION;
import static com.mbsindia.driver.notification.NotificationUtils.TYPE_NOTIFICATION;

/**
 * Created by Mac on 4/25/2018.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private static String TAG = MyFirebaseMessaging.class.getSimpleName();
    private NotificationUtils notificationUtils;
    private String msgType;
    private SessionManager session;

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        updateTokenToServer(token);
    }

    private void updateTokenToServer(String refreshedToken) {
        /*FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(TABLE_TOKENS);
        FCMToken token = new FCMToken(refreshedToken);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            tokens.child(FirebaseAuth.getInstance().getUid())
                    .setValue(token);
        }*/

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference user = db.getReference(TABLE_DRIVERS);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user.child(FirebaseAuth.getInstance().getUid()).child("token").setValue(refreshedToken);
        }

    }

    @SuppressLint("WrongConstant")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            JSONObject data = new JSONObject(remoteMessage.getData());
            Log.d("MESSAGE RECEIVED", data.toString());
            msgType = data.getString(MSG_TYPE);
            if (msgType.equals(TYPE_NOTIFICATION)) {
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData());
                    handleDataMessage(json);
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            } else if (msgType.equals(RIDE_REQUEST)) {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setClass(getBaseContext(), RiderCallActivity.class);
                intent.putExtra("totalFare", data.getString("totalFare"));
                intent.putExtra("paymentMode", data.getString("paymentMode"));
                intent.putExtra(RIDE_ID, data.getString(RIDE_ID));
                intent.putExtra(RIDER_ID, data.getString("user_id"));
                intent.putExtra(RIDER_TOKEN, data.getString("rider_token"));
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                startActivity(intent);
            } else {
                handleActionMessage(data, msgType);
            }
        } catch (NullPointerException exp) {
            exp.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleActionMessage(JSONObject data, String msgType) {
        try {
            session = AppController.getInstance().getSessionManager();
            String riderId = data.getString("user_id");
            String riderToken = data.getString("token");

            String title = data.getString("title");
            String message = data.getString("message");
//            LatLng location = new Gson().fromJson(data.getString("body"), LatLng.class);
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent broadcastIntent = new Intent(PUSH_NOTIFICATION);
                broadcastIntent.putExtra("title", title);
                broadcastIntent.putExtra("message", message);
                broadcastIntent.putExtra(MSG_TYPE, msgType);
                broadcastIntent.putExtra(RIDE_ID, data.getString(RIDE_ID));
                broadcastIntent.putExtra(RIDER_ID, riderId);
                broadcastIntent.putExtra(RIDER_TOKEN, riderToken);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), DriverTrackingActivity.class);
                if (data.has("paymentMode")) {
                    resultIntent.putExtra("paymentMode", data.getString("paymentMode"));
                }
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra(MSG_TYPE, msgType);
                resultIntent.putExtra(RIDE_ID, data.getString(RIDE_ID));
                resultIntent.putExtra(RIDER_ID, data.getString("user_id"));
                resultIntent.putExtra(RIDER_TOKEN, data.getString("token"));

                showNotificationMessage(getApplicationContext(), title, message, "", resultIntent);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject data) {
        //Log.e(TAG, "push json: " + json.toString());

        try {
            //JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            //boolean isBackground = data.getBoolean("is_background");
            //String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            //JSONObject payload = data.getJSONObject("payload");
            String imageUrl = "";

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), DriverMap2Activity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}