package com.brittlepins.brittlepins.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.brittlepins.brittlepins.R;
import com.brittlepins.brittlepins.api.model.Login;
import com.brittlepins.brittlepins.helpers.AuthServices;

public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameEditText = findViewById(R.id.usernameEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUsernameEditText.setText("");
        mPasswordEditText.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }

    public void goToSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void logIn(View view) {
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        Login login = new Login(username, password);
        AuthServices.logIn(this, login);
    }
}
