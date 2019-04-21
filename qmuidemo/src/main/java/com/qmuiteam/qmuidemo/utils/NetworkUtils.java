package com.qmuiteam.qmuidemo.utils;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {
    private static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final String TAG = "NetworkUtils";
    public static String post(String url, String json) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Log.i(TAG, "request: " + json+request.toString());
        try {
            Response response=okHttpClient.newCall(request).execute();
            Log.i(TAG, response.toString());
            if(response.isSuccessful()){
                String s = response.body().string();
                Log.i(TAG, "response: " + s);
                response.close();
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String url, FormBody.Builder params) {
        Request request = new Request.Builder()
                .url(url)
                .post(params.build())
                .build();
        Log.i(TAG, request.toString());
        try {
            Response response=okHttpClient.newCall(request).execute();
            Log.i(TAG, response.toString());
            if(response.isSuccessful()){
                String s = response.body().string();
                Log.i(TAG, "post: " + s);
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
