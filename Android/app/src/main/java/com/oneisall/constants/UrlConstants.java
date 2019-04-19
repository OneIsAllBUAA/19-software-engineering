package com.oneisall.constants;

public class UrlConstants {
//    private static final String API_BASE  = "http://10.139.21.49:8000/api";    // Library
//    private static final String API_BASE  = "http://10.135.240.12:8000/api";    // dormitory
    private static final String BASE  = "http://192.168.24.25:8000";
    private static final String API_BASE= BASE + "/api";  //local
    public static final String ALL_TASKS = API_BASE + "/all_tasks";
    public static final String TASK_INFO = API_BASE + "/task_info";
    public static final String ENTER_TASK = API_BASE + "/enter_task";
//    public static final String MEDIA_BASE = "http://10.139.21.49:8000/media/";
    public static final String MEDIA_BASE = BASE + "/media/";
    public static final String POST_SUB_RESULT = API_BASE+"/post_subtask_result";
}
