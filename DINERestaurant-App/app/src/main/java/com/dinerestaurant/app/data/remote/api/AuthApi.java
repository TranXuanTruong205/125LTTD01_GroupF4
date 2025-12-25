package com.dinerestaurant.app.data.remote.api;

import com.dinerestaurant.app.model.LoginRequest;
import com.dinerestaurant.app.model.LoginResponse;
import com.dinerestaurant.app.model.LoginVerifyRequest;
import com.dinerestaurant.app.model.RegisterRequest;
import com.dinerestaurant.app.model.RegisterVerifyRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface AuthApi {

    @POST("api/auth/register/request")
    Call<Map<String, Object>> registerRequest(@Body RegisterRequest request);
    @POST("api/auth/register/verify")
    Call<Map<String, Object>> verifyRegister(@Body RegisterVerifyRequest body);
    @POST("api/auth/login/request")
    Call<Map<String, Object>> loginRequestOtp(@Body LoginRequest request);
    @POST("api/auth/login/verify")
    Call<Map<String, Object>> verifyLoginOtp(@Body LoginVerifyRequest request);
    @POST("api/auth/login/google")
    Call<Map<String, Object>> loginGoogle(@Body Map<String, String> body);

}