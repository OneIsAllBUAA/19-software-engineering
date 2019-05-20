package com.oneisall.tasks.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oneisall.R;
import com.oneisall.model.response.CheckTaskRequestResult;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemCheckAll extends LinearLayout {
    final static private String TAG = "IteCheckAll";

    //static
    public final static int NONE = 0;
    public final static int ACCEPTED = 1;
    public final static int REJECTED = 2;

    private Context mContext;
    private CheckTaskRequestResult.UserAnswer mAllUser;
    private int mState;

    @BindView(R.id.check_task_all_author_name)
    TextView mUsername;
    @BindView(R.id.check_task_all_answer_list) LinearLayout mAnsLinear;
    @BindView(R.id.check_task_all_button_linear) LinearLayout mBtnLinear;

    public ItemCheckAll(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_check_task_all, this, true);
        ButterKnife.bind(this);
        mContext = context;
        initUI();
    }
    public ItemCheckAll(Context context, CheckTaskRequestResult.UserAnswer allUser, int state) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_check_task_all, this, true);
        ButterKnife.bind(this);
        mAllUser = allUser;
        mState = state;
        mContext = context;
        initUI();
    }

    public ItemCheckAll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemCheckAll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initUI(){
        if(mAllUser!=null){
            //审核细节
            //用户名
            mUsername.setText(mAllUser.getUserName());
            //该用户的答案
            for(String ans: mAllUser.getUser_answer()){
                TextView item = new TextView(getContext());
                item.setText(ans);
                item.setTextSize(14);
                item.setTextColor(getResources().getColor(R.color.text_black));
                mAnsLinear.addView(item);
            }
            //根据state设置按钮
            setBtns();
        }
    }
    private void setBtns(){
        mBtnLinear.removeAllViews();
        Button ac = new Button(getContext());
        ac.setTextColor(getResources().getColor(R.color.colorWhite));
        ac.setBackgroundColor(getResources().getColor(R.color.check_task_accept_color));
        Button reject = new Button(getContext());
        reject.setTextColor(getResources().getColor(R.color.colorWhite));
        reject.setBackgroundColor(getResources().getColor(R.color.check_task_reject_color));
        if(mState==NONE){
            ac.setText("通过");
            reject.setText("退回");
            ac.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSubInter!=null) mSubInter.passThis(mAllUser.getLabel_id());
                }
            });
            reject.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSubInter!=null) mSubInter.rejectThis(mAllUser.getLabel_id());
                }
            });
            mBtnLinear.addView(ac);
            mBtnLinear.addView(reject);
        }
        else if(mState== ACCEPTED){
            ac.setText("已通过");
            mBtnLinear.addView(ac);
        }
        else if(mState== REJECTED){
            reject.setText("已退回");
            mBtnLinear.addView(reject);
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
        public void passSingleByThreshold(double threshold);
        public void passMultiByThreshold(double threshold);
    }
    //
    public int getLableId(){ return mAllUser.getLabel_id(); }
    public void setAnsState(int state){
        mState = state;
        setBtns();
    }
}
