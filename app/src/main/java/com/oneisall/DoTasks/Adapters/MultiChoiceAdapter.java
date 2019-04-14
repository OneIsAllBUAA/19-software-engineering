package com.oneisall.DoTasks.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import com.oneisall.R;
public class MultiChoiceAdapter extends RecyclerView.Adapter<MultiChoiceAdapter.MyViewHolder> {
    private List<String> mDatas;
    private List<String> mAns;
    private Context mContext;
    public MultiChoiceAdapter(List<String> qs,List<String> as, Context context){
        mDatas = qs;
        mAns = as;
        mContext = context;
    }
    @Override
    public MultiChoiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MultiChoiceAdapter.MyViewHolder holder = new MultiChoiceAdapter.MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_select_multi, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MultiChoiceAdapter.MyViewHolder holder, int position)
    {
        holder.tv.setText(mDatas.get(position));


        //TODO: add answer
    }
    private void addCheckBoxs(View v){
        LinearLayout layout = v.findViewById(R.id.multi_linear_layout);
        for(int i=0; i<4; i++){
            CheckBox cb = new CheckBox(mContext);
            //设置选项显示属性
            cb.setText("选项"+i);
            cb.setTextColor(Color.rgb(99,99,99));
            cb.setTextSize(16);
            //add
            layout.addView(cb);
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

        public MyViewHolder(View view)
        {
            super(view);
            tv = (TextView) view.findViewById(R.id.single_text);
            addCheckBoxs(view);
        }
    }
}