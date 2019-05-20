package com.oneisall.tasks.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oneisall.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemMultiTask extends LinearLayout {
    final private static String TAG = "ItemMultiTask";

    private String question;
    private List<String> ansList;
    private Context mContext;
    private String mAns="";
    //
    @BindView(R.id.multi_question_text) TextView mQuestion;
    @BindView(R.id.multi_linear_layout) LinearLayout mAnswerGroup;

    public ItemMultiTask(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_select_single, this, true);
        ButterKnife.bind(this);
        mContext = context;
        initUI();
    }
    public ItemMultiTask(Context context, String question, List<String> ansList) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_select_multi, this, true);
        ButterKnife.bind(this);
        this.question = question;
        this.ansList = ansList;
        mContext = context;
        initUI();
    }

    public ItemMultiTask(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemMultiTask(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initUI(){
        mQuestion.setText(question);
        //动态添加选项
        for(int i=0; i<ansList.size(); i++){
            CheckBox checkBox = new CheckBox(mContext);
            checkBox.setText(ansList.get(i));
            checkBox.setTextSize(14);
            checkBox.setTextColor(getResources().getColor(R.color.text_black));
            //设置监听器
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    resetAns();
                }
            });
            mAnswerGroup.addView(checkBox);
        }
        //
        if(!mAns.equals("")){
            Log.i(TAG, mAns);
            String[] ms = mAns.split("&");
//            Log.i(TAG, ms.toString());
            for(int i=1; i<ms.length; i++){
//                Log.i(TAG, "ms["+i+"]="+ms[i]);
                ((CheckBox)mAnswerGroup.getChildAt(Integer.valueOf(ms[i])-1)).setChecked(true);
            }
        }
    }
    private void resetAns(){
        mAns ="";
        for(int i=0; i < mAnswerGroup.getChildCount(); i++){
            CheckBox cb = (CheckBox)mAnswerGroup.getChildAt(i);
            if(cb.isChecked()){
                mAns += "&"+(i+1);
            }
        }
    }
    public String getAns(){
        if(mAns.equals(""))
            return "&";
        return mAns;
    }
}
