package com.qmuiteam.qmuidemo.api;

import com.google.gson.Gson;
import com.qmuiteam.qmuidemo.constants.UrlConstants;
import com.qmuiteam.qmuidemo.model.request.LoginRequest;
import com.qmuiteam.qmuidemo.model.request.LogoutRequest;
import com.qmuiteam.qmuidemo.model.request.RecoverPasswordRequest;
import com.qmuiteam.qmuidemo.model.request.ResetPasswordRequest;
import com.qmuiteam.qmuidemo.model.request.SignUpRequest;
import com.qmuiteam.qmuidemo.model.request.UserInfoRequest;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
import com.qmuiteam.qmuidemo.model.response.UserInfoRequestResponse;
import com.qmuiteam.qmuidemo.utils.NetworkUtils;

public class UserApi {
    private static final String TAG = "UserApi";
    public static SingleMessageResponse login(LoginRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.LOGIN, queryString), SingleMessageResponse.class);
    }
    public static SingleMessageResponse logout(LogoutRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.LOGOUT, queryString), SingleMessageResponse.class);
    }

    public static SingleMessageResponse signUp(SignUpRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.SIGN_UP, queryString), SingleMessageResponse.class);
    }

    public static UserInfoRequestResponse getUserInfo(UserInfoRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.USER_INFO, queryString), UserInfoRequestResponse.class);
    }

    public static SingleMessageResponse recoverPassword(RecoverPasswordRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.RECOVER_PASSWORD, queryString), SingleMessageResponse.class);
    }

    public static SingleMessageResponse resetPassword(ResetPasswordRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.RESET_PASSWORD, queryString), SingleMessageResponse.class);
    }
}
