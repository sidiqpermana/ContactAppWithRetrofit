package com.nbs.ppm.contactapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nbs.ppm.contactapp.converter.GsonConverterFactory;
import com.nbs.ppm.contactapp.util.AppUrl;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.Retrofit;

/**
 * Created by Sidiq on 28/10/2015.
 */
public class ServiceGenerator {
    private ServiceGenerator(){}

    public static <S> S createService(Class<S> serviceClass){

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

        Retrofit builder = new Retrofit.Builder()
                .baseUrl(AppUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient).build();

        return builder.create(serviceClass);
    }
}
