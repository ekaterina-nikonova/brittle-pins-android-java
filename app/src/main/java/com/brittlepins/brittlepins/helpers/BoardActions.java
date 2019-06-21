package com.brittlepins.brittlepins.helpers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.brittlepins.brittlepins.ui.MainActivity;
import com.google.gson.JsonParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardActions {
    public static void create(Activity a, HashMap<String, String> data) {
        final Activity activity = a;
        SharedPreferences cookiePreferences = MainActivity.cookiePreferences;
        try {
            String authToken = cookiePreferences.getString("csrf", "");

            data.put("name", "Board " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            data.put("description", "Created in " + activity.getPackageName());

            HashMap<String, HashMap<String, String>> board = new HashMap<>();
            board.put("board", data);

            Call<ResponseBody> call = AuthServices.userClient.createBoard(board, authToken);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.i("Create board", "Successfully created");
                        showToast(activity,"Board created");
                    } else {
                        Log.e("Create board", "Could not create");
                        showToast(activity, "Could not create board");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Create board - fail", "Failed to create board: " + t.getMessage());
                    t.printStackTrace();
                    showToast(activity, "Failed to create board.");
                }
            });
        } catch (JsonParseException ex) {
            Log.e("Board JSON", "Could not create board: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private

    static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

}
