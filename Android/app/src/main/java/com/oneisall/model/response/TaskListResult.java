package com.oneisall.model.response;

import java.io.Serializable;
import java.util.List;

public class TaskListResult implements Serializable {
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
