package com.taxiappclone.common.remote;

import com.taxiappclone.common.notification.model.FCMMessage;
import com.taxiappclone.common.notification.model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Accept:application/json",
            "Content-Type:application/json",
            "Authorization:key=AIzaSyDDK8QH8HcCAG-4W37muBJzbE7ug_9Y8Ec"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body FCMMessage body);
}