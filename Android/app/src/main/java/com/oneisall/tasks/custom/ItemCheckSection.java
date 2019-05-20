package com.oneisall.tasks.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oneisall.R;
import com.oneisall.model.response.CheckTaskRequestResult;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.oneisall.constants.TaskTypes.TYPES_LABEL;
import static com.oneisall.constants.TaskTypes.TYPES_MULTI;
import static com.oneisall.constants.TaskTypes.TYPES_QA;
import static com.oneisall.constants.TaskTypes.TYPES_SINGLE;

public class ItemCheckSection extends LinearLayout {

    final static private String TAG = "IteCheckSection";

    private List<ItemCheckChart> mChartItems = new ArrayList<>();
    private List<ItemCheckAll> mAllItems = new ArrayList<>();
    private Context mContext;
    private int mIndex;
//    private int taskType;

    @BindView(R.id.check_question_text) TextView mQuestion;
    @BindView(R.id.check_opt_imag) ImageView mOptImg;
    @BindView(R.id.check_opt_text) TextView mOptText;
    @BindView(R.id.checked_items) LinearLayout mItemLinear;

    @BindView(R.id.check_task_threshold_nice_spinner) NiceSpinner mSelectSpinner;
    @BindView(R.id.check_task_threshold_pass_button) TextView mPassThresholdBth;
    @BindView(R.id.check_task_threshold_frame) LinearLayout mThresholdFrame;
    public ItemCheckSection(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_check_section, this, true);
        ButterKnife.bind(this);
        mContext = context;
        initItems(null,null);
    }

    public ItemCheckSection(Context context, CheckTaskRequestResult.CheckQA qa, ItemCheckAll.SubmitInterface inter, int taskType, int id) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_check_section, this, true);
        ButterKnife.bind(this);
//        this.taskType = taskType;
        this.mIndex = id;
        mContext = context;
        //初始化各种类型的所有条目
        initItems(qa,inter);
        //根据任务类型加载布局
        initUI(taskType, inter);
    }

    public ItemCheckSection(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemCheckSection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initItems(CheckTaskRequestResult.CheckQA qa, ItemCheckAll.SubmitInterface inter){
        if(qa==null) return;
        //设置问题
        mQuestion.setText((mIndex+1)+"."+qa.getQuestion());
        //初始化该问题所有的chart表
        for(int i=0; i<qa.getAnswers().size();i++){
            mChartItems.add(new ItemCheckChart(mContext, qa.getAnswers().get(i), i));
        }
        //初始化该问题的所有详细答案
        for(CheckTaskRequestResult.UserAnswer uans: qa.getDetails()){
            ItemCheckAll item = new ItemCheckAll(mContext, uans, uans.getState());
            item.setSubmitInterface(inter);
            mAllItems.add(item);
        }
    }
    private void initUI(int type, ItemCheckAll.SubmitInterface inter){
        switch (type){
            case TYPES_SINGLE:
            case TYPES_MULTI:{
                //现在展示的是统计图，用户可选择查看全部结果
                mOptText.setText(R.string.check_task_opt_to_all);
                mOptImg.setImageDrawable(getResources().getDrawable(R.mipmap.loupe));
                //展示统计图
                displayCharts();
                //设置阈值通过及响应按钮
                List<String> dataset = new LinkedList<>(Arrays.asList("0%", "20%", "40%", "60%", "80%", "100%"));
                mSelectSpinner.attachDataSource(dataset);
                mPassThresholdBth.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double threshold = get_threshold(mSelectSpinner.getText().toString());
                        if(type==TYPES_SINGLE){
                            inter.passSingleByThreshold(threshold);
                            //TODO: 更新ItemCheckAll中的状态
//                            for(int i=0; i<mChartItems.size())
                        }
                        else{
                            inter.passMultiByThreshold(get_threshold(mSelectSpinner.getText().toString()));
                        }
                    }
                });
                break;
            }
            case TYPES_QA:
            case TYPES_LABEL:{
                mOptText.setVisibility(GONE);
                mOptImg.setVisibility(GONE);
                mThresholdFrame.setVisibility(GONE);
                displayAll();
                break;
            }
        }

    }

    private void displayCharts(){
        mThresholdFrame.setVisibility(VISIBLE);
        mItemLinear.removeAllViews();
        for(ItemCheckChart item: mChartItems){
            mItemLinear.addView(item);
        }
    }
    private void displayAll(){
        mThresholdFrame.setVisibility(GONE);
        mItemLinear.removeAllViews();
        for(ItemCheckAll item: mAllItems){
            mItemLinear.addView(item);
        }
    }

    @OnClick({R.id.check_opt_text,R.id.check_opt_imag})
    public void onClick(){
        //如果当前显示的是“查看统计报告”，则显示统计报告
        if(mOptText.getText().equals(getResources().getString(R.string.check_task_opt_to_chart))){
            Log.i(TAG, "click 1....");
            mOptText.setText(R.string.check_task_opt_to_all);
            mOptImg.setImageDrawable(getResources().getDrawable(R.mipmap.loupe));
            displayCharts();
        }
        //否则
        else{
            Log.i(TAG, "click 1....");
            mOptText.setText(R.string.check_task_opt_to_chart);
            mOptImg.setImageDrawable(getResources().getDrawable(R.mipmap.bar_chart));
            displayAll();
        }
    }

    private double get_threshold(String s){
        if(s.equals("0%")) return 0;
        if(s.equals("20%")) return 0.2;
        if(s.equals("40%")) return 0.4;
        if(s.equals("60%")) return 0.6;
        if(s.equals("80%")) return 0.8;
        return 1;
    }

    public void updateItemCheckAll(int id,int state){
        for(ItemCheckAll item: mAllItems){
            if(item.getLableId()==id){
                item.setAnsState(state);
            }
        }
    }

    //阈值通过后刷新列表
    public void refreshAllItem(List<Integer> idList, int state){
        for(ItemCheckAll item : mAllItems){
            if(idList.contains(item.getLableId())){
                item.setAnsState(state);
            }
        }
    }
}
