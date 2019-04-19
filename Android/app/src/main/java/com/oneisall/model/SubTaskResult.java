package com.oneisall.model;

public class SubTaskResult {
    private String user;
    private int task_id;
    private int pk; //sub-task id
    private String result;
    public SubTaskResult(String u,int tid, int id, String r){
        user = u;
        task_id = tid;
        this.pk = id;
        result = r;
    }
    @Override
    public String toString(){
        return "{user="+user+", task_id="+task_id+", pk="+pk+", result="+result+"}";
    }
}
