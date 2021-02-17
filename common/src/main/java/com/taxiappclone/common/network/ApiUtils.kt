package com.taxiappclone.common.network

import com.taxiappclone.common.app.AppConfig

object ApiUtils {
    val apiService: APIService
        get() = RetrofitClient.getClient(AppConfig.URL_MAIN)!!.create(APIService::class.java)
}