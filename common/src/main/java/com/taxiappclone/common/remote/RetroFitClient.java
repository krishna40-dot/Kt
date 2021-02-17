package com.taxiappclone.common.remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Mac on 1/7/2018.
 */
public class RetroFitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseURL)
    {
        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
