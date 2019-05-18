package com.example.brittlepins.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brittlepins.R;
import com.example.brittlepins.api.model.Login;
import com.example.brittlepins.api.model.Signup;
import com.example.brittlepins.api.model.User;
import com.example.brittlepins.helpers.AuthServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUsernameEditText = findViewById(R.id.signupUsernameEditText);
        mEmailEditText = findViewById(R.id.signupEmailEditText);
        mPasswordEditText = findViewById(R.id.signupPasswordEditText);
    }

    public void finishActivity(View view) {
        finish();
    }

    public void signUp(View view) {
        final String username = mUsernameEditText.getText().toString().trim();
        final String email = mEmailEditText.getText().toString().trim();
        final String password = mPasswordEditText.getText().toString().trim();

        Signup signup = new Signup(username, email, password);
        Call<User> call = AuthServices.userClient.signup(signup);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    AuthServices.logIn(SignupActivity.this, new Login(email, password));
                } else {
                    showToast("Could not sign up.");
                    Log.e("Sign up - response", "Could not sign up.");
                    // TODO: handle sign up errors when validations are implemented on back end.
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showToast("Failed to sign up.");
                Log.e("Sign up - failure", "Failed to sign up: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
