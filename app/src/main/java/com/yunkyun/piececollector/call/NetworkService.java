package com.yunkyun.piececollector.call;

import com.yunkyun.piececollector.object.Place;
import com.yunkyun.piececollector.object.Record;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by YunKyun on 2017-08-02.
 */

public interface NetworkService {
    String serverURL = "http://128.199.209.152/";

    @POST("users/new")
    Call<okhttp3.ResponseBody> createUser(@Body HashMap<String, String> parameters);

    @GET("versions/{table}")
    Call<String> getVersion(@Path("table") String table);

    @GET("places")
    Call<List<Place>> getPlaces();

    @POST("records")
    Call<List<Record>> getRecords(@Body HashMap<String, String> parameters);

    @Multipart
    @POST("upload")
    retrofit2.Call<okhttp3.ResponseBody> postImage(@QueryMap HashMap<String, String> parameters, @Part MultipartBody.Part image);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(serverURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
