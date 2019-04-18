package com.qmuiteam.qmuidemo.model.response;

import java.util.List;

public class TaskListResult {
    private List<Task> resultArray;

    public List<Task> getResultArray() {
        return resultArray;
    }

    @Override
    public String toString() {
        return "TaskListResult{" +
                "resultArray=" + resultArray +
                '}';
    }
}
