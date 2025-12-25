package com.dinerestaurant.app.data.remote.api;

import com.dinerestaurant.app.model.UserAddress;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserAddressApi {

    // GET /api/user/addresses
    @GET("api/user/addresses")
    Call<List<UserAddress>> getMyAddresses();

    // GET /api/user/addresses/{id}
    @GET("api/user/addresses/{id}")
    Call<UserAddress> getAddressById(@Path("id") int id);

    // POST /api/user/addresses
    @POST("api/user/addresses")
    Call<UserAddress> addAddress(@Body UserAddress address);

    // PUT /api/user/addresses/{id}
    @PUT("api/user/addresses/{id}")
    Call<UserAddress> updateAddress(
            @Path("id") int id,
            @Body UserAddress address
    );

    // DELETE /api/user/addresses/{id}
    @DELETE("api/user/addresses/{id}")
    Call<Map<String, String>> deleteAddress(@Path("id") int id);

    // PUT /api/user/addresses/default/{id}
    @PUT("api/user/addresses/default/{id}")
    Call<Map<String, String>> setDefaultAddress(@Path("id") int id);
    @GET("api/user/addresses/default")
    Call<UserAddress> getDefaultAddress();

}
