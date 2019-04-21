package com.qmuiteam.qmuidemo.model.request;

public class LogoutRequest {
    private String uesrname;
    private String password;

    public LogoutRequest(String uesrname, String password) {
        this.uesrname = uesrname;
        this.password = password;
    }

    @Override
    public String toString() {
        return "LogoutRequest{" +
                "uesrname='" + uesrname + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
