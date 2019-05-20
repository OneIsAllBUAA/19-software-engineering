package com.oneisall.model.request;

public class FavoriteTaskRequest {
    private String username;
    private int task_id;

    public FavoriteTaskRequest(String username, int task_id) {
        this.username = username;
        this.task_id = task_id;
    }

    @Override
    public String toString() {
        return "FavoriteTaskRequest{" +
                "username='" + username + '\'' +
                "task_id=" + task_id +
                '}';
    }
}
