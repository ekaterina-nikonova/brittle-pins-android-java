package com.example.brittlepins.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.brittlepins.R;
import com.example.brittlepins.helpers.AuthServices;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setTitle(R.string.boards_action_bar_header);
        fetchBoards();
    }

    private

    void fetchBoards() {
        Call<ResponseBody> call = AuthServices.userClient.getBoards();
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

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
