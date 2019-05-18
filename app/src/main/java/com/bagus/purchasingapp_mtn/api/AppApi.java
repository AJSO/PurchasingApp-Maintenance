package com.bagus.purchasingapp_mtn.api;


import com.bagus.purchasingapp_mtn.model.Report;
import com.bagus.purchasingapp_mtn.model.ApiResponse;
import com.bagus.purchasingapp_mtn.model.User;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppApi {
    @POST("maintenance/login.php")
    Call<User> login(@Body JsonObject jsonObject);

    @POST("maintenance/user_update.php")
    Call<ApiResponse> updateProfile(@Header("token") String token, @Body JsonObject jsonObject);

    @POST("maintenance/report_add.php")
    Call<ApiResponse> reportAdd(@Header("token") String token, @Body JsonObject jsonObject);

    @GET("maintenance/report_read.php")
    Call<Report> reportRead(@Header("token") String token, @Query("userSID") String userSID);

    @POST("maintenance/report_update_status.php")
    Call<ApiResponse> reportUpdateStatus(@Header("token") String token, @Body JsonObject jsonObject);
}
