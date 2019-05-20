package com.oneisall.tasks.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oneisall.R;
import com.oneisall.model.response.CheckTaskRequestResult;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemCheckChart extends LinearLayout {
    final static private String TAG = "IteCheckChart";

    private Context mContext;

    private CheckTaskRequestResult.AnswerDetail mChartInfo;
    private int mIndex;

    @BindView(R.id.check_task_chart_progress) QMUIProgressBar mProgress;
    @BindView(R.id.check_task_answer_id) TextView mAnsId;
    @BindView(R.id.check_task_answer_text) TextView mAnsText;
    @BindView(R.id.check_task_answer_participate_num) TextView mVoteNumText;
    @BindView(R.id.check_task_view_detail_img) ImageView mViewDetailImg;

    public ItemCheckChart(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_check_task_chart, this, true);
        ButterKnife.bind(this);
        mContext = context;
        initUI();
    }
    public ItemCheckChart(Context context, CheckTaskRequestResult.AnswerDetail answersChartInfo, int id) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_check_task_chart, this, true);
        ButterKnife.bind(this);
        this.mChartInfo = answersChartInfo;
        this.mIndex = id;
        mContext = context;
        initUI();
    }

    public ItemCheckChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemCheckChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initUI(){
        if(mChartInfo!=null){
            //初始化 图标统计结果 布局
            //答案id
            mAnsId.setText((char)('A'+mIndex)+":");
            //答案内容
            mAnsText.setText(mChartInfo.getAnswer());
            //投票统计
            mVoteNumText.setText(mChartInfo.getVote_num()+"人投票");
            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSimpleBottomSheetList(mChartInfo.getUser_list(),mChartInfo.getAccept_num_list());
                }
            };
            mVoteNumText.setOnClickListener(listener);
            mViewDetailImg.setOnClickListener(listener);
            //进度条≈统计图
            mProgress.setProgress((int) (mChartInfo.getProportion() * 100));
            mProgress.setMaxValue(100);
            mProgress.setTextSize(30);
            mProgress.setTextColor(Color.parseColor("#00008B"));
            mProgress.setQMUIProgressBarTextGenerator(new QMUIProgressBar.QMUIProgressBarTextGenerator() {
                @Override
                public String generateText(QMUIProgressBar progressBar, int value, int maxValue) {
                    return 100 * value / maxValue + "%";
                }
            });
            mProgress.setBarColor(Color.parseColor("#ffffff"),Color.parseColor("#1E90FF"));
        }
    }



    private void showSimpleBottomSheetList(List<String> detail, List<Integer> num) {
        QMUIBottomSheet.BottomListSheetBuilder tmp = new QMUIBottomSheet.BottomListSheetBuilder(mContext);
        for(int i=0; i<detail.size(); i++){
            tmp.addItem(detail.get(i),"已通过"+num.get(i)+"条任务");
        }
        if(detail.size()==0){
            tmp.addItem("无人投票！","");
        }
        QMUIBottomSheet bs = tmp.build();
        bs.show();
    }

}
