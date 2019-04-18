package com.qmuiteam.qmuidemo.constants;

import java.util.ArrayList;
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
    public static final int NON = 0;
    public static final int SINGLE = 1;
    public static final int MULTI = 2;
    public static final int QA = 3;
    public static final int LABEL = 4;
    public static String getTemplateName(int templateID){
        if(templateID >=TEMPLATES.size() || templateID <= 0)    return "无效模板";
        return TEMPLATES.get(templateID);
    }
    public static String getTypeName(int typeID){
        if(typeID >=TYPES.size() || typeID <= 0)    return "无效类型";
        return TYPES.get(typeID);
    }
}
