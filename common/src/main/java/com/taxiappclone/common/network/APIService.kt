package com.taxiappclone.common.network

import com.google.gson.JsonObject
import com.taxiappclone.common.model.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface APIService {

    @POST("api_rider/add_address")
    fun addAddress(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_rider/update_address")
    fun updateAddress(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_rider/get_user_address")
    fun getAddress(@Body requestBody: RequestBody): Call<RiderAddressResponse>

    @POST("api_rider/delete_address")
    fun deleteAddress(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_driver/add_address")
    fun addDriverAddress(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_driver/update_address")
    fun updateDriverAddress(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_driver/get_user_address")
    fun getDriverAddress(@Body requestBody: RequestBody): Call<RiderAddressResponse>

    @POST("api_driver/delete_address")
    fun deleteDriverAddress(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_common/get_country")
    fun getCountries(): Call<CountriesResponse>

    @POST("api_common/get_state")
    fun getStates(@Body requestBody: RequestBody): Call<StatesResponse>

    @POST("api_common/get_city")
    fun getCities(@Body requestBody: RequestBody): Call<CitiesResponse>

    @POST("api_rider/update_user")
    fun updateRider(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_common/pay")
    fun addMoney(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("api_driver/update_user")
    fun updateDriver(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_driver/add_rating_review_driver")
    fun rateDriver(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_driver/add_rating_review_rider")
    fun rateRider(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_driver/update_lat_lng")
    fun updateDriverLocation(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_common/send_android_notification")
    fun sendNotification(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api/v1/driver/login")
    fun loginDriver(@Body requestBody: RequestBody): Call<DriverLoginResponse>

    @POST("api_common/update_ride_detail")
    fun updateRideDetail(@Body requestBody: RequestBody): Call<JSONObject>

    @POST("api_driver/get_my_rides")
    fun getRidesDriver(@Body requestBody: RequestBody, @Header("Authorization") auth_token: String): Call<MyRidesResponse>

    @POST("api_rider/get_my_rides")
    fun getRidesRider(@Body requestBody: RequestBody, @Header("Authorization") auth_token: String): Call<MyRidesResponse>

    @POST("api_common/check_mobile_number")
    fun checkRegisteredUser(@Body requestBody: RequestBody): Call<JsonObject>

}