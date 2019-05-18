package com.example.brittlepins.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.brittlepins.R;
import com.example.brittlepins.helpers.ActivityWithMenu;
import com.example.brittlepins.helpers.AuthServices;
import com.google.gson.JsonParseException;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ActivityWithMenu {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences cookiePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE);
        cookiePreferences = getSharedPreferences("cookies", MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!AuthServices.loggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }

    public void showBoards(View view) {
        Intent intent = new Intent(this, BoardsActivity.class);
        startActivity(intent);
    }

    public void createBoard(View view) {
        try {
            String authToken = cookiePreferences.getString("csrf", "");

            HashMap<String, HashMap<String, String>> board = new HashMap<>();
            HashMap<String, String> boardData = new HashMap<>();
            boardData.put("name", "Mobile board");
            boardData.put("description", "Mobile board description");
            board.put("board", boardData);

            Call<ResponseBody> call = AuthServices.userClient.createBoard(board, authToken);
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
