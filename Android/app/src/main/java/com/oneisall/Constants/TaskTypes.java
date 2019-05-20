package com.oneisall.constants;

import java.util.Arrays;
import java.util.List;

/** consistent with the web-server
 *  0:non
 *  1:choose single,
 *  2:choose multi,
 *  3:Q&A,
 *  4:label it
 */

// task_templates = ['', '图片', '视频', '音频']
// task_types = ['', '单选式', '多选式', '问答式', '标注式']
public class TaskTypes {
    private static final List<String> TEMPLATES = Arrays.asList("", "图片", "视频", "音频");
    private static final List<String> TYPES = Arrays.asList("", "单选式", "多选式", "问答式","标注式");
    public static final int TEMPLATES_NON = 0;
    public static final int TEMPLATES_PIC = 1;
    public static final int TEMPLATES_VIDEO = 2;
    public static final int TEMPLATES_AUDIO = 3;
    public static final int TYPES_NON = 0;
    public static final int TYPES_SINGLE = 1;
    public static final int TYPES_MULTI = 2;
    public static final int TYPES_QA = 3;
    public static final int TYPES_LABEL = 4;

    /*
        my task 新增
     */
    public static final int TASK_TO_BE_DONE = 0;
    public static final int TASK_TO_BE_CHECKED = 1;
    public static final int TASK_REJECTED = 2;
    public static final int TASK_DONE = 3;
    public static final int TASK_RELEASED = 0;
    public static final int TASK_INVITED_TO_CHECK = 1;
    public static final int TASK_FAVORITE = 2;

    public static String getTemplateName(int templateID){
        if(templateID >=TEMPLATES.size() || templateID <= 0)    return "无效模板";
        return TEMPLATES.get(templateID);
    }
    public static String getTypeName(int typeID){
        if(typeID >=TYPES.size() || typeID <= 0)    return "无效类型";
        return TYPES.get(typeID);
    }
}
