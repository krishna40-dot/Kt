package com.taxiappclone.common.notification.model;

public class Data {
    public String type, title, message, body,timestamp,user_id,token,ride_id;

    public Data(){}

    public Data(String type, String user_id, String token) {
        this.type = type;
        this.user_id = user_id;
        this.token = token;
    }

    public Data(String type, String body, String timestamp, String user_id, String token) {
        this.type = type;
        this.body = body;
        this.user_id = user_id;
        this.timestamp = timestamp;
        this.token = token;
    }

    public Data(String type, String title, String message, String body, String timestamp, String user_id, String token) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.body = body;
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.token = token;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}