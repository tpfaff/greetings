package com.wajumbie.robot.network;


import com.wajumbie.robot.models.WeatherResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Tyler on 12/20/2016.
 */

public interface ApiService {


    @GET("weather")
    Observable<WeatherResponse> getWeatherForZip(@Query("q") int zipcode);


}
