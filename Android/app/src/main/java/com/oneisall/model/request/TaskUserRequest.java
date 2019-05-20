package com.oneisall.model.request;

public class TaskUserRequest {
    private String username;
    private int task_id;

    public TaskUserRequest(String username, int task_id) {
        this.username = username;
        this.task_id = task_id;
    }

    @Override
    public String toString() {
        return "UserInfoRequest{" +
                "username='" + username + '\'' +
                '}';
    }
}
