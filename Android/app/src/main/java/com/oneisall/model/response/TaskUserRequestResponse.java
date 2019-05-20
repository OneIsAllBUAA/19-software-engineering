package com.oneisall.model.response;

public class TaskUserRequestResponse {
    private int isFavorite;
    private int isGrab;
    private int status;
    private int num_worker;

    public int getIsFavorite() {
        return isFavorite;
    }

    public int getIsGrab() {
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

    @Override
    public String toString(){
        return "TaskUserRequestResponse:{ isFav: "+ isFavorite+", isGrab: "+isGrab+"}";
    }
}
