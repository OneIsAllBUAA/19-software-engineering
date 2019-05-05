package com.qmuiteam.qmuidemo.model.response;

public class Fields {
    private int type;
    private int template;
    private String content;
    private String name;
    private int admin;
    private String details;
    private String c_time;
    private int max_tagged_num;
    private boolean is_closed;
    private int credit;
    private int user_level;

    @Override
    public String toString() {
        return "Fields{" +
                "type=" + type +
                ", template=" + template +
                ", content='" + content + '\'' +
                ", name='" + name + '\'' +
                ", admin=" + admin +
                ", details='" + details + '\'' +
                ", c_time='" + c_time + '\'' +
                ", max_tagged_num=" + max_tagged_num +
                ", is_closed=" + is_closed +
                ", credit=" + credit +
                ", user_level=" + user_level +
                '}';
    }

    public int getType() {
        return type;
    }

    public int getTemplate() {
        return template;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public int getAdmin() {
        return admin;
    }

    public String getDetails() {
        return details;
    }

    public String getC_time() {
        return c_time;
    }

    public int getMax_tagged_num() {
        return max_tagged_num;
    }

    public boolean isIs_closed() {
        return is_closed;
    }

    public int getCredit() {
        return credit;
    }

    public int getUser_level() {
        return user_level;
    }
}