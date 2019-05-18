package com.example.brittlepins.helpers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.brittlepins.BuildConfig;
import com.example.brittlepins.api.model.Login;
import com.example.brittlepins.api.model.User;
import com.example.brittlepins.api.service.UserClient;
import com.example.brittlepins.ui.LoginActivity;
import com.example.brittlepins.ui.MainActivity;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthServices {
    private static ClearableCookieJar jar;

    public static UserClient userClient = buildClient();

    public static boolean loggedIn() {
        SharedPreferences prefs = MainActivity.cookiePreferences;
        String url = MainActivity.sharedPreferences.getString("host", "")
                + "/api/v1|jwt_access";

        Boolean cookieInPrefs = ! prefs.getString(url, "").isEmpty();
        Boolean csrfInPrefs = ! prefs.getString("csrf", "").isEmpty();
        return cookieInPrefs && csrfInPrefs;
    }

    public static void resetApp() {
        jar.clear();
    }

    public static void logIn(Activity a, Login login) {
        final Activity activity = a;
        Call<User> call = AuthServices.userClient.login(login);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    showToast(activity,"Successfully logged in");

                    String token = response.body().getCSRF();
                    MainActivity.cookiePreferences.edit().putString("csrf", token).apply();
                    showToast(activity, token);

                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                } else {
                    if (response.raw().code() == 401) {
                        AuthServices.resetApp();
                        showToast(activity,"Invalid username or password");
                    } else {
                        AuthServices.resetApp();
                        showToast(activity,"Something went wrong.");
                        Log.e("Log in response", response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                AuthServices.resetApp();
                showToast(activity,"Could not log in.");
                Log.e("Log in failure", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public static void logOut(Activity activity) {
        AuthServices.resetApp();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    private

    static UserClient buildClient() {
        jar  = new PersistentCookieJar(
                new SetCookieCache(),
                new SharedPrefsCookiePersistor(MainActivity.cookiePreferences)
        );

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(jar)
                .build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL + "/api/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = retrofitBuilder.build();

        // For checking cookie presence in loggedIn()
        HttpUrl baseUrl = retrofit.baseUrl();
        String host = baseUrl.scheme() + "://" + baseUrl.host();
        MainActivity.sharedPreferences.edit().putString("host", host).apply();

        UserClient userClient = retrofit.create(UserClient.class);

        return userClient;
    }

    static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
