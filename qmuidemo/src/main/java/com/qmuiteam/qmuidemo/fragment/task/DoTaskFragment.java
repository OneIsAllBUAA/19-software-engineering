package com.qmuiteam.qmuidemo.fragment.task;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.tab.QMUITabBuilder;
import com.qmuiteam.qmui.widget.tab.QMUITabIndicator;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseAsyncTask;
import com.qmuiteam.qmuidemo.base.BaseFragment;
import com.qmuiteam.qmuidemo.model.request.SubmitTaskRequest;
import com.qmuiteam.qmuidemo.model.response.EnterTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
import com.qmuiteam.qmuidemo.model.response.SubTask;
import com.qmuiteam.qmuidemo.model.response.Task;
import com.qmuiteam.qmuidemo.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.TaskApi.submitTask;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.*;
import static com.qmuiteam.qmuidemo.constants.UrlConstants.WEBSITE_BASE;
import static com.qmuiteam.qmuidemo.utils.DialogUtils.showDialog;

public class DoTaskFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.tabSegment) QMUITabSegment mTabSegment;
    @BindView(R.id.contentViewPager) ViewPager mContentViewPager;
    
    private int mCurrentItemCount;
    private List<Pair<SubTask, View>> mPageMap = new ArrayList<>();

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private List<List<String>> mAnswers = new ArrayList<>();
    private List<List<List<Boolean>>> mOptions = new ArrayList<>();

    private List<SimpleExoPlayer> mediaPlayers = new ArrayList<>();

    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mCurrentItemCount;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            SubTask subTask = taskDetail.getSubTasks().get(position);
            View view = getPageView(subTask, position);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            View view = (View) object;
            SubTask page = mPageMap.stream().filter(p->p.second.equals(view)).findFirst().get().first;

            int pos = taskDetail.getSubTasks().indexOf(page);
            if (pos >= mCurrentItemCount) {
                return POSITION_NONE;
            }
            return POSITION_UNCHANGED;
        }
    };

    private Task task;
    private EnterTaskRequestResult taskDetail;
    private static final String TAG = "DoTaskFragment";
    
    public void setTask(Task task) {
        this.task = task;
    }
    public void setTaskDetail(EnterTaskRequestResult taskDetail) {
        this.taskDetail = taskDetail;
        this.mCurrentItemCount = taskDetail.getSubTasks().size();
    }

    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tab_viewpager_layout, null);
        ButterKnife.bind(this, rootView);
        initTopBar();
        initTabAndPager();
        return rootView;
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle(this.task.getFields().getName());
    }

    private void initTabAndPager() {
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(0, false);
        QMUITabBuilder tabBuilder = mTabSegment.tabBuilder();
        for (int i = 0; i < mCurrentItemCount; i++) {
            mTabSegment.addTab(tabBuilder.setText("问题" + (i + 1)).build());
            mAnswers.add(new ArrayList<>());
            mOptions.add(new ArrayList<>());
            for(EnterTaskRequestResult.QA qa : taskDetail.getQa_list()){
                mAnswers.get(i).add("");
                mOptions.get(i).add(new ArrayList<>());
                for(String s : qa.getAnswers()){
                    mOptions.get(i).get(mOptions.get(i).size()-1).add(false);
                }
            }
        }
        int space = QMUIDisplayHelper.dp2px(getContext(), 16);
        mTabSegment.setIndicator(new QMUITabIndicator(
                QMUIDisplayHelper.dp2px(getContext(), 2), false, true));
        mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
        mTabSegment.setItemSpaceInScrollMode(space);
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setPadding(space, 0, space, 0);
    }


    private View getPageView(SubTask subTask, int position) {
        View view = mPageMap.stream().filter(p->p.first.equals(subTask)).map(p->p.second).findFirst().orElse(null);
        if (view == null) {
            view = getSubTaskView(subTask, position);
            mPageMap.add(new Pair<>(subTask, view));
        }
        return view;
    }

    private View getSubTaskView(SubTask subTask, int position){
        ScrollView parent = new ScrollView(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20,100,20,150);
        View templateView, typeView;

        switch (task.getFields().getTemplate()){
            case TEMPLATES_PIC:{
                templateView = new ImageView(getContext());

                Log.i(TAG, "getSubTaskView: " + WEBSITE_BASE + subTask.getFile());
                Glide.with(this)
                        .load(WEBSITE_BASE + subTask.getFile())
                        .centerCrop()
                        .override(650,650)
                        .into((ImageView)templateView);
                break;
            }
            case TEMPLATES_AUDIO:
            case TEMPLATES_VIDEO:{
                templateView = new PlayerView(getContext());
                SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(getContext()),new DefaultTrackSelector(),new DefaultLoadControl());
                ((PlayerView) templateView).setPlayer(player);
                if(task.getFields().getTemplate() == TEMPLATES_VIDEO ){
                    templateView.setMinimumWidth(400);
                    templateView.setMinimumHeight(600);
                }
                player.setPlayWhenReady(false);
                player.seekTo(0);
                Log.i(TAG, "getSubTaskView: " + WEBSITE_BASE + subTask.getFile());
                MediaSource mediaSource = buildMediaSource(Uri.parse(WEBSITE_BASE + subTask.getFile()));
                player.prepare(mediaSource, true, false);
                mediaPlayers.add(player);
                break;
            }
            default:{
                templateView = new TextView(getContext());
                ((TextView)templateView).setGravity(Gravity.CENTER);
                ((TextView)templateView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                ((TextView)templateView).setText(subTask.getFile());
            }
        }
        templateView.setPadding(0,0,0,50);
        switch (task.getFields().getType()){
            case TYPES_MULTI:
            case TYPES_SINGLE:{
                typeView = new QMUIGroupListView(getContext());
                int index = 1;
                for(int i=0; i<taskDetail.getQa_list().size();i++){
                    EnterTaskRequestResult.QA qa = taskDetail.getQa_list().get(i);
                    QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext());
                    section.setTitle("问题" + Integer.toString(index++)+": "+qa.getQuestion());
                    for(int j=0; j<qa.getAnswers().size();j++){
                        String answer = qa.getAnswers().get(j);
                        QMUICommonListItemView item = ((QMUIGroupListView)typeView).createItemView(
                                null,
                                answer,
                                null,
                                QMUICommonListItemView.HORIZONTAL,
                                QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
                        final int i2 = i;
                        final int j2 = j;
                        section.addItemView(item, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {item.toggleSwitch();
                                mOptions.get(position).get(i2).set(j2,!mOptions.get(position).get(i2).get(j2));
                            }
                        });
                    }
                    section.addTo((QMUIGroupListView)typeView);
                }
                break;
            }
            case TYPES_QA:{
                typeView = new LinearLayout(getContext());
                ((LinearLayout) typeView).setGravity(Gravity.CENTER);
                ((LinearLayout) typeView).setOrientation(LinearLayout.VERTICAL);
                typeView.setPadding(100,0,100,0);
                for(int i=0; i< taskDetail.getQa_list().size();i++){
                    EnterTaskRequestResult.QA qa = taskDetail.getQa_list().get(i);
                    TextView textView = new TextView(getContext());
                    textView.setGravity(Gravity.CENTER);
                    textView.setPadding(0,50,0,30);
                    textView.setText(qa.getQuestion());
                    EditText editText = new EditText(getContext());
                    editText.setPadding(100,0,100,0);
                    editText.setGravity(Gravity.CENTER);
                    final int i2 = i;
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            mAnswers.get(position).set(i2, s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    ((LinearLayout) typeView).addView(textView);
                    ((LinearLayout) typeView).addView(editText);
                }
                break;
            }
            default:{
                typeView = new TextView(getContext());
                ((TextView)typeView).setGravity(Gravity.CENTER);
                ((TextView)typeView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                ((TextView)typeView).setText(taskDetail.getQa_list().toString());
            }
        }
        QMUIRoundButton buttonView = new QMUIRoundButton(getContext());
        buttonView.setText("提交");
        buttonView.setVisibility(taskDetail.getSubTasks().size()-1 == position? View.VISIBLE : View.GONE);
        buttonView.setOnClickListener(v->{
            List<String> result;
            switch (task.getFields().getType()){
                case TYPES_MULTI:
                case TYPES_SINGLE:{
                    result = getOptionAnswerString();
                    break;
                }
                case TYPES_QA:{
                    result = getTextAnswersString();
                    break;
                }
                default:{
                    result = new ArrayList<>();
                }
            }

            // Log.i(TAG, "getSubTaskView: " + result.get(0));
            new SubmitTask(getContext()).execute(new SubmitTaskRequest(UserUtils.getUserName(getContext()), subTask.getTask(), result));
        });
        buttonView.setPadding(0,50,0,0);
        layout.addView(templateView);
        layout.addView(typeView);
        layout.addView(buttonView);
        parent.addView(layout);
        return parent;
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("ua")).
                createMediaSource(uri);
    }

    private class SubmitTask extends BaseAsyncTask<SubmitTaskRequest, Void, SingleMessageResponse> {

        private SubmitTask(Context context){
            super(context);
        }

        @Override
        protected SingleMessageResponse doInBackground(SubmitTaskRequest... requests) {
            return submitTask(requests[0]);
        }

        @Override
        protected void onPostExecute(SingleMessageResponse result) {
            super.onPostExecute(result);
            if(result != null){
                Log.i(TAG, "onPostExecute: "+result.toString());
                if(result.getMessage().equals("任务已完成")){
                    showDialog(result.getMessage(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, context, mContentViewPager);
                    popBackStack();
                }else
                    showDialog(result.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, context, mContentViewPager);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mContentViewPager);
            }
        }
    }

    private List<String> getTextAnswersString(){
        List<String> result = new ArrayList<>();

        for(List<String> l : mAnswers){
            String s = "";
            for(String a : l){
                s = s + '|' + a;
            }
            result.add(s);
        }
        return result;
    }

    private List<String> getOptionAnswerString(){
        List<String> result = new ArrayList<>();

        for(List<List<Boolean>> subAnswer : mOptions){
            String s = "";
            for(List<Boolean> answer : subAnswer){
                s+="|";
                for(int i=0; i<answer.size();i++){
                    if(answer.get(i)) s = s + "&" + Integer.toString(i+1);
                }
            }
            result.add(s);
        }

        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for(SimpleExoPlayer player : mediaPlayers){
            if (player != null) {
                player.setPlayWhenReady(false);
                player.stop();
                player.seekTo(0);
            }
        }
    }
}
