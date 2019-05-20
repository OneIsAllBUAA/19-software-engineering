package com.oneisall.tasks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.oneisall.R;
import com.oneisall.model.response.EnterTaskRequestResult;
import com.oneisall.model.response.SubTask;
import com.oneisall.model.response.Task;
import com.oneisall.tasks.adapters.MyJzvdStd;
import com.oneisall.tasks.custom.ItemMultiTask;
import com.oneisall.tasks.custom.ItemQATask;
import com.oneisall.tasks.custom.ItemSingleTask;
import com.oneisall.utils.DpUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import static com.oneisall.constants.TaskTypes.TEMPLATES_AUDIO;
import static com.oneisall.constants.TaskTypes.TEMPLATES_PIC;
import static com.oneisall.constants.TaskTypes.TEMPLATES_VIDEO;
import static com.oneisall.constants.TaskTypes.TYPES_MULTI;
import static com.oneisall.constants.TaskTypes.TYPES_QA;
import static com.oneisall.constants.TaskTypes.TYPES_SINGLE;
import static com.oneisall.constants.UrlConstants.WEBSITE_BASE;

public class DoSubTaskFragment extends Fragment {

    final private static String TAG = "DoSubTaskFragment";
    final private static int SCROLL_TO_TRANSPARENT = 20;
    //stl
    private Task task;
    private SubTask subTask;
    private List<EnterTaskRequestResult.QA> qaList;
    private boolean displaySubmit;
    //
    private ImageView mSubImg;
    private PhotoView mScaleImg;
    private MyJzvdStd mSubVideo;
    private LinearLayout mSubVideoLinear;
    private PlayerView mSubAudio;

    private NestedScrollView mScrollView;
    private CardView mMediaFrame;

    private LinearLayout mQALinear;
    private Context mContext;
    private boolean mLoadFail;
    RequestOptions options;



