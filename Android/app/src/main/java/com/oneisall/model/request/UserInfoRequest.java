package com.oneisall.model.request;

public class UserInfoRequest {
    private String username;

    public UserInfoRequest(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserInfoRequest{" +
                "username='" + username + '\'' +
                '}';
    }
}
