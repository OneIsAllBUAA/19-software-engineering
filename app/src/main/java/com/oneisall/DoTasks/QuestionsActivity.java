package com.oneisall.DoTasks;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.michaldrabik.tapbarmenulib.TapBarMenu;
import com.oneisall.Api.TaskApi;
import com.oneisall.Constants.TaskTypes;
import com.oneisall.Constants.Templates;
import com.oneisall.DoTasks.Adapters.MultiChoiceAdapter;
import com.oneisall.DoTasks.Adapters.MyJzvdStd;
import com.oneisall.DoTasks.Adapters.QuestionsAdapter;
import com.oneisall.DoTasks.Adapters.SingleChoiceAdapter;
import com.oneisall.Model.SubTaskDetail;
import com.oneisall.Model.SubTaskResult;
import com.oneisall.Model.TaskDetail;
import com.oneisall.Model.TaskRequest;
import com.oneisall.R;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;
import static com.oneisall.Constants.UrlConstants.MEDIA_BASE;

/**
 * 需添加网络应答为空的验证机制
 */
public class QuestionsActivity extends AppCompatActivity implements  View.OnClickListener {

    private static final String TAG = "QuestionsTask";
    private TaskDetail taskDetail;

    private TextView mProg;
    //
    private ImageView mImgSub;
    //
    private MyJzvdStd myJzvdStd;
    RequestOptions options;
    MediaPlayer mMediaPlayer;
    //
    private TapBarMenu mButtons;
    private ImageView mGiveUp, mSubmit;
    private RecyclerView mRecycle;
    //stl
    private String mPath;
    private int subId;
    private int pathId = -1;
    private List<String> mDatas=new ArrayList<String>();
    private List<String> mAns = new ArrayList<String>();
    //
    int taskType = 2;
    int template = 3;
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
            case R.id.tapBarMenu:{
                Log.i(TAG,"click pop menu");
                mButtons.toggle();
                break;
            }
            case R.id.give_up:{
                Toast.makeText(this, "give up", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.submit:{
                submitMessage();
                break;
            }
        }
    }
    void submitMessage(){
        //check and package
        String result="";
        for(int i=0; i<mDatas.size();i++){
            if(mAns.get(i).equals("")){
                Log.i(TAG, "null answer");
                Toast.makeText(QuestionsActivity.this,"答案不能为空", Toast.LENGTH_SHORT).show();
                return ;
            }
            result += "|q"+(i+1)+mAns.get(i);
        }
        //post TODO:改为真实参数
        PostSubTaskResult postTask = new PostSubTaskResult();
        postTask.setContext(QuestionsActivity.this);
        postTask.execute(new SubTaskResult("hhh",22, subId, result));
    }
    void getSubTask(){
        GetTaskInfo task = new GetTaskInfo();
        task.setOnDataFinishedListener(new GetTaskInfo.OnDataFinishedListener() {
            @Override
            public void onDataSuccessfully(SubTaskDetail taskInfo) {
                SubTaskDetail subTaskDetail = taskInfo;
                mPath = MEDIA_BASE+subTaskDetail.getFields().getFile();
                Log.i(TAG, mPath);
                if(mPath==""){
                    Toast.makeText(QuestionsActivity.this, "任务已完成", Toast.LENGTH_SHORT).show();
                    //TODO: 返回上一级activity界面，任务完成提示已在onDataFailed给出。
                    return ;
                }
                Glide.with(QuestionsActivity.this).load(mPath).into(mImgSub);
                subId = subTaskDetail.getPk();
                mProg.setText(subTaskDetail.getSeq()+"/"+subTaskDetail.getNum());
                setMeida();
                Toast.makeText(QuestionsActivity.this, "加载完成", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDataFailed() {
                Toast.makeText(QuestionsActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
//                finish();//go back
                Log.i(TAG, "获取信息失败");
                //TODO:delete
                setMeida();
            }
        });
        task.execute();
    }
    void initView(){
        //get instances
        mProg = (TextView)findViewById(R.id.subtask_progress);
        mButtons = (TapBarMenu)findViewById(R.id.tapBarMenu);
        mGiveUp = (ImageView)findViewById(R.id.give_up);
        mSubmit = (ImageView)findViewById(R.id.submit);

        mImgSub = (ImageView) findViewById(R.id.img_subtask);
        myJzvdStd= (MyJzvdStd)findViewById(R.id.videoplayer);
        Glide.with(this).load(R.mipmap.loading).into(mImgSub);
        options = new RequestOptions()
                .error(R.mipmap.error);
        //on click listener
        mButtons.setOnClickListener(this);
        mGiveUp.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
        //TODO: delete
        initDatas();
        Log.i(TAG, "init data");
        setQList();
    }
    private void setQList(){
        //recycle view
        mRecycle = (RecyclerView)findViewById(R.id.qa_recyle_view);
        mRecycle.setLayoutManager(new LinearLayoutManager(this));
        if(taskType == TaskTypes.SINGLE){
            setSingleTask();
        }
        else if(taskType == TaskTypes.MULTI){
            setMultiTask();
        }
        else if(taskType == TaskTypes.QA){
            setQATask();
        }
    }
    //三种任务种类的适配器设置
    private void setSingleTask(){
        SingleChoiceAdapter adapter = new SingleChoiceAdapter(mDatas,mAns,QuestionsActivity.this);
        mRecycle.setAdapter(adapter);
        //on checked
        adapter.setOnCheckItemChangedListener(new SingleChoiceAdapter.onCheckItemListener() {
            @Override
            public void onCheckChanged(int pos, int checkId) {
                mAns.set(pos, "&"+checkId+"");
                //TODO: delete
                Toast.makeText(QuestionsActivity.this,checkId+"", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setMultiTask(){
        MultiChoiceAdapter adapter = new MultiChoiceAdapter(mDatas,mAns,QuestionsActivity.this);
        mRecycle.setAdapter(adapter);
    }
    private void setQATask(){
        QuestionsAdapter adapter = new QuestionsAdapter(mDatas,mAns,QuestionsActivity.this);
        mRecycle.setAdapter(adapter);
        //on changed,监听事件
        adapter.setOnAnswerItemChangedListener(new QuestionsAdapter.onAnswerItemListener() {
            @Override
            public void onAnswerChanged(int pos, String ans) {
                mAns.set(pos, "&"+ans);
                Toast.makeText(QuestionsActivity.this,ans, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //TODO: 拆分任务content中的具体问题
    private void initDatas(){
        for(int i=0; i<4; i++){
            mDatas.add("Q"+i+": please answer");
            mAns.add("");
        }
    }
    //video player
    void setVideoPlayer(){
//        myJzvdStd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;  //纵向
        //TODO:切换为真实文件url。eg：网络音频地址：http://img.tukuppt.com/newpreview_music/09/00/32/5c89189c4f4cf81405.mp3
        //演示视频地址：http://img.tukuppt.com/video_show/2418175/00/01/29/5b3ef186949f4.mp4
//        mPath = "http://img.tukuppt.com/video_show/2418175/00/01/29/5b3ef186949f4.mp4";
        mPath = "http://img.tukuppt.com/newpreview_music/09/00/32/5c89189c4f4cf81405.mp3";
        myJzvdStd.setUp(mPath
                , "", JzvdStd.SCREEN_NORMAL);
        //视频缩略图
        loadVideoScreenshot(this, mPath, myJzvdStd.thumbImageView, 1);
    }
    public void loadVideoScreenshot(final Context context, String uri, ImageView imageView, long frameTimeMicros) {
        if(template!=1) return ;
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        requestOptions.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }
            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((context.getPackageName() + "RotateTransform").getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);
    }
    void setMeida(){
        //TODO: get real file url
//        int template = taskDetail.getFields().getTemplate()
        if(template==Templates.VIDEO){
            mImgSub.setVisibility(View.GONE);
            myJzvdStd.setVisibility(View.VISIBLE);
            setVideoPlayer();
        }
        else if(template==Templates.IMG){
            myJzvdStd.setVisibility(View.GONE);
            mImgSub.setVisibility(View.VISIBLE);
            Glide.with(this).load(mPath).apply(options).into(mImgSub);
        }
        else if(template==Templates.AUDIO){
            mImgSub.setVisibility(View.GONE);
            myJzvdStd.setVisibility(View.VISIBLE);
//            Glide.with(this).load(R.mipmap.music_player).apply(options).into(mImgSub);
            setVideoPlayer();

        }
        else{
            findViewById(R.id.media_frame).setVisibility(View.GONE);

        }
    }
    //get info
    private static class GetTaskInfo extends AsyncTask<Void, Void, SubTaskDetail> {
        @Override
        protected SubTaskDetail doInBackground(Void... voids) {
            return TaskApi.getTaskInfo(new TaskRequest());
        }

        @Override
        protected void onPostExecute(SubTaskDetail taskInfo) {
            if(taskInfo!=null){
                Log.i(TAG, "get info:"+taskInfo.toString());
                mDataFinishedListener.onDataSuccessfully(taskInfo);
            }
            else{
                Log.i(TAG, "get failed");
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
            public void onDataSuccessfully(SubTaskDetail taskInfo);
            public void onDataFailed();
        }

    }
    //post subtask result
    private class PostSubTaskResult extends AsyncTask<SubTaskResult, Void, Boolean>{
        Context context;
        public void setContext(Context c){
            context = c;
        }
        @Override
        protected Boolean doInBackground(SubTaskResult... results) {
            return TaskApi.postSubTaskResult(results[0]);
        }

        @Override
        protected void onPostExecute(Boolean suc) {
            if(!suc){
                Toast.makeText(context, "submit error", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.i("PostResult", suc+"");
                Toast.makeText(context, "已提交", Toast.LENGTH_SHORT).show();
                getSubTask();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.resetAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
