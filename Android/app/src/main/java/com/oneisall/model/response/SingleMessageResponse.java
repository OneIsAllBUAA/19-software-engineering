package com.oneisall.model.response;

public class SingleMessageResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SingleMessageResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
