package com.oneisall.DoTasks;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.oneisall.Api.TaskApi;
import com.oneisall.DoTasks.Adapters.QuestionsAdapter;
import com.oneisall.Model.SubTaskDetail;
import com.oneisall.Model.SubTaskResult;
import com.oneisall.Model.TaskInfo;
import com.oneisall.Model.TaskRequest;
import com.oneisall.R;

import java.util.ArrayList;
import java.util.List;

import static com.oneisall.Constants.UrlConstants.MEDIA_BASE;

public class QuestionsActivity extends AppCompatActivity implements  View.OnClickListener {

    private static final String TAG = "QuestionsTask";
    private TextView mProg;
    private ImageView mImgSub;
    private Button mGiveUp;
    private Button mSubmit;
    private RecyclerView mRecycle;
    //stl
    private String mPath;
    private int subId;
    private int pathId = -1;
    private List<String> mDatas=new ArrayList<String>();
    private List<String> mAns = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        //init
        getSubTask();
        initView();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.give_up:{
                Toast.makeText(this, "give up", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.submit:{
                if(submitMessage())
                    getSubTask();
                else
                    Toast.makeText(this, "submit error", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
    Boolean submitMessage(){
        //check and package
        String result="";
        for(int i=0; i<mDatas.size();i++){
            if(mAns.get(i).equals("")){
                Log.i(TAG, "null answer");
                return false;
            }
            result += "|q"+i+"&"+mAns.get(i);
        }
        //post TODO:改为真实参数
        return TaskApi.postSubTaskResult(new SubTaskResult("hhh",9, subId, result));
    }
    void getSubTask(){
        GetTaskInfo task = new GetTaskInfo();
        task.setOnDataFinishedListener(new GetTaskInfo.OnDataFinishedListener() {
            @Override
            public void onDataSuccessfully(TaskInfo taskInfo) {
                List<SubTaskDetail> resultInfo = taskInfo.getResultArray();
                mPath = MEDIA_BASE+taskInfo.getResultArray().get(0).getFields().getFile();
                Log.i(TAG, mPath);
                Glide.with(QuestionsActivity.this).load(mPath).into(mImgSub);
                subId = taskInfo.getResultArray().get(0).getPk();
            }

            @Override
            public void onDataFailed() {
                Toast.makeText(QuestionsActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "获取信息失败");
            }
        });
        task.execute();
    }
    void initView(){
        //get instances
        mProg = (TextView)findViewById(R.id.subtask_progress);
        mImgSub = (ImageView) findViewById(R.id.img_subtask);
        Glide.with(this).load(mPath).into(mImgSub);
        mProg.setText((pathId+1)+"/1");
        mGiveUp = (Button)findViewById(R.id.give_up);
        mSubmit = (Button)findViewById(R.id.submit);
        //on click listener
        mGiveUp.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
        //TODO: delete
        initDatas();
        Log.i(TAG, "init data");
        //recycle view
        QuestionsAdapter adapter = new QuestionsAdapter(mDatas,mAns,QuestionsActivity.this);
        mRecycle = (RecyclerView)findViewById(R.id.qa_recyle_view);
        mRecycle.setLayoutManager(new LinearLayoutManager(this));
        mRecycle.setAdapter(adapter);
        Log.i(TAG, "ok");
        //on changed,监听事件
        adapter.setOnAnswerItemChangedListener(new QuestionsAdapter.onAnswerItemListener() {
            @Override
            public void onAnswerChanged(View v, int pos, String ans) {
                mAns.set(pos, ans);
                Toast.makeText(QuestionsActivity.this,ans, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initDatas(){
        for(int i=0; i<1; i++){
            mDatas.add("Q"+i+": please answer");
            mAns.add("");
        }
    }

    //get info
    private static class GetTaskInfo extends AsyncTask<Void, Void, TaskInfo> {
        @Override
        protected TaskInfo doInBackground(Void... voids) {
            return TaskApi.getTaskInfo(new TaskRequest());
        }

        @Override
        protected void onPostExecute(TaskInfo taskInfo) {
            Log.i(TAG, "onPostExecute: " + taskInfo.getResultArray());
            if(taskInfo!=null){
                mDataFinishedListener.onDataSuccessfully(taskInfo);
            }
            else{
                mDataFinishedListener.onDataFailed();
            }
        }
        //
        OnDataFinishedListener mDataFinishedListener;
        public void setOnDataFinishedListener(OnDataFinishedListener m){
            this.mDataFinishedListener = m;
        }
        //回调接口
        public interface OnDataFinishedListener {
            public void onDataSuccessfully(TaskInfo taskInfo);
            public void onDataFailed();
        }

        }
}
