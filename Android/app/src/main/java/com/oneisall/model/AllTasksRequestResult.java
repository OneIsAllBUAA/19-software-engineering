package com.oneisall.model;

import java.util.List;

public class AllTasksRequestResult{
    private List<TaskDetail> resultArray;
    public List<TaskDetail> getResultArray() {
        return resultArray;
    }

    @Override
    public String toString() {
        return "AllTasksRequestResult{" +
                "resultArray=" + resultArray +
                '}';
    }
}
