package com.taxiappclone.common.remote;

import com.taxiappclone.common.notification.model.FCMMessage;
import com.taxiappclone.common.notification.model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Mac on 1/7/2018.
 */
public class Common {
    public static final String URL_MAPS = "https://maps.googleapis.com";
    public static final String URL_FCM = "https://fcm.googleapis.com/";
    public static IGoogleAPI getGoogleAPI()
    {
        return RetroFitClient.getClient(URL_MAPS).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(URL_FCM).create(IFCMService.class);
    }
}
