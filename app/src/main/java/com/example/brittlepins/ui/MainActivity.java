package com.example.brittlepins.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.brittlepins.BuildConfig;
import com.example.brittlepins.R;
import com.example.brittlepins.api.model.Login;
import com.example.brittlepins.api.model.User;
import com.example.brittlepins.api.service.UserClient;
import com.google.gson.JsonParseException;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;
    private OkHttpClient.Builder mClientBuilder;
    private Retrofit.Builder mRetrofitBuilder;
    private Retrofit mRetrofit;
    private UserClient mUserClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        mClientBuilder = new OkHttpClient.Builder();
        mClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder newRequestBuilder = request.newBuilder().header("Cookie", "");
                return chain.proceed(newRequestBuilder.build());
            }
        }).cookieJar(new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        });

        mRetrofitBuilder = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL + "/api/v1/")
                .client(mClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create());
        mRetrofit = mRetrofitBuilder.build();
        mUserClient = mRetrofit.create(UserClient.class);
    }

    public void logIn(View view) {
        Login login = new Login("john@doe.com", "johndoe123");
        Call<User> call = mUserClient.login(login);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    showToast("Successfully logged in");

                    String token = response.body().getCSRF();
                    showToast(token);
                    mSharedPreferences.edit().putString("csrf", token).apply();
                } else {
                    showToast("Wrong response.");
                    Log.e("Log in response", response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showToast("Could not log in.");
                Log.e("Log in failure", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void showBoards(View view) {
        Call<ResponseBody> call = mUserClient.getBoards();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    showToast("Boards successfully fetched");
                    try {
                        Log.i("--*-- Response --*--", response.body().string());
                    } catch (IOException ex) {
                        Log.e("Response to string", ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    showToast("Could not fetch boards.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void createBoard(View view) {
        try {
            String authToken = mSharedPreferences.getString("csrf", "");

            HashMap<String, HashMap<String, String>> board = new HashMap<>();
            HashMap<String, String> boardData = new HashMap<>();
            boardData.put("name", "Mobile board");
            boardData.put("description", "Mobile board description");
            board.put("board", boardData);

            Call<ResponseBody> call = mUserClient.createBoard(board, authToken);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.i("Create board", "Successfully created");
                        showToast("Board created");
                    } else {
                        Log.e("Create board", "Could not create");
                        showToast("Could not create board");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Create board - fail", "Failed to create board: " + t.getMessage());
                    t.printStackTrace();
                    showToast("Failed to create board.");
                }
            });
        } catch (JsonParseException ex) {
            Log.e("Board JSON", "Could not create board: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    private

    void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
