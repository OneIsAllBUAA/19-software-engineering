package com.qmuiteam.qmuidemo.model.request;

public class CheckTaskRequest {
    private int task_id;

    public CheckTaskRequest(int task_id) {
        this.task_id = task_id;
    }

    @Override
    public String toString() {
        return "CheckTaskRequest{" +
                "task_id=" + task_id +
                '}';
    }

    public int getTask_id() {
        return task_id;
    }
}
