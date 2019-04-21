package com.qmuiteam.qmuidemo.model.response;

public class UserInfoRequestResponse {
    private String username;
    private int user_id;
    private String email;
    private int total_credits;

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
