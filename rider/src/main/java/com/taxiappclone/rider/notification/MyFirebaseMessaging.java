package com.taxiappclone.rider.notification;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.rider.activity.HomeActivity;
import com.taxiappclone.rider.activity.RiderTrackingActivity;
import com.taxiappclone.rider.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import static com.taxiappclone.common.app.AppConfig.TABLE_RIDERS;
import static com.taxiappclone.common.app.Constants.DRIVER_ID;
import static com.taxiappclone.common.app.Constants.DRIVER_TOKEN;
import static com.taxiappclone.common.app.Constants.MSG_TYPE;
import static com.taxiappclone.common.app.Constants.ON_RIDE;
import static com.taxiappclone.common.app.Constants.RIDE_ACCEPTED;
import static com.taxiappclone.common.app.Constants.RIDE_CANCELLED_BY_DRIVER;
import static com.taxiappclone.common.app.Constants.RIDE_DRIVER_ARRIVED;
import static com.taxiappclone.common.app.Constants.RIDE_END_TIME;
import static com.taxiappclone.common.app.Constants.RIDE_FINISHED;
import static com.taxiappclone.common.app.Constants.RIDE_ID;
import static com.taxiappclone.common.app.Constants.RIDE_REJECTED;
import static com.taxiappclone.common.app.Constants.RIDE_STARTED;
import static com.taxiappclone.rider.app.CommonUtils.showToast;
import static com.taxiappclone.rider.notification.NotificationUtils.PUSH_NOTIFICATION;
import static com.taxiappclone.rider.notification.NotificationUtils.TYPE_NOTIFICATION;

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

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
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
        DatabaseReference user = db.getReference(TABLE_RIDERS);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            user.child(FirebaseAuth.getInstance().getUid()).child("token").setValue(refreshedToken);
        }
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        session = AppController.getInstance().getSessionManager();
        try {
            JSONObject data = new JSONObject(remoteMessage.getData());
            Log.d("MESSAGE RECEIVED", data.toString());
            msgType = data.getString(MSG_TYPE);

            if(msgType.equals(TYPE_NOTIFICATION)){
                handleDataMessage(data);
            }else{
                handleActionMessage(data,msgType);
            }
        }
        catch (NullPointerException exp)
        {
            exp.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleActionMessage(JSONObject data,String msgType) {
        try {
            String driverId = data.getString("user_id");
            String driverToken  = data.getString("token");
            String title="",message="";
            switch (msgType){
                case RIDE_ACCEPTED:
                    title = "Ride Accepted";
                    message = "Driver accepted your ride";
                    break;
                case RIDE_DRIVER_ARRIVED:
                    title = "Driver Arrived";
                    message = "Driver has arrived at your location";
                    break;
                case RIDE_REJECTED:
                    title = "Ride Rejected";
                    message = "Driver rejected your ride";
                    break;
                case RIDE_CANCELLED_BY_DRIVER:
                    title = "Ride Cancelled";
                    message = "Your ride is cancelled by driver";
                    break;
                case RIDE_STARTED:
                    title = "Ride Started";
                    message = "Your ride is started. Tap to see details";
                    break;
                case RIDE_FINISHED:
                    title = "Ride Ended";
                    message = "Your ride is ended. Tap to see details";
                    break;
                default:
                    if(data.has("title"))
                        title = data.getString("title");
                    if(data.has("message"))
                        message = data.getString("message");
                    break;
            }
//            LatLng location = new Gson().fromJson(data.getString("body"), LatLng.class);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                Log.d(TAG,"App is not in background");
                // app is in foreground, broadcast the push message
                Intent broadcastIntent = new Intent(PUSH_NOTIFICATION);
                broadcastIntent.putExtra("title",title);
                broadcastIntent.putExtra("message",message);
                broadcastIntent.putExtra("msg_type", msgType);
                broadcastIntent.putExtra("driver_id", driverId);
                broadcastIntent.putExtra("driver_token", driverToken);
                broadcastIntent.putExtra("ride_id", data.getString("ride_id"));
                if(msgType.equals(RIDE_ACCEPTED))
                    session.setValue(ON_RIDE,true);
                session.setValue(RIDE_ID,data.getString("ride_id"));
                session.setValue(DRIVER_ID,driverId);
                session.setValue(DRIVER_TOKEN,driverToken);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                session.setValue(RIDE_ID,data.getString("ride_id"));
                session.setValue(ON_RIDE,true);
                Log.d(TAG,"App is in background");
                // app is in background, show the notification in notification tray
                Intent resultIntent;
                resultIntent = new Intent(getApplicationContext(), RiderTrackingActivity.class);
                /*if(session.getBooleanValue(ON_RIDE))
                    resultIntent = new Intent(getApplicationContext(), RiderTrackingActivity.class);
                else
                    resultIntent = new Intent(getApplicationContext(), HomeActivity.class);*/
                resultIntent.putExtra("title",title);
                resultIntent.putExtra("message",message);
                resultIntent.putExtra("msg_type", msgType);
                resultIntent.putExtra("driver_id", driverId);
                resultIntent.putExtra("driver_token", driverToken);
                resultIntent.putExtra("ride_id", data.getString("ride_id"));

                showNotificationMessage(getApplicationContext(), title, message, "", resultIntent);
            }
        }catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void handleDataMessage(JSONObject data) {
        //Log.e(TAG, "push json: " + json.toString());

        String title = "";
        String message = "";
        String timestamp = "";
        String imageUrl = "";
        try {
            title = data.getString("title");
            message = data.getString("message");
            timestamp = data.getString("timestamp");
            imageUrl = "";
        }catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        }
        try{

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
                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
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
