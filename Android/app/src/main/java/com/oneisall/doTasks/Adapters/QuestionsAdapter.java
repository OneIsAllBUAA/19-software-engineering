package com.oneisall.doTasks.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.oneisall.R;

import java.util.List;

//recycle view apter
public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.MyViewHolder>
{
    private List<String> mDatas;
    private List<String> mAns;
    private Context mContext;
    public QuestionsAdapter(List<String> qs,List<String> as, Context context){
        mDatas = qs;
        mAns = as;
        mContext = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_qa, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        holder.tv.setText(mDatas.get(position));
        holder.et.setText(mAns.get(position));
        holder.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ansItemListener.onAnswerChanged(position, s.toString());
            }
        });
        holder.et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                ansItemListener.onAnswerChanged(v, position, holder.et.getText().toString());
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
        EditText et;

        public MyViewHolder(View view)
        {
            super(view);
            tv = (TextView) view.findViewById(R.id.question_text);
            et = (EditText)view.findViewById(R.id.question_answer);
        }
    }
    //
    //监听接口
    public interface onAnswerItemListener{
        void onAnswerChanged(int pos, String ans);
    }
    private onAnswerItemListener ansItemListener;
    public void setOnAnswerItemChangedListener(onAnswerItemListener ansItemListener){
        this.ansItemListener = ansItemListener;
    }
}