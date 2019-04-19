package com.oneisall.DoTasks.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.oneisall.R;

import java.util.List;

public class SingleChoiceAdapter extends RecyclerView.Adapter<SingleChoiceAdapter.MyViewHolder>{
    final static String TAG = "SingleAdapter";
    private List<String> mDatas;
    private List<String> mAns;
    private Context mContext;
    public SingleChoiceAdapter(List<String> qs,List<String> as, Context context){
        mDatas = qs;
        mAns = as;
        mContext = context;
    }
    @Override
    public SingleChoiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        SingleChoiceAdapter.MyViewHolder holder = new SingleChoiceAdapter.MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_select_single, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(SingleChoiceAdapter.MyViewHolder holder, int position)
    {
        holder.tv.setText(mDatas.get(position));
        //TODO: add answer
        //设置选项
        for(int i=0; i<3; i++){
            RadioButton radioButton = new RadioButton(mContext);
            //设置文字距离四周的距离
            radioButton.setPadding(30, 0, 5, 5);
            //设置文字
            radioButton.setText("选项" + i);
            radioButton.setTextSize(16);
            radioButton.setTextColor(Color.rgb(99, 99, 99));
            //将radioButton添加到radioGroup中
            holder.radioGroup.addView(radioButton);
        }
        //设置监听
        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int i;
                for(i=0; i<holder.radioGroup.getChildCount(); i++){
                    RadioButton rd = (RadioButton)holder.radioGroup.getChildAt(i);
                    if(rd.isChecked()){
                        break;
                    }
                }
                checkItemListener.onCheckChanged(position, i+1);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv;
        RadioGroup radioGroup;
        public MyViewHolder(View view)
        {
            super(view);
            tv = (TextView) view.findViewById(R.id.single_text);
            radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        }
    }
    //监听接口
    public interface onCheckItemListener{
        void onCheckChanged(int pos, int checkId);
    }
    private onCheckItemListener checkItemListener;
    public void setOnCheckItemChangedListener(onCheckItemListener checkItemListener){
        this.checkItemListener = checkItemListener;
    }
}
