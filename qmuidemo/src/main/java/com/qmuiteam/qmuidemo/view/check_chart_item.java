package com.qmuiteam.qmuidemo.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmuidemo.R;



public class check_chart_item extends RelativeLayout {
    private QMUIProgressBar mProgress;
    private TextView mAnsId;
    private TextView mAnsText;
    private TextView mPartiNum;
    private TextView mChartDetail;
    public check_chart_item(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.display_answer_chart, this, true);
        mAnsId = (TextView)findViewById(R.id.answer_id);
        mAnsText = (TextView) findViewById(R.id.answer_text);
        mPartiNum = (TextView) findViewById(R.id.answer_participate_num);
        mChartDetail = (TextView) findViewById(R.id.chart_detail);
        mProgress = (QMUIProgressBar) findViewById(R.id.chart_progress);
    }
    public void setDetailClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            mChartDetail.setOnClickListener(onClickListener);
        }
    }
    public void setAnsId(int id){
        mAnsId.setText((char)('A'+id)+":");
    }
    public void setAnsText(String text){
        mAnsText.setText(text);
    }
    public void setPartNum(int num){
        mPartiNum.setText(num+"人投票");
    }
    public void setProgressValue(int value){
        mProgress.setProgress(value);
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