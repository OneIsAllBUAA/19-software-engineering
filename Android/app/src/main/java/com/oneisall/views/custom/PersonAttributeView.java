package com.oneisall.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oneisall.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonAttributeView extends RelativeLayout {
    final private static String TAG = "TaskAttributeView";

    @BindView(R.id.person_attribute_name) TextView mAttrName;
    @BindView(R.id.person_attribute_text) TextView mAttrText;
//    @BindView(R.id.task_attribute) LinearLayout mAttr;
    public PersonAttributeView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_person_attribute, this, true);
        ButterKnife.bind(this);
    }

    public PersonAttributeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PersonAttributeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //自定义方法
    public void setAttrName(int id){
        mAttrName.setText(getResources().getString(id));
    }
    public void setAttrText(String text){
        mAttrText.setText(text);
    }
}
