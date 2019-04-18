package com.qmuiteam.qmuidemo.model.response;


public class Task {
    private String model;
    private int pk;
    private Fields fields;

    public String getModel() {
        return model;
    }

    public int getPk() {
        return pk;
    }

    public Fields getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "Task{" +
                "model='" + model + '\'' +
                ", pk=" + pk +
                ", fields=" + fields +
                '}';
    }
}