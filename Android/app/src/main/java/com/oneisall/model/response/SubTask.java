package com.oneisall.model.response;

import java.io.Serializable;

public class SubTask implements Serializable {
    private int id;
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

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SubTaskFields{" +
                "id=" + id +
                ", file='" + file + '\'' +
                ", task=" + task +
                ", result='" + result + '\'' +
                '}';
    }
}
