package com.qmuiteam.qmuidemo.constants;

public class UrlConstants {
//    private static final String API_BASE  = "http://10.139.21.49:8000/api";    // Library
//    private static final String API_BASE  = "http://10.135.240.12:8000/api";    // dormitory
    public static final String WEBSITE_BASE = "http://114.115.181.247:8092";
    //public static final String WEBSITE_BASE = "http://10.23.169.52";
    private static final String API_BASE= WEBSITE_BASE + "/api";  //local
    public static final String ALL_TASKS = API_BASE + "/all_tasks";
    public static final String ENTER_TASK = API_BASE + "/enter_task";
    public static final String MY_TASK = API_BASE + "/my_task";
    public static final String SUBMIT_TASK = API_BASE + "/submit_task";

    public static final String GRAB_TASK = API_BASE + "/grab_task";
    public static final String FAVORITE_TASK = API_BASE + "/favorite_task";
    public static final String FAVORITE_TASKS = API_BASE + "/favorite_tasks";

    // user
    public static final String LOGIN = API_BASE + "/login";
    public static final String LOGOUT = API_BASE + "/logout";
    public static final String USER_INFO = API_BASE + "/user_info";
}
