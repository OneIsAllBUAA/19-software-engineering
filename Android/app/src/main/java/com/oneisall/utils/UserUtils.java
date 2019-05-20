package com.oneisall.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserUtils {
    final private static String TAG = "UserUtils";
    private static final String USER_INFO = "USER_INFO";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String USER_LEVEL = "USER_LEVEL";

    public static String getUserName(Context context){
        Log.i(TAG, "context:"+context);
        return context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).getString(USERNAME, null);
    }

    public static String getPassword(Context context){
        return context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).getString(PASSWORD, null);
    }
    public static int getUserLevel(Context context){
        String level = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).getString(USER_LEVEL, null);
        return Integer.valueOf(level);
    }


    public static void setUserInfo(Context context, String username, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.apply();
    }
    public static void setUserLevel(Context context, int level){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
        editor.putString(USER_LEVEL, level+"");
        editor.apply();
    }

    public static void clearUserInfo(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

}
