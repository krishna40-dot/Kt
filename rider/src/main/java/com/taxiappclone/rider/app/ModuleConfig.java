package com.taxiappclone.rider.app;

import static com.taxiappclone.common.app.AppConfig.URL_MAIN;

public class ModuleConfig {
    public static final String RIDER_API_KEY = "gUkXn2r5u8x/A?D(G+KbPeShVmYq3s6v";
    public static String URL_UPLOAD_PATH = URL_MAIN + "uploads/";

    public static String URL_API = URL_MAIN + "api/v1/rider/";
    public static final String URL_LOGIN = URL_API + "login";
    public static final String URL_CREATE_USER = URL_API + "register";
    public static final String URL_GET_USER = URL_API + "get_user";
    public static final String URL_UPDATE_TOKEN = URL_API + "update_token";
    public static final String URL_GET_VEHICLE_TYPES = URL_API + "get_vehicle_types";
    public static final String URL_CREATE_RIDE_REQUEST = URL_API + "create_ride_request";
}
