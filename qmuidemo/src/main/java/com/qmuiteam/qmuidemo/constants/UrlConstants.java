package com.qmuiteam.qmuidemo.constants;

public class UrlConstants {
//    private static final String API_BASE  = "http://10.139.21.49:8000/api";    // Library
//    private static final String API_BASE  = "http://10.135.240.12:8000/api";    // dormitory
    public static final String WEBSITE_BASE = "http://10.135.28.137";
    private static final String API_BASE= WEBSITE_BASE + "/api";  //local
    public static final String ALL_TASKS = API_BASE + "/all_tasks";
    public static final String TASK_INFO = API_BASE + "/task_info";
    public static final String ENTER_TASK = API_BASE + "/enter_task";
    public static final String MY_TASK = API_BASE + "/my_task";

//    public static final String MEDIA_BASE = "http://10.139.21.49:8000/media/";
    public static final String MEDIA_BASE = WEBSITE_BASE + "/media/";
    public static final String POST_SUB_RESULT = API_BASE+"/post_subtask_result";
    public static final String FAVORITE_TASKS = API_BASE + "/favorite_tasks";

    // user
    public static final String LOGIN = API_BASE + "/login";
    public static final String LOGOUT = API_BASE + "/logout";
    public static final String USER_INFO = API_BASE + "/user_info";
}
