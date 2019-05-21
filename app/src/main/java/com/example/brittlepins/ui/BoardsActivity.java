package com.example.brittlepins.ui;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.brittlepins.R;
import com.example.brittlepins.api.model.Board;
import com.example.brittlepins.helpers.ActivityWithMenu;
import com.example.brittlepins.helpers.AuthServices;
import com.example.brittlepins.helpers.BoardsViewAdapter;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardsActivity extends ActivityWithMenu {
    private ArrayList<Board> mBoards = new ArrayList<>();
    private RecyclerView mBoardsContainer;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards);

        mBoardsContainer = findViewById(R.id.boardsContainer);
        mAdapter = new BoardsViewAdapter(mBoards);
        mBoardsContainer.setAdapter(mAdapter);
        mManager = new LinearLayoutManager(this);
        mBoardsContainer.setLayoutManager(mManager);
        // Remove blinks on touch
        ((SimpleItemAnimator) mBoardsContainer.getItemAnimator()).setSupportsChangeAnimations(false);
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
                    try {
                        Gson gson = new Gson();
                        String responseString = response.body().string();
                        Board[] boards = gson.fromJson(responseString, Board[].class);
                        mBoards.addAll(Arrays.asList(boards));
                        mAdapter.notifyDataSetChanged();
                    } catch (IOException ex) {
                        showToast("Could not load boards.");
                        Log.e("Response to string", ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    showToast("Could not fetch boards.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Could not connect to server.");
                Log.e("Fetch boards - fail", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
