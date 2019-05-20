package com.oneisall.tasks.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oneisall.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemQATask extends LinearLayout {
    final private static String TAG = "ItemSingleTask";

    private String question;
    private List<String> ansList;
    private Context mContext;
    private String mAns="";
    //
    @BindView(R.id.qa_question_text) TextView mQuestion;
    @BindView(R.id.qa_question_answer) EditText mAnswer;

    public ItemQATask(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_select_single, this, true);
        ButterKnife.bind(this);
        mContext = context;
        initUI();
    }
    public ItemQATask(Context context, String question, List<String> ansList) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_qa, this, true);
        ButterKnife.bind(this);
        this.question = question;
        this.ansList = ansList;
        mContext = context;
        initUI();
    }

    public ItemQATask(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemQATask(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initUI(){
        mQuestion.setText(question);
        //监听输入变化
        mAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAns = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //
        if(!mAns.equals("")) mAnswer.setText(mAns);
    }

    public String getAns(){ return "&"+mAns; }
}
