package com.qmuiteam.qmuidemo.fragment.task;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
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
import androidx.core.content.ContextCompat;
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
                for(EnterTaskRequestResult.QA qa : taskDetail.getQa_list()){
                    QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext());
                    section.setTitle("问题" + Integer.toString(index++)+": "+qa.getQuestion());
                    for(String answer : qa.getAnswers()){
                        QMUICommonListItemView item = ((QMUIGroupListView)typeView).createItemView(
                                null,
                                answer,
                                null,
                                QMUICommonListItemView.HORIZONTAL,
                                QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
                        section.addItemView(item, v->{
                            item.toggleSwitch();
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
                for(EnterTaskRequestResult.QA qa : taskDetail.getQa_list()){
                    TextView textView = new TextView(getContext());
                    textView.setGravity(Gravity.CENTER);
                    textView.setPadding(0,50,0,30);
                    textView.setText(qa.getQuestion());
                    EditText editText = new EditText(getContext());
                    editText.setPadding(100,0,100,0);
                    editText.setGravity(Gravity.CENTER);
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
            new SubmitTask(getContext()).execute(new SubmitTaskRequest(UserUtils.getUserName(getContext()), subTask.getTask(),null));
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
                showDialog(result.getMessage(), QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mContentViewPager);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mContentViewPager);
            }
        }
    }
}
