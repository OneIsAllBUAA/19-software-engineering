package com.oneisall.Api;


import android.util.Log;

import com.google.gson.Gson;
import com.oneisall.Constants.UrlConstants;
import com.oneisall.Model.SubTaskDetail;
import com.oneisall.Model.SubTaskResult;
import com.oneisall.Model.TaskRequest;
import com.oneisall.Utils.NetworkUtils;

public class TaskApi {
    private static final String TAG = "TaskApi";
    public static SubTaskDetail getTaskInfo(TaskRequest request){
        try{
            String queryString = new Gson().toJson(request);
            Log.i(TAG, queryString);
            Gson gson = new Gson();
//        Log.i(TAG, NetworkUtils.post(UrlConstants.TASK_INFO, queryString));
            String response = NetworkUtils.post(UrlConstants.TASK_INFO, queryString);
            Log.i(TAG, response);
            SubTaskDetail taskInfo = gson.fromJson(response, SubTaskDetail.class);
            if(taskInfo==null){
                Log.i(TAG, "transfer json failed");
                taskInfo = new SubTaskDetail();
                taskInfo.init();
            }
            return taskInfo;
        }catch (Exception e){
            return null;
        }
    }
    public static Boolean postSubTaskResult(SubTaskResult result){
        Log.i(TAG, result.toString());
        String queryString = new Gson().toJson(result);
        Log.i(TAG, UrlConstants.POST_SUB_RESULT+" "+queryString);
        if(NetworkUtils.post(UrlConstants.POST_SUB_RESULT, queryString)!=null){
            return true;
        }
        return false;
    }
}