package com.oneisall.Api;

import com.google.gson.Gson;
import com.oneisall.Constants.UrlConstants;
import com.oneisall.Model.AllTasksRequest;
import com.oneisall.Model.AllTasksRequestResult;
import com.oneisall.Utils.NetworkUtils;

public class TaskApi {
    private static final String TAG = "TaskApi";
    public static AllTasksRequestResult getAllTasks(AllTasksRequest request){
        Gson queryGson = new Gson();
        String queryString = queryGson.toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.ALL_TASKS, queryString), AllTasksRequestResult.class);
    }
}
