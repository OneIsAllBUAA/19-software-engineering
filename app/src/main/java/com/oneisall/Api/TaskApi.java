package com.oneisall.Api;


import android.util.Log;

import com.google.gson.Gson;
import com.oneisall.Constants.UrlConstants;
import com.oneisall.Model.SubTaskResult;
import com.oneisall.Model.TaskInfo;
import com.oneisall.Model.TaskRequest;
import com.oneisall.Utils.NetworkUtils;

import java.net.URI;

public class TaskApi {
    private static final String TAG = "TaskApi";
    public static TaskInfo getTaskInfo(TaskRequest request){
        String queryString = new Gson().toJson(request);
        Log.i(TAG, queryString);
        Gson gson = new Gson();
//        Log.i(TAG, NetworkUtils.post(UrlConstants.TASK_INFO, queryString));
        return gson.fromJson(NetworkUtils.post(UrlConstants.TASK_INFO, queryString), TaskInfo.class);
    }
    public static Boolean postSubTaskResult(SubTaskResult result){
        Log.i(TAG, result.toString());
        String queryString = new Gson().toJson(result);
        Boolean suc = NetworkUtils.post(UrlConstants.POST_SUB_RESULT, queryString)!=null;
        Log.i(TAG, UrlConstants.POST_SUB_RESULT+" "+queryString);
        return suc;
    }
}