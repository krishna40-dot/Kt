package com.taxiappclone.common.notification;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.taxiappclone.common.R;
import com.taxiappclone.common.notification.model.FCMResponse;

import org.json.JSONObject;
import com.taxiappclone.common.utils.ServerUtilities;
import java.io.IOException;

public class SendPushNotification {
    private static final String TAG = SendPushNotification.class.getSimpleName();
    private OnResponseListener onResponseListener;
    private Context context;
    private AsyncTask<Void, Void, String> mRegisterTask;
    private JSONObject params;

    public SendPushNotification(Context context, JSONObject params, OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
        this.params = params;
        this.context = context;
        new SendMessageTask().execute();
    }

    class SendMessageTask extends AsyncTask<Void, Void, String> {

        long startTime = System.currentTimeMillis();
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String doInBackground(Void... voids) {
            startTime = System.currentTimeMillis();
            String result = ServerUtilities.sendFCMMessage(params);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG, "FCM Response: " + result.toString());
                FCMResponse response = new Gson().fromJson(result,FCMResponse.class);
                if(onResponseListener !=null) {
                    if (response.getSuccess() == 1)
                        onResponseListener.onSuccessListener();
                    else
                        onResponseListener.onFailedListener();
                }
            } else {
                if(onResponseListener !=null)
                    onResponseListener.onErrorListener();
            }
            //hideDialog();
            mRegisterTask = null;
        }
    }


    public interface OnResponseListener{
        public void onSuccessListener();
        public void onFailedListener();
        public void onErrorListener();
    }
}
