package com.oneisall.model.request;

public class TaskIdAndUsernameRequest {
    private int task_id;
    private String username;

    public TaskIdAndUsernameRequest(int task_id, String username) {
        this.task_id = task_id;
        this.username = username;
    }

    @Override
    public String toString() {
        return "TaskIdAndUsernameRequest{" +
                "task_id=" + task_id +
                ", username='" + username + '\'' +
                '}';
    }
}
