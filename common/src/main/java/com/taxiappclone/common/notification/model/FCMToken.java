package com.taxiappclone.common.notification.model;

/**
 * Created by Mac on 4/25/2018.
 */

public class FCMToken {
    private String token;

    public FCMToken() {
    }

    public FCMToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
