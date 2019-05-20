package com.oneisall.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oneisall.R;
import com.oneisall.model.response.Task;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.oneisall.constants.TaskTypes.TEMPLATES_PIC;
import static com.oneisall.constants.TaskTypes.TEMPLATES_VIDEO;
import static com.oneisall.constants.TaskTypes.TYPES_LABEL;
import static com.oneisall.constants.TaskTypes.TYPES_MULTI;
import static com.oneisall.constants.TaskTypes.TYPES_QA;
import static com.oneisall.constants.TaskTypes.TYPES_SINGLE;

public class ItemTaskView extends LinearLayout {
    final private static String TAG = "ItemTask";
    private Task task;
    @BindView(R.id.item_task_name) TextView mTaskName;
    @BindView(R.id.item_task_template_img) ImageView mTemplateImg;
    @BindView(R.id.item_task_capacity) TextView mTaskCapa;
    @BindView(R.id.item_task_credit) TextView mTaskCredit;
    @BindView(R.id.item_task_is_closed) ImageView mTaskClosedImg;
    @BindView(R.id.item_task_type_img) ImageView mTaskType;
    @BindView(R.id.item_task) LinearLayout mTask;
    @BindView(R.id.item_bottom_divider) TextView mDivider;
    public ItemTaskView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_each_task, this, true);
        ButterKnife.bind(this);
    }
    public ItemTaskView(Context context, Task task) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_each_task, this, true);
        ButterKnife.bind(this);
        //init
        this.task = task;
        setTaskName(task.getFields().getName());
        setTaskCapa(task.getFields().getNum_worker(),task.getFields().getMax_tagged_num());
        setTaskCredit(task.getFields().getCredit());
        setTaskType(task.getFields().getType());
        setTemplateImg();
        if(task.getFields().isIs_closed()) mTaskClosedImg.setVisibility(VISIBLE);
        else mTaskClosedImg.setVisibility(GONE);
    }

    public ItemTaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTaskName(String name){
        mTaskName.setText(name);
    }
    public void setTaskCapa(int num_worker, int max_tagged_num){
        int remain = max_tagged_num - num_worker;
        mTaskCapa.setText("剩余 "+remain+" 工位，共有"+max_tagged_num+"工位");
//        mTaskCapa.setText(remain+"/"+max_tagged_num);
    }
    public void setTaskCredit(int credit){
        mTaskCredit.setText(credit+"积分/任务");
    }
    public void setTaskType(int type){
        switch (type){
            case TYPES_SINGLE:{
                mTaskType.setBackgroundResource(R.mipmap.single_icon);
                break;
            }
            case TYPES_MULTI:{
                mTaskType.setBackgroundResource(R.mipmap.multi_icon);
                break;
            }
            case TYPES_QA:{
                mTaskType.setBackgroundResource(R.mipmap.qa_icon);
                break;
            }
            case TYPES_LABEL:{
                mTaskType.setBackgroundResource(R.mipmap.label_icon);
                break;
            }
            default:{
                mTaskType.setBackgroundResource(R.mipmap.error);
            }
        }
    }
    public void setOnClickListener(OnClickListener onClickListener){
        mTask.setOnClickListener(onClickListener);
    }
    private void setTemplateImg(){
        if(task.getFields().getTemplate()==TEMPLATES_PIC){
            mTemplateImg.setImageDrawable(getResources().getDrawable(R.mipmap.template_pic));
        }
        else if(task.getFields().getTemplate()==TEMPLATES_VIDEO){
            mTemplateImg.setImageDrawable(getResources().getDrawable(R.mipmap.template_video));
        }
        else{
            mTemplateImg.setImageDrawable(getResources().getDrawable(R.mipmap.template_audio));
        }
    }
    public void setBottomDividerVisibilty(int visibilty){
        mDivider.setVisibility(visibilty);
    }

    public Task getTask(){return task;}
}
