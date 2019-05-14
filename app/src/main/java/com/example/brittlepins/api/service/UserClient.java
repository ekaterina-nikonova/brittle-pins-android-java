package com.example.brittlepins.api.service;

import com.example.brittlepins.api.model.Login;
import com.example.brittlepins.api.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserClient {
    @POST("signin")
    Call<User> login(@Body Login login);

    @GET("boards")
    Call<ResponseBody> getBoards(@Header("X-CSRF-TOKEN") String authToken);

}
