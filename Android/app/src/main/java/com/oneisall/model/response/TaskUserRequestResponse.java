package com.oneisall.model.response;

import java.util.List;

public class TaskUserRequestResponse {
    //static
    // status: 0-未做过，1-待审核/退回，2-审核中，3-完成
    // grab: 0-未抢位，1-开启抢位，2-抢到位置,3-抢位失败
    public final static int GRAB_NO = 0;
    public final static int GRAB_GRABBING = 1;
    public final static int GRABBED_BY_OTHERS=2;
    public final static int GRAB_FAILED = 3;
    public final static int GRAB_SUCCESSFUL=4;
    //
    private int isFavorite;
    private int isGrab;
    private int status;
    private int num_worker;
    private TaskUser task_user;
    private List<String> zip;

    public int getIsFavorite() {
        return isFavorite;
    }

    public int getIsGrab() {
        if(task_user==null) return isGrab;
        String s = task_user.getStatus();
        if(s.equals("grabbing")) return GRAB_GRABBING;
        if(s.equals("grabbed")) return GRABBED_BY_OTHERS;
        if(task_user.isHas_grabbed()) return GRAB_SUCCESSFUL;
        return isGrab;
    }

    public int getStatus() {
        return status;
    }

    public int getNum_worker() {
        return num_worker;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setIsGrab(int isGrab) {
        this.isGrab = isGrab;
    }

    public List<String> getZip() {
        return zip;
    }


    @Override
    public String toString(){
        return "TaskUserRequestResponse:{ isFav: "+ isFavorite+", isGrab: "+isGrab+", task_user:"+task_user+", zip:"+zip+"}";
    }
}
