package com.example.brittlepins.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.brittlepins.BuildConfig;
import com.example.brittlepins.R;
import com.example.brittlepins.api.service.UserClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    Retrofit retrofit = builder.build();
    UserClient userClient = retrofit.create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
