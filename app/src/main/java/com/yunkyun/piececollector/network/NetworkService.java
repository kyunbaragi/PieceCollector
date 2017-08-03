package com.yunkyun.piececollector.network;

import com.yunkyun.piececollector.object.Place;
import com.yunkyun.piececollector.object.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by YunKyun on 2017-08-02.
 */

public interface NetworkService {
    String serverURL = "http://128.199.209.152/";

    @POST("users")
    Call<User> addUser(@Body User user);

    @GET("versions/{table}")
    Call<String> getVersion(@Path("table") String table);

    @GET("places")
    Call<List<Place>> getPlaces();

    @GET("places/{areaCode}")
    Call<List<Place>> getPlacesByArea(@Path("areaCode") String areaCode);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(serverURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
