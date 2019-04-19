package com.oneisall.model;

public class SubTaskDetail {
    private String model;
    private int pk;
    private SubTaskFields fields;

    public String getModel() {
        return model;
    }

    public int getPk() {
        return pk;
    }
    public SubTaskFields getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "TaskDetail{" +
                "model='" + model + '\'' +
                ", pk=" + pk +
                ", fields=" + fields +
                '}';
    }
}
