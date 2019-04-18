package com.qmuiteam.qmuidemo.utils;

import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {
    private static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "NetworkUtils";
    public static String post(String url, String json) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Log.i(TAG, json+"---"+request.toString());
        Log.i(TAG, requestBody.contentType()+"");
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
