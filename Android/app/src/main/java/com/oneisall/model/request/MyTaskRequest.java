package com.oneisall.model.request;
public class MyTaskRequest {
    private String username;

    public MyTaskRequest(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "MyTaskRequest{" +
                "username='" + username + '\'' +
                '}';
    }
}
