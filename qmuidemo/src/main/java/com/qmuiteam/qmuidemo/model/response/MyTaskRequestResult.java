package com.qmuiteam.qmuidemo.model.response;

import java.util.List;

public class MyTaskRequestResult {
    private List<Task> favorite;
    private List<Task> grabbed;
    private List<Task> released;

    public List<Task> getFavorite() {
        return favorite;
    }

    public List<Task> getGrabbed() {
        return grabbed;
    }

    public List<Task> getReleased() {
        return released;
    }
}
