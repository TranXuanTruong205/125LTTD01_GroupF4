package com.dinerestaurant.app.data.remote.api;
import com.dinerestaurant.app.model.User;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserApi {

    @GET("api/user/me")
    Call<User> getProfile();

    @PUT("api/user/me")
    Call<Map<String, String>> updateProfile(
            @Body Map<String, Object> body
    );

    @Multipart
    @POST("api/user/avatar")
    Call<Map<String, String>> uploadAvatar(
            @Part MultipartBody.Part file
    );
}
