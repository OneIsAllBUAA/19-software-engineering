package com.qmuiteam.qmuidemo.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.fragment.task.CheckTaskFragment;
import com.qmuiteam.qmuidemo.model.request.SubmitCheckResultRequest;

import java.util.ArrayList;

public class check_all_item extends RelativeLayout {
    //static
    public final static int NONE = 0;
    public final static int ACCEPTED = 1;
    public final static int REJECTED = 2;
    //var
    private TextView mAuthor;
    private LinearLayout mAnsList;
    private LinearLayout mBtnFrame;
    private int ansState;
    private int clickOpt;
    private int label_id;
    public check_all_item(Context context, int id) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.display_all_answers, this, true);
        mAuthor = (TextView)findViewById(R.id.author_name);
        mAnsList = (LinearLayout)findViewById(R.id.answer_list);
        mBtnFrame = (LinearLayout) findViewById(R.id.accept_or_reject_frame);
        label_id = id;
    }
    //set author name
    public void setAuthorName(String name){
        mAuthor.setText(name);
    }
    //add answer to list
    public void addAnswer(String ans){
        TextView item = new TextView(getContext());
        item.setText(ans);
        item.setTextSize(15);
        item.setTextColor(Color.parseColor("#80000000"));
        mAnsList.addView(item);
    }
    //set buttons
    public void setAnsState(int state){
        ansState = state;
        setBtn();
    }
    private void setBtn(){
        clickOpt = NONE;
        mBtnFrame.removeAllViews();
        Button ac = new Button(getContext());
//        lp.setMargins(2,2,2,2);
        ac.setTextColor(Color.rgb(255,255,255));
//        ac.setBackgroundColor(Color.rgb(50,205,50));
        ac.setBackgroundColor(Color.parseColor("#32CD32"));
        Button reject = new Button(getContext());
        reject.setTextColor(Color.rgb(255,255,255));
//        reject.setBackgroundColor(Color.rgb(238,173,14));
        reject.setBackgroundColor(Color.parseColor("#EEAD0E"));
//        Log.i("CheckTaskItem", "produce button!");
        if(ansState==NONE){
            ac.setText("通过");
            reject.setText("退回");
            ac.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickOpt = ACCEPTED;
                    ansState = clickOpt;
                    setBtn();
                    if(mSubInter!=null) mSubInter.passThis(label_id);
                }
            });
            reject.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickOpt = REJECTED;
                    ansState = clickOpt;
                    setBtn();
                    if(mSubInter!=null) mSubInter.rejectThis(label_id);
                }
            });
            mBtnFrame.addView(ac);
            mBtnFrame.addView(reject);
        }
        else if(ansState== ACCEPTED){
            ac.setText("已通过");
            mBtnFrame.addView(ac);
        }
        else if(ansState== REJECTED){
            reject.setText("已退回");
            mBtnFrame.addView(reject);
        }
    }
    //回调接口
    private SubmitInterface mSubInter;
    public void setSubmitInterface(SubmitInterface inter){
        mSubInter = inter;
    }
    public interface SubmitInterface{
        public void passThis(int id);
        public void rejectThis(int id);
    }
    //get opt
    public int getClickOpt(){ return clickOpt; }
}
