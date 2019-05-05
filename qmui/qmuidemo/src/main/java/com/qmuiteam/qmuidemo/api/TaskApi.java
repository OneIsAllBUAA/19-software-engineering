package com.qmuiteam.qmuidemo.api;

import android.util.Log;

import com.google.gson.Gson;
import com.qmuiteam.qmuidemo.constants.UrlConstants;
import com.qmuiteam.qmuidemo.model.request.AllTasksRequest;
import com.qmuiteam.qmuidemo.model.request.CheckTaskRequest;
import com.qmuiteam.qmuidemo.model.request.EnterTaskRequest;
import com.qmuiteam.qmuidemo.model.request.FavoriteTaskRequest;
import com.qmuiteam.qmuidemo.model.request.MyTaskRequest;
import com.qmuiteam.qmuidemo.model.request.SubmitCheckResultRequest;
import com.qmuiteam.qmuidemo.model.request.SubmitTaskRequest;
import com.qmuiteam.qmuidemo.model.request.TaskIdAndUsernameRequest;
import com.qmuiteam.qmuidemo.model.response.CheckTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.EnterTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.MyTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
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

    public static CheckTaskRequestResult checkTask(CheckTaskRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        Log.i(TAG, NetworkUtils.post(UrlConstants.CHECK_TASK, queryString));
        CheckTaskRequestResult tmp = gson.fromJson(NetworkUtils.post(UrlConstants.CHECK_TASK, queryString), CheckTaskRequestResult.class);
        Log.i(TAG, "get check info!"+tmp);
        return tmp;
    }

    public static MyTaskRequestResult getMyTask(MyTaskRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.MY_TASK, queryString), MyTaskRequestResult.class);
    }

    public static SingleMessageResponse submitTask(SubmitTaskRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.SUBMIT_TASK, queryString), SingleMessageResponse.class);
    }
    public static SingleMessageResponse submitCheckResult(SubmitCheckResultRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.SUBMIT_CHECK_RESULT, queryString), SingleMessageResponse.class);
    }

    public static SingleMessageResponse grabTask(TaskIdAndUsernameRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.GRAB_TASK, queryString), SingleMessageResponse.class);
    }

    public static SingleMessageResponse favoriteTask(TaskIdAndUsernameRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.FAVORITE_TASK, queryString), SingleMessageResponse.class);
    }


}