package com.oneisall.tasks.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.oneisall.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemSingleTask extends LinearLayout{
    final private static String TAG = "ItemSingleTask";

    private String question;
    private List<String> ansList;
    private Context mContext;
    //答案,用户选中的button序号，从0计数，返回答案时从1计数
    private int mAns=-1;
    //
    @BindView(R.id.single_question_text) TextView mQuestion;
    @BindView(R.id.single_radio_group) RadioGroup mAnswerGroup;

    public ItemSingleTask(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_select_single, this, true);
        ButterKnife.bind(this);
        mContext = context;
        initUI();
    }
    public ItemSingleTask(Context context, String question, List<String> ansList) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_select_single, this, true);
        ButterKnife.bind(this);
        this.question = question;
        this.ansList = ansList;
        mContext = context;
        initUI();
    }

    public ItemSingleTask(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemSingleTask(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initUI(){
        if(ansList==null) return;
        mQuestion.setText(question);
        //动态添加选项
        for(int i=0; i<ansList.size(); i++){
            RadioButton radioButton = new RadioButton(mContext);
            //设置文字
            radioButton.setText(ansList.get(i));
            radioButton.setTextSize(14);
            radioButton.setTextColor(getResources().getColor(R.color.text_black));
            radioButton.setTag(i);
            //设置监听器
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        mAns = (int)radioButton.getTag();
                        Log.i(TAG, mAns+"");
                    }
                }
            });
            //将radioButton添加到radioGroup中
            mAnswerGroup.addView(radioButton);
        }
        //恢复用户输入现场
        if(mAns>0) ((RadioButton)mAnswerGroup.getChildAt(mAns)).setChecked(true);
    }

    public String getAns(){
        if(mAns<0)
            return "&";
        return "&"+(mAns+1); }
}
