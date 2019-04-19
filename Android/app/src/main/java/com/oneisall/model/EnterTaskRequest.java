package com.oneisall.model;

public class EnterTaskRequest {
    private int task_id;

    public EnterTaskRequest(int task_id) {
        this.task_id = task_id;
    }

    @Override
    public String toString() {
        return "EnterTaskRequest{" +
                "task_id=" + task_id +
                '}';
    }
}
