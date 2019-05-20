package com.oneisall.api;

import android.util.Log;

import com.google.gson.Gson;
import com.oneisall.constants.UrlConstants;
import com.oneisall.model.request.AllTasksRequest;
import com.oneisall.model.request.CheckTaskRequest;
import com.oneisall.model.request.EnterTaskRequest;
import com.oneisall.model.request.FavoriteTaskRequest;
import com.oneisall.model.request.MyTaskRequest;
import com.oneisall.model.request.SearchTaskRequest;
import com.oneisall.model.request.SubmitCheckResultRequest;
import com.oneisall.model.request.SubmitTaskRequest;
import com.oneisall.model.request.TaskIdAndUsernameRequest;
import com.oneisall.model.request.TaskUserRequest;
import com.oneisall.model.response.CheckTaskRequestResult;
import com.oneisall.model.response.EnterTaskRequestResult;
import com.oneisall.model.response.MyTaskRequestResult;
import com.oneisall.model.response.SingleMessageResponse;
import com.oneisall.model.response.TaskListResult;
import com.oneisall.model.response.TaskUserRequestResponse;
import com.oneisall.utils.NetworkUtils;

public class TaskApi {
    private static final String TAG = "TaskApi";

    public static TaskListResult getAllTasks(AllTasksRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.ALL_TASKS, queryString), TaskListResult.class);
    }

    public static TaskListResult getRecommendTasks(AllTasksRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.RECOMMEND_TASKS, queryString), TaskListResult.class);
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

    public static SingleMessageResponse grabTask(TaskIdAndUsernameRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.GRAB_TASK, queryString), SingleMessageResponse.class);
    }
    public static SingleMessageResponse undoGrab(TaskIdAndUsernameRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.UNDO_GRAB, queryString), SingleMessageResponse.class);
    }

    public static SingleMessageResponse favoriteTask(TaskIdAndUsernameRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.FAVORITE_TASK, queryString), SingleMessageResponse.class);
    }
    public static SingleMessageResponse undoFavorite(TaskIdAndUsernameRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.UNDO_FAVORITE, queryString), SingleMessageResponse.class);
    }

    public static CheckTaskRequestResult checkTask(CheckTaskRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        Log.i(TAG, NetworkUtils.post(UrlConstants.CHECK_TASK, queryString));
        CheckTaskRequestResult tmp = gson.fromJson(NetworkUtils.post(UrlConstants.CHECK_TASK, queryString), CheckTaskRequestResult.class);
        Log.i(TAG, "get check info!"+tmp);
        return tmp;
    }

    public static SingleMessageResponse submitCheckResult(SubmitCheckResultRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.SUBMIT_CHECK_RESULT, queryString), SingleMessageResponse.class);
    }

    public static TaskUserRequestResponse getTaskUser(TaskUserRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.GET_TASK_USER, queryString), TaskUserRequestResponse.class);
    }

    public static TaskListResult searchTask(SearchTaskRequest request){
        String queryString = new Gson().toJson(request);
        Gson gson = new Gson();
        return gson.fromJson(NetworkUtils.post(UrlConstants.SEARCH_TASK, queryString), TaskListResult.class);
    }
}