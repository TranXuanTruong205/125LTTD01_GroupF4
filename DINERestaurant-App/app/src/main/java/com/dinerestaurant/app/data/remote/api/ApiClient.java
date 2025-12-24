package com.dinerestaurant.app.data.remote.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    public static void init(Context context) {
        if (retrofit != null) return;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(logging)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static AuthApi getAuthApi() {
        return retrofit.create(AuthApi.class);
    }
    public static ApiNotification getNotificationApi() {
        return retrofit.create(ApiNotification.class);
    }
    public static PromotionApi getPromotionApi() {
        return retrofit.create(PromotionApi.class);
    }

}