    public static DoSubTaskFragment getInstance(SubTask subTask, List<EnterTaskRequestResult.QA> qaList, Task task, PhotoView img, boolean displaySubmit, Context context) {
        DoSubTaskFragment fragment = new DoSubTaskFragment();
        fragment.task = task;
        fragment.subTask = subTask;
        fragment.qaList = qaList;
        fragment.mScaleImg = img;
        fragment.displaySubmit = displaySubmit;
        fragment.mContext = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subtask, null);
        //图片相关
        mSubImg = v.findViewById(R.id.subtask_img);
        //视频相关
        mSubVideo = v.findViewById(R.id.subtask_videoplayer);
        mSubVideoLinear = v.findViewById(R.id.subtask_video_linear);
        //音频相关
        mSubAudio = v.findViewById(R.id.subtask_audio);
        //设置向上滑动媒体文件框“透明”
        mScrollView = v.findViewById(R.id.subtask_scroll_view);
        mMediaFrame = v.findViewById(R.id.media_frame);
        //答题区
        mQALinear = v.findViewById(R.id.qa_linear);
        initUI(v);
        setSubmitButton();
        return v;
    }

    private void initUI(View view){
        //设置媒体frame：图片，音频或视频
        if(task.getFields().getTemplate()==TEMPLATES_AUDIO){
            mQALinear.setPadding(0,DpUtils.dip2px(mContext,90),0,DpUtils.dip2px(mContext,80));
        }
        else{
            mQALinear.setPadding(0,DpUtils.dip2px(mContext,190),0,DpUtils.dip2px(mContext,80));
        }
        setMediaFrame();
        //加载选项列表
        setQAList();
        //滑动
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.i(TAG, scrollX+","+scrollY+"----"+oldScrollX+","+oldScrollY);
                if(DpUtils.px2dp(mContext,scrollY) >= SCROLL_TO_TRANSPARENT){
                    mMediaFrame.setElevation(0);
//                    Toast.makeText(mContext,"该隐藏了",Toast.LENGTH_SHORT).show();
//                    Log.i(TAG,"g该隐藏了!!!");
                }
                else if(DpUtils.px2dp(mContext,scrollY) < SCROLL_TO_TRANSPARENT){
                    mMediaFrame.setElevation(5);
//                    Jzvd.goOnPlayOnResume();
                }
            }
        });
    }
    private void setMediaFrame(){
        String mPath = WEBSITE_BASE + subTask.getFile();
        mLoadFail = false;
        switch (task.getFields().getTemplate()){
            case TEMPLATES_PIC:{
                mSubVideo.setVisibility(View.GONE);
                mSubVideoLinear.setVisibility(View.GONE);
                mSubAudio.setVisibility(View.GONE);
                mSubImg.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(mPath)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Toast.makeText(getContext(),"加载失败，点击重试",Toast.LENGTH_SHORT).show();
                                mLoadFail = true;
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .fitCenter()
//                        .override(650,650)
                        .into(mSubImg);
                //缩放
                mSubImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scaleImag(mPath);
                    }
                });
                break;
            }
            case TEMPLATES_VIDEO:{
                mSubImg.setVisibility(View.GONE);
                mSubAudio.setVisibility(View.GONE);
                mSubVideo.setVisibility(View.VISIBLE);
                mSubVideoLinear.setVisibility(View.VISIBLE);
                mSubVideo.setUp(mPath, "", JzvdStd.SCREEN_NORMAL);
                //视频缩略图
                loadVideoScreenshot(mContext, mPath, mSubVideo.thumbImageView, 1);
                break;
            }
            case TEMPLATES_AUDIO:{
                mSubImg.setVisibility(View.GONE);
                mSubVideoLinear.setVisibility(View.GONE);
                mSubVideo.setVisibility(View.GONE);
                mSubAudio.setVisibility(View.VISIBLE);
                SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(getContext()),new DefaultTrackSelector(),new DefaultLoadControl());
                mSubAudio.setPlayer(player);
                player.setPlayWhenReady(false);
                player.seekTo(0);
                MediaSource mediaSource = buildMediaSource(Uri.parse(mPath));
                player.prepare(mediaSource, true, false);
                player.addListener(new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                        Log.i(TAG, error.getMessage());
                        Toast.makeText(getContext(),"加载失败，点击重试",Toast.LENGTH_SHORT).show();
                        mLoadFail = true;
                    }

                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                    @Override
                    public void onSeekProcessed() {

                    }
                });
                break;
            }
        }
    }
    private void setQAList(){
        switch(task.getFields().getType()){
            case TYPES_SINGLE:{
                setSingleTask();
                break;
            }
            case TYPES_MULTI:{
                setMultiTask();
                break;
            }
            case TYPES_QA:{
                setQATask();
                break;
            }
        }
    }
    private void setSingleTask(){
        for(int i=0; i<qaList.size(); i++){
            EnterTaskRequestResult.QA qa= qaList.get(i);
            ItemSingleTask q = new ItemSingleTask(mContext, (i+1)+"."+qa.getQuestion(),qa.getAnswers());
            mQALinear.addView(q);
        }
    }
    private void setMultiTask(){
        for(int i=0; i<qaList.size(); i++){
            EnterTaskRequestResult.QA qa= qaList.get(i);
            ItemMultiTask q = new ItemMultiTask(mContext, (i+1)+"."+qa.getQuestion(),qa.getAnswers());
            mQALinear.addView(q);
        }
    }
    private void setQATask(){
        for(int i=0; i<qaList.size(); i++){
            EnterTaskRequestResult.QA qa= qaList.get(i);
            ItemQATask q = new ItemQATask(mContext, (i+1)+"."+qa.getQuestion(),qa.getAnswers());
            mQALinear.addView(q);
        }
    }
    private void setSubmitButton(){
        //提交按钮
        Log.i(TAG, displaySubmit+"");
        if(displaySubmit){
            QMUIRoundButton btn = new QMUIRoundButton(mContext);
            btn.setText("提交");
            btn.setTextSize(16);
            btn.setTextColor(getResources().getColor(R.color.colorWhite));
            btn.setBackground(getResources().getDrawable(R.drawable.submit_button_border_view));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lp.setMargins(230,10,230,0);
            btn.setLayoutParams(lp);
            mQALinear.addView(btn);
            //设置监听提交
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "submit............");
                    mSubmitResult.submitTaskResult();
                }
            });
        }
    }
    public String getResult(){
        String ans = "";
        Log.i(TAG, qaList.size()+"");
        for(int i=0; i<qaList.size(); i++){
            ans+= "|q"+(i+1);
            String itemAns="";
            //getans同意返回若干（“&”+答案）的格式
            switch (task.getFields().getType()){
                case TYPES_SINGLE:{
                    itemAns = ((ItemSingleTask)mQALinear.getChildAt(i)).getAns();
                    break;
                }
                case TYPES_MULTI:{
                    itemAns = ((ItemMultiTask)mQALinear.getChildAt(i)).getAns();
                    break;
                }
                case TYPES_QA:{
                    itemAns = ((ItemQATask)mQALinear.getChildAt(i)).getAns();
                    break;
                }
            }
            if(itemAns.equals("&")){
               return "error:"+(i+1);

            }
            Log.i(TAG,itemAns);
            ans += itemAns;
        }
        return ans;
    }

    //提交任务的接口
    public interface SubmitTaskResultInterface{
        public void submitTaskResult();
    }
    SubmitTaskResultInterface mSubmitResult;
    public void setSubmitTaskResultInterface(SubmitTaskResultInterface inter){
        mSubmitResult = inter;
    }

    //网上搜来的加载视频缩略图的方法
    private void loadVideoScreenshot(final Context context, String uri, ImageView imageView, long frameTimeMicros) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        Glide.with(context)
                .load(uri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(getContext(),"加载失败，点击重试",Toast.LENGTH_SHORT).show();
                        mLoadFail = true;
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .apply(requestOptions)
                .into(imageView);
    }
    //加载音频
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("ua")).
                createMediaSource(uri);
    }
    //缩放图片
    private void scaleImag(String mPath){
        Glide.with(this)
                .load(mPath)
                .centerInside()
//                        .override(650,650)
                .into(mScaleImg);
        mScaleImg.setVisibility(View.VISIBLE);
        //获取img1的信息
        Info mRectF = PhotoView.getImageViewInfo(mSubImg);
        //让img2从img1的位置变换到他本身的位置
        mScaleImg.animaFrom(mRectF);
        DoTaskActivity.setInfo(mRectF);
    }


    //
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "i'm on Resume!!!");
        //TODO:待商榷：现在的做法是onPause暂停视音频播放，但onResume并不恢复。
    }

    // 停止播放
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "i'm on pause....");
        //暂停视频
        if(task.getFields().getTemplate()==TEMPLATES_VIDEO)
            Jzvd.goOnPlayOnPause();
        //暂停音频
        else if(task.getFields().getTemplate() == TEMPLATES_AUDIO){
            SimpleExoPlayer player = (SimpleExoPlayer)mSubAudio.getPlayer();
            if(player!=null) player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //释放音频资源
        SimpleExoPlayer player = (SimpleExoPlayer)mSubAudio.getPlayer();
        if (player != null) {
            player.release();
        }
    }

    //点击返回键
    void onBackPressed() {
        //如果音视频还在播放
        SimpleExoPlayer player = (SimpleExoPlayer)mSubAudio.getPlayer();
        if (player != null) {
            player.release();
        }
        if (Jzvd.backPress()) {
            return;
        }
    }
}
