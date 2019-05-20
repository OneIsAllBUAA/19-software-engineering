package com.oneisall.model.request;

import java.util.List;

public class SubmitTaskRequest {
    private String username;
    private int task_id;
    private List<String> answer;

    public SubmitTaskRequest(String username, int task_id, List<String> answer) {
        this.username = username;
        this.task_id = task_id;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "SubmitTaskRequest{" +
                "username='" + username + '\'' +
                ", task_id=" + task_id +
                ", answer=" + answer +
                '}';
    }
}
