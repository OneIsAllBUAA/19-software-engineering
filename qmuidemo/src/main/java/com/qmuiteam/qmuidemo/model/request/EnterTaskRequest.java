package com.qmuiteam.qmuidemo.model.request;

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

    public int getTask_id() {
        return task_id;
    }
}
