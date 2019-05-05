package com.qmuiteam.qmuidemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserUtils {

    private static final String USER_INFO = "USER_INFO";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    public static String getUserName(Context context){
        return context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).getString(USERNAME, null);
    }

    public static String getPassword(Context context){
        return context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).getString(PASSWORD, null);
    }


    public static void setUserInfo(Context context, String username, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.apply();
    }

    public static void clearUserInfo(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

}
