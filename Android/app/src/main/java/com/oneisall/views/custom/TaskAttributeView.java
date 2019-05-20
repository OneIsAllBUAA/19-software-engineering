package com.oneisall.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oneisall.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAttributeView extends LinearLayout {
    final private static String TAG = "TaskAttributeView";

    @BindView(R.id.task_attribute_name) TextView mAttrName;
    @BindView(R.id.task_attribute_text) TextView mAttrText;
    @BindView(R.id.task_attribute) LinearLayout mAttr;
    public TaskAttributeView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_task_attribute, this, true);
        ButterKnife.bind(this);
    }

    public TaskAttributeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskAttributeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAttrName(int id){
        mAttrName.setText(getResources().getString(id));
    }
    //特别针对人员要求属性的方法
    public void setmAttrText(int level){
        String text;
        if(level<=1) text = "均可参与";
        else text = level+"级及以上用户";
        mAttrText.setText(text);
    }
    public void setmAttrText(String text){
        if(text.equals("")) text="暂无";
        mAttrText.setText(text);
    }
    public void setMarginTop(boolean margin){
        if(margin){
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,10,0,0);
            mAttr.setLayoutParams(lp);
        }
    }
}
