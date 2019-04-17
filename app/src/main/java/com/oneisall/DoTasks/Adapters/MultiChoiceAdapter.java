package com.oneisall.DoTasks.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import com.oneisall.R;
public class MultiChoiceAdapter extends RecyclerView.Adapter<MultiChoiceAdapter.MyViewHolder> {
    final static String TAG = "MultiAdapter";
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
        //设置监听
        for(int i=0; i<holder.cbs.getChildCount(); i++){
            CheckBox cb = (CheckBox)holder.cbs.getChildAt(i);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    resetResult(position, holder.cbs);
                }
            });
        }
    }
    private void resetResult(int pos, LinearLayout cbs){
        String re="";
        for(int i=0; i<cbs.getChildCount(); i++){
            if(((CheckBox)cbs.getChildAt(i)).isChecked()){
                re += "&"+(i+1);
            }
        }
        mAns.set(pos, re);
        Log.i(TAG, pos+":"+re);
    }
    private void addCheckBoxs(LinearLayout cbs){
        //TODO: add answer
        for(int i=0; i<4; i++){
            CheckBox cb = new CheckBox(mContext);
            //设置选项显示属性
            cb.setText("选项"+i);
            cb.setTextColor(Color.rgb(99,99,99));
            cb.setTextSize(16);
            //add
            cbs.addView(cb);
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
        LinearLayout cbs;

        public MyViewHolder(View view)
        {
            super(view);
            tv = (TextView) view.findViewById(R.id.single_text);
            cbs = (LinearLayout) view.findViewById(R.id.multi_linear_layout);
            addCheckBoxs(cbs);
        }
    }

    //监听接口
    public interface onCheckItemsListener {
        void onCheckChanged(int pos, int checkId);
    }
    private onCheckItemsListener checkItemsListener;
    public void setOnCheckItemChangedListener(onCheckItemsListener checkItemsListener){
        this.checkItemsListener = checkItemsListener;
    }
}