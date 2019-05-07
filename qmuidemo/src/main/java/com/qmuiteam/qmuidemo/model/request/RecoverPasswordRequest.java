package com.qmuiteam.qmuidemo.model.request;

public class RecoverPasswordRequest {
    private String email;

    public RecoverPasswordRequest(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "RecoverPasswordRequest{" +
                "email='" + email + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }
}
