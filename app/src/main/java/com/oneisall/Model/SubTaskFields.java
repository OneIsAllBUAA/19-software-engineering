package com.oneisall.Model;

public class SubTaskFields {
    private String file;
    private int task;
    private String result;
    public String getFile(){
        return file;
    }
    public int getTask(){
        return task;
    }
    public String getResult(){
        return result;
    }
    @Override
    public String toString(){
        return "Fields{" +
                "file=" + file +
                ", task=" + task +
                ", result=" + result
                +"}";
    }
}
