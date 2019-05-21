package com.example.brittlepins.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.brittlepins.R;
import com.example.brittlepins.helpers.ActivityWithMenu;
import com.example.brittlepins.helpers.AuthServices;

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
}
