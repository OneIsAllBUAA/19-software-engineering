package com.qmuiteam.qmuidemo.api;

import com.google.gson.Gson;
import com.qmuiteam.qmuidemo.constants.UrlConstants;
import com.qmuiteam.qmuidemo.model.request.AllTasksRequest;
import com.qmuiteam.qmuidemo.model.request.EnterTaskRequest;
import com.qmuiteam.qmuidemo.model.request.FavoriteTaskRequest;
import com.qmuiteam.qmuidemo.model.response.EnterTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.TaskListResult;
import com.qmuiteam.qmuidemo.utils.NetworkUtils;

public class TaskApi {
    private static final String TAG = "TaskApi";

    public static TaskListResult getAllTasks(AllTasksRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.ALL_TASKS, queryString), TaskListResult.class);
    }

    public static TaskListResult getFavoriteTasks(FavoriteTaskRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.FAVORITE_TASKS, queryString), TaskListResult.class);
    }

    public static EnterTaskRequestResult enterTask(EnterTaskRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.ENTER_TASK, queryString), EnterTaskRequestResult.class);
    }


}