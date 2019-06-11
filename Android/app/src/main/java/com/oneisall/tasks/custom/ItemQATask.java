package com.oneisall.tasks.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
                int index = mAnswer.getSelectionStart() - 1;
                while (index > 0) {
                    Log.i(TAG, "index:"+index);
                    if (isEmojiCharacter(s.charAt(index))) {
                        Editable edit = mAnswer.getText();
//                        edit.delete(s.length() - 2, s.length());
                        edit.delete(index-1, index+1);
                        Toast.makeText(mContext,"不支持输入表情符号",Toast.LENGTH_SHORT).show();
                        index -= 1;
                    }
                    index -= 1;
                }
            }
        });
        //
        if(!mAns.equals("")) mAnswer.setText(mAns);
    }

    private boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    public String getAns(){ return "&"+mAns; }
}
