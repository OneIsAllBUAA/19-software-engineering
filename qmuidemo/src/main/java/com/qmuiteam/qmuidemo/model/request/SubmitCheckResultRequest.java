package com.qmuiteam.qmuidemo.model.request;

import java.util.List;

public class SubmitCheckResultRequest {
    private List<Integer> accept_list;
    private List<Integer> reject_list;
    public SubmitCheckResultRequest(List<Integer> accept_list, List<Integer> reject_list){
        this.accept_list=accept_list;
        this.reject_list = reject_list;
    }
}