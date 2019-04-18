package com.qmuiteam.qmuidemo.model.request;

public class FavoriteTaskRequest {
    private String username;

    public FavoriteTaskRequest(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "FavoriteTaskRequest{" +
                "username='" + username + '\'' +
                '}';
    }
}
