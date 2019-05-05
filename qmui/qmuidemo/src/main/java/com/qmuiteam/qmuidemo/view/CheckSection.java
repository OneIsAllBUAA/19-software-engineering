package com.qmuiteam.qmuidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmuidemo.R;

public class CheckSection extends RelativeLayout {
    //state
    public final static int NONE = 0;
    public final static int OPT_ALL = 1;
    public final static int OPT_CHART = 2;
    //var
    private TextView mQuestionText;
    private TextView mOptionText;
    private ImageView mOptionImg;
    private int mOptState;
    private LinearLayout mItemList;
    //list
    private SparseArray<check_all_item> mAllItemViews = new SparseArray<>();
    private SparseArray<check_chart_item> mChartItemViews = new SparseArray<>();

    public CheckSection(Context context, int state) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.check_item, this, true);
        mQuestionText = (TextView) findViewById(R.id.check_question_text);
        mOptionText = (TextView) findViewById(R.id.check_opt_text);
        mOptionImg = (ImageView) findViewById(R.id.check_opt_imag);
        mItemList = (LinearLayout) findViewById(R.id.checked_items);
        mOptState = state;
        //如果有选项统计报告，则可以在 统计报告 和 全部结果 中切换
        if(mOptState==OPT_CHART){
            mOptState = OPT_ALL;
            changeOpt();
            mOptionText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeOpt();
                }
            });
            mOptionImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeOpt();
                }
            });
        }
        //否则不显示切换选项
        else{
            mOptionText.setVisibility(GONE);
            mOptionImg.setVisibility(GONE);
        }
    }
    public void setQGone(){ mQuestionText.setVisibility(GONE);}
    public void changeOpt() {
        if(mOptState==OPT_ALL){
            //提示词
            mOptionText.setText("查看全部结果>");
            mOptionImg.setImageResource(R.drawable.loupe);
            //当前状态
            mOptState = OPT_CHART;
            initSectionViews();
        }
        else if(mOptState == OPT_CHART){
            mOptionText.setText("查看选项统计报告>");
            mOptionImg.setImageResource(R.drawable.bar_chart);
            mOptState = OPT_ALL;
            initSectionViews();
        }
    }
    public void setmQuestionText(String q){
        mQuestionText.setText(q);
    }
    public void addView(check_all_item ans){
        mAllItemViews.append(mAllItemViews.size(), ans);
    }
    public void addView(check_chart_item ans){
        mChartItemViews.append(mChartItemViews.size(), ans);
    }
    public void initSectionViews(){
        mItemList.removeAllViews();
        switch (mOptState){
            case OPT_ALL:{
                for(int i=0; i<mAllItemViews.size(); i++){
                    mItemList.addView(mAllItemViews.get(i));
                }
                break;
            }
            case OPT_CHART:{
                for(int i=0; i<mChartItemViews.size(); i++){
                    mItemList.addView(mChartItemViews.get(i));
                }
                break;
            }
            default:{}
        }
    }
}
