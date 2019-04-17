package com.oneisall.Model;

public class SubTaskDetail {
    private int seq;
    private SubTaskFields fields;
    private String model;
    private int pk;
    private int num;

    public String getModel() {
        return model;
    }

    public void init(){
        fields=new SubTaskFields();
        fields.setFile("");
    }
    public int getPk() {
        return pk;
    }
    public int getSeq(){
        return seq;
    }
    public int getNum(){
        return num;
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
