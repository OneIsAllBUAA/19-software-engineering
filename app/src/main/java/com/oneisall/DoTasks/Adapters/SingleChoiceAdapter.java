package com.oneisall.DoTasks.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
        Log.i(TAG, holder.tv + " " + holder.radioGroup+"");
        //TODO: add answer
        //设置选项
        for(int i=0; i<4; i++){
            RadioButton radioButton = new RadioButton(mContext);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            //设置RadioButton边距 (int left, int top, int right, int bottom)
            lp.setMargins(15,0,0,0);
            //设置文字距离四周的距离
            radioButton.setPadding(30, 0, 5, 5);
            //设置文字
            radioButton.setText("选项" + i);
            radioButton.setTextSize(16);
            radioButton.setTextColor(Color.rgb(99, 99, 99));
            //将radioButton添加到radioGroup中
            holder.radioGroup.addView(radioButton);
        }
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
}
