package com.example.brittlepins.api.service;

import com.example.brittlepins.api.model.Login;
import com.example.brittlepins.api.model.User;

import java.util.HashMap;

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
    //Call<ResponseBody> getBoards(@Header("X-CSRF-TOKEN") String authToken);
    Call<ResponseBody> getBoards();

    @POST("boards")
    Call<ResponseBody> createBoard(
            @Body HashMap<String, HashMap<String, String>> board,
            @Header("X-CSRF-TOKEN") String authToken
    );
}
