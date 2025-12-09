package com.dinerestaurant.app.data.repository;

import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.AuthApi;
import com.dinerestaurant.app.model.LoginRequest;
import com.dinerestaurant.app.model.LoginResponse;
import com.dinerestaurant.app.model.LoginVerifyRequest;
import com.dinerestaurant.app.model.RegisterRequest;
import com.dinerestaurant.app.model.RegisterVerifyRequest;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class AuthRepository {

    private final AuthApi authApi;

    public AuthRepository() {
        this.authApi = ApiClient.getAuthApi();
    }

    public Call<Map<String, Object>> registerRequest(RegisterRequest request) {
        return authApi.registerRequest(request);
    }

    public Call<Map<String, Object>> verifyRegister(RegisterVerifyRequest body) {
        return ApiClient.getAuthApi().verifyRegister(body);
    }

    public Call<Map<String, Object>> loginRequestOtp(LoginRequest request) {
        return authApi.loginRequestOtp(request);
    }
    public Call<Map<String, Object>> verifyLoginOtp(LoginVerifyRequest request) {
        return ApiClient.getAuthApi().verifyLoginOtp(request);
    }

}