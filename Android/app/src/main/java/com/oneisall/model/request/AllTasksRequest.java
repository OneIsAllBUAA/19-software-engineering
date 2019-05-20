package com.oneisall.model.request;

public class AllTasksRequest {
    private String username;
    private String KeyWord;

    public AllTasksRequest(String username) {
        this.username = username;
        this.KeyWord = "";
    }
    public AllTasksRequest(String username, String key) {
        this.username = username;
        this.KeyWord = key;
    }
}
