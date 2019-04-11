package com.oneisall.Utils;

import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {
    private static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "NetworkUtils";
    private static OkHttpClient httpClient;

    public static String post(String url, String json) {

        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response= httpClient.newCall(request).execute();
            if(response.isSuccessful() && null !=response.body()){
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static{
        httpClient = new OkHttpClient();
    }
}
