package com.oneisall.Model;

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
