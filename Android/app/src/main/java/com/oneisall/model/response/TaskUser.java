package com.oneisall.model.response;

public class TaskUser {
    private int task;   //task_id
    private boolean has_grabbed;
    private int num_label_rejected;
    private String status;
    private int user;   //user id
    private int id; //pk
    private int num_label_unreviewed;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public boolean isHas_grabbed() {
        return has_grabbed;
    }
    @Override
    public String toString(){
        return "status:"+status+", has_grabbed:"+has_grabbed;
    }
}
