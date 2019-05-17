package com.example.brittlepins.helpers;

import android.content.SharedPreferences;

import com.example.brittlepins.BuildConfig;
import com.example.brittlepins.api.service.UserClient;
import com.example.brittlepins.ui.MainActivity;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
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
}
