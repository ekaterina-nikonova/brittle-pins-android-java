package com.brittlepins.brittlepins.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.brittlepins.brittlepins.R;

public class ComponentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_components);
    }

    public void addComponent(View view) {
        Intent intent = new Intent(this, AddComponentActivity.class);
        startActivity(intent);
    }
}
