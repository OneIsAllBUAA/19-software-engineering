package com.oneisall.model.response;

import java.util.List;

public class MyTaskRequestResult {
    private List<Task> favorite;
    private List<Task> grabbed;
    private List<Task> released;
    private List<Task> rejected;
    private List<Task> unreviewed;
    private List<Task> invited;
    private List<Task> doing;
    private List<Task> done;


    public List<Task> getFavorite() {
        return favorite;
    }

    public List<Task> getGrabbed() {
        return grabbed;
    }

    public List<Task> getReleased() {
        return released;
    }

    public List<Task> getRejected() { return rejected; }

    public List<Task> getUnreviewed() { return unreviewed; }

    public List<Task> getInvited() { return invited; }

    public List<Task> getDoing(){ return doing; }
    public List<Task> getDone(){ return done; }
}