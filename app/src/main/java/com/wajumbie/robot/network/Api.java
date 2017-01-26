package com.wajumbie.robot.network;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wajumbie.robot.models.WeatherResponse;

import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static final String API_KEY = "dfd9b9000e82691974aad2bfeee6ce0f";

    public final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    private static Api instance;
    private Retrofit retrofit;
    private ApiService service;


    private Api() {

        //This interceptor will log all http traffic
        //and automatically add the api key to each request
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client =
                new OkHttpClient
                        .Builder()
                        .addInterceptor(interceptor)
                        .addInterceptor(
                                new Interceptor() {
                                    @Override
                                    public Response intercept(Chain chain) throws IOException {
                                        Request original = chain.request();
                                        Request request = original.newBuilder().url(original.url().toString() + "&APPID=" + API_KEY).build();
                                        return chain.proceed(request);
                                    }
                                }
                        )
                        .build();

        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(BASE_URL)
                .build();

        service = retrofit.create(ApiService.class);
    }

    private static Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }

    public static Observable<WeatherResponse> getWeatherForZip(int zip) {
        return getInstance()
                .service
                .getWeatherForZip(zip);
    }


}