package com.qmuiteam.qmuidemo.model.request;

import java.util.List;

public class SubmitTaskRequest {
    private String username;
    private int task_id;
    private List<String> answers;

    public SubmitTaskRequest(String username, int task_id, List<String> answers) {
        this.username = username;
        this.task_id = task_id;
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "SubmitTaskRequest{" +
                "username='" + username + '\'' +
                ", task_id=" + task_id +
                ", answers=" + answers +
                '}';
    }
}
