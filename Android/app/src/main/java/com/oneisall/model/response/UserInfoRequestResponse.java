package com.oneisall.model.response;

public class UserInfoRequestResponse {
    private String username;
    private int user_id;
    private String email;
    private int total_credits;
    private int num_label_accepted;
    private String last_login_time;

    public String getUsername() {
        return username;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public int getTotal_credits() {
        return total_credits;
    }

    public int getNum_label_accepted() {
        return num_label_accepted;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    @Override
    public String toString() {
        return "UserInfoRequestResponse{" +
                "username='" + username + '\'' +
                ", user_id=" + user_id +
                ", email='" + email + '\'' +
                ", total_credits=" + total_credits +
                '}';
    }
}
