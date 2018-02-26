package com.fortvision.minisites.network;

import com.fortvision.minisites.model.FVButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FVServerApiFactory {

    public static FVServerAPI create() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<FVButton>() {
                }.getType(), new FVButtonDeserializer())
                .create();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .cache(null)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fb.fortvision.com/fb/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(FVServerAPI.class);
    }
}
