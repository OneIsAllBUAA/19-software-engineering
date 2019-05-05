package com.qmuiteam.qmuidemo.fragment.task;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
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
import com.qmuiteam.qmuidemo.model.request.SubmitCheckResultRequest;
import com.qmuiteam.qmuidemo.model.response.CheckTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
import com.qmuiteam.qmuidemo.model.response.SubTask;
import com.qmuiteam.qmuidemo.model.response.Task;
import com.qmuiteam.qmuidemo.view.CheckSection;
import com.qmuiteam.qmuidemo.view.check_all_item;
import com.qmuiteam.qmuidemo.view.check_chart_item;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Checksum;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.angmarch.views.NiceSpinner;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.TaskApi.submitCheckResult;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.*;
import static com.qmuiteam.qmuidemo.constants.UrlConstants.CHECK_TASK;
import static com.qmuiteam.qmuidemo.constants.UrlConstants.WEBSITE_BASE;
import static com.qmuiteam.qmuidemo.utils.DialogUtils.showDialog;

public class CheckTaskFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.tabSegment) QMUITabSegment mTabSegment;
    @BindView(R.id.contentViewPager) ViewPager mContentViewPager;
    @BindView(R.id.nice_spinner) NiceSpinner niceSpinner;
    @BindView(R.id.threshold_pass_button) QMUIRoundButton mPassBtn;

    private int mCurrentItemCount;
    private List<Pair<SubTask, View>> mPageMap = new ArrayList<>();

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

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
            SubTask subTask = cTaskDetail.getSubTasks().get(position);
            CheckTaskRequestResult.Statistic statistic = cTaskDetail.getStatistics().get(position);
            Log.i(TAG, subTask.toString()+" - "+statistic.toString());
            View view = getPageView(subTask, statistic, position);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            //button
            mPassBtn.setOnClickListener(v->{
                double threshold = get_threshold(niceSpinner.getText().toString());
                //intersection logic
                if(task.getFields().getType()==TYPES_SINGLE){
                    passSingleByThreshold(threshold);
                }
                else if(task.getFields().getType()==TYPES_MULTI){
                    passMultiByThreshold( threshold);
                }
                else if(task.getFields().getType()==TYPES_QA){
                    passAll();
                }
//                Toast.makeText(getContext(), threshold+"", Toast.LENGTH_SHORT).show();
            });
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

            int pos = cTaskDetail.getSubTasks().indexOf(page);
            if (pos >= mCurrentItemCount) {
                return POSITION_NONE;
            }
            return POSITION_UNCHANGED;
        }
    };

    private Task task;
    private CheckTaskRequestResult cTaskDetail;
    private static final String TAG = "CheckTaskFragment";

    public void setTask(Task task) {
        this.task = task;
    }
    public void setCTaskDetail(CheckTaskRequestResult cTaskDetail) {
        this.cTaskDetail = cTaskDetail;
        this.mCurrentItemCount = cTaskDetail.getSubTasks().size();
    }

    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tab_viewpager_layout, null);
        ButterKnife.bind(this, rootView);
        initTopBar();
        initTabAndPager();
        //spinner
        List<String> dataset = new LinkedList<>(Arrays.asList("0%", "20%", "40%", "60%", "80%", "100%"));
        niceSpinner.attachDataSource(dataset);
//        Log.i(TAG, cTaskDetail.toString());
        if(task.getFields().getType()==TYPES_SINGLE || task.getFields().getType()==TYPES_MULTI){
            ((TextView)rootView.findViewById(R.id.pass_divider)).setVisibility(View.VISIBLE);
            ((FrameLayout)rootView.findViewById(R.id.pass_frame)).setVisibility(View.VISIBLE);
        }
        else if(task.getFields().getType()==TYPES_QA){
            ((TextView)rootView.findViewById(R.id.pass_divider)).setVisibility(View.VISIBLE);
            ((FrameLayout)rootView.findViewById(R.id.pass_frame)).setVisibility(View.VISIBLE);
            niceSpinner.setVisibility(View.GONE);
            mPassBtn.setText("  全部通过  ");
        }
        return rootView;
    }
    private void initTopBar() {
        Log.i(TAG, this.task+"");
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle(this.task.getFields().getName());
    }

    private void initTabAndPager() {

        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(0, false);
        QMUITabBuilder tabBuilder = mTabSegment.tabBuilder();
        for (int i = 0; i < mCurrentItemCount; i++) {
            if(task.getFields().getType()!=TYPES_LABEL){
                mTabSegment.addTab(tabBuilder.setText("问题" + (i + 1)).build());
            }
            else{
                mTabSegment.addTab(tabBuilder.setText("标注" + (i + 1)).build());
            }
            mOptions.add(new ArrayList<>());
            //question, answers
//            for(CheckTaskRequestResult.CheckQA qa : cTaskDetail.getStatistics().get(position).getCheckQa_list()){
//                mOptions.get(i).add(new ArrayList<>());
//                for(CheckTaskRequestResult.AnswerDetail answerDetail: qa.getAnswers()){
//                    mOptions.get(i).get(mOptions.get(i).size()-1).add(false);
//                }
//            }
        }
        int space = QMUIDisplayHelper.dp2px(getContext(), 16);
        mTabSegment.setIndicator(new QMUITabIndicator(
                QMUIDisplayHelper.dp2px(getContext(), 2), false, true));
        mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
        mTabSegment.setItemSpaceInScrollMode(space);
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setPadding(space, 0, space, 0);
    }


    private View getPageView(SubTask subTask, CheckTaskRequestResult.Statistic statistic, int position) {
        View view = mPageMap.stream().filter(p->p.first.equals(subTask)).map(p->p.second).findFirst().orElse(null);
        if (view == null) {
            view = getSubTaskView(subTask,statistic, position);
            mPageMap.add(new Pair<>(subTask, view));
        }
        return view;
    }

    private View getSubTaskView(SubTask subTask, CheckTaskRequestResult.Statistic statistic, int position){
        ScrollView parent = new ScrollView(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20,50,20,150);
        View templateView, typeView;

        switch (task.getFields().getTemplate()){
            case TEMPLATES_PIC:{
                templateView = new ImageView(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(650, ViewGroup.LayoutParams.WRAP_CONTENT);
                templateView.setLayoutParams(lp);
                Log.i(TAG, "getSubTaskView: " + WEBSITE_BASE + subTask.getFile());
                Glide.with(this)
                        .load(WEBSITE_BASE + subTask.getFile())
                        .fitCenter()
//                        .override(650,350)
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
        templateView.setPadding(0,0,0,20);
        layout.addView(templateView);
        //
        List<CheckTaskRequestResult.CheckQA> cqa_list = statistic.getCheckQa_list();
        // check ui
        int qsize = cqa_list.size();
        int index = 1;
        for(int i = 0; i< qsize; i++){
            CheckTaskRequestResult.CheckQA cqa = cqa_list.get(i);
            CheckSection section = new CheckSection(getContext(), get_init_state());
            section.setmQuestionText(Integer.toString(index++)+". "+cqa.getQuestion());
            if(task.getFields().getType()==TYPES_LABEL) section.setQGone();
            //chart view
            for(int j=0; j<cqa.getAnswers().size();j++) {
                //第index个问题的第j个选项及其统计结果
                CheckTaskRequestResult.AnswerDetail answer = cqa.getAnswers().get(j);
                check_chart_item cItem = new check_chart_item(getContext());
                cItem.setAnsId(j);
                cItem.setAnsText(answer.getAnswer());
                cItem.setPartNum(answer.getVote_num());
                cItem.setProgressValue((int) (answer.getProportion() * 100));
                cItem.setPadding(0, 0, 0, 2);
                //选项统计报告详情
                cItem.setDetailClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSimpleBottomSheetList(answer.getUser_list(), answer.getAccept_num_list());
                    }
                });
                section.addView(cItem);
            }
            //all answers-detail
            for(int j=0; j<cqa.getDetails().size(); j++){
                //第index个问题的 不同用户的 答案
                CheckTaskRequestResult.UserAnswer uans = cqa.getDetails().get(j);
                check_all_item cItem = new check_all_item(getContext(), uans.getLabel_id());
                cItem.setAuthorName(uans.getUserName());
                for(String k: uans.getUser_answer()){
                    cItem.addAnswer(k);
                }
                cItem.setAnsState(uans.getState());
                cItem.setSubmitInterface(new check_all_item.SubmitInterface() {
                    @Override
                    public void passThis(int id) {
                        SubmitCheckTask tmp = new SubmitCheckTask(getContext());
                        tmp.setNotBack(true);
//                        Toast.makeText(getContext(),id+"",Toast.LENGTH_SHORT).show();
                        tmp.execute(new SubmitCheckResultRequest(new ArrayList<>(Arrays.asList(id)),new ArrayList<>()));
                    }

                    @Override
                    public void rejectThis(int id) {
                        SubmitCheckTask tmp = new SubmitCheckTask(getContext());
                        tmp.setNotBack(true);
                        tmp.execute(new SubmitCheckResultRequest(new ArrayList<>(),new ArrayList<>(Arrays.asList(id))));
                    }
                });
                cItem.setPadding(0,0,0,2);
                section.addView(cItem);
            }
            //display
            section.initSectionViews();
            section.setPadding(30,5,20,10);
            layout.addView(section);
        }

        parent.addView(layout);
        return parent;
    }
    private void showSimpleBottomSheetList(List<String> detail, List<Integer> num) {
        QMUIBottomSheet.BottomListSheetBuilder tmp = new QMUIBottomSheet.BottomListSheetBuilder(getActivity());
        for(int i=0; i<detail.size(); i++){
            tmp.addItem(detail.get(i),"已通过"+num.get(i)+"条任务");
        }
        if(detail.size()==0){
            tmp.addItem("无人投票！","");
        }
        QMUIBottomSheet bs = tmp.build();
        bs.show();
    }
    private int get_init_state(){
        int type = task.getFields().getType();
        if(type ==TYPES_SINGLE || type == TYPES_MULTI) return CheckSection.OPT_CHART;
        else if(type == TYPES_QA || type == TYPES_LABEL) return CheckSection.OPT_ALL;
        else return CheckSection.NONE;
    }
    private double get_threshold(String s){
        if(s.equals("0%")) return 0;
        if(s.equals("20%")) return 0.2;
        if(s.equals("40%")) return 0.4;
        if(s.equals("60%")) return 0.6;
        if(s.equals("80%")) return 0.8;
        return 1;
    }
    private void passSingleByThreshold(double threshold){
        List<CheckTaskRequestResult.CheckQA> cqa_list = cTaskDetail.getStatistics().get(mContentViewPager.getCurrentItem()).getCheckQa_list();
        List<Integer> accept_list = new ArrayList<>();
        for(int i=0; i<cqa_list.size(); i++){
            List<CheckTaskRequestResult.AnswerDetail> ans = cqa_list.get(i).getAnswers();
            for(int j=0; j<ans.size(); j++){
                if(ans.get(j).getProportion() >= threshold)
                    accept_list.addAll(ans.get(j).getLabel_list());
            }
        }
        new SubmitCheckTask(getContext()).execute(new SubmitCheckResultRequest(accept_list,new ArrayList<>()));
    }
    private void passMultiByThreshold(double threshold){
        List<CheckTaskRequestResult.CheckQA> cqa_list = cTaskDetail.getStatistics().get(mContentViewPager.getCurrentItem()).getCheckQa_list();
        List<Integer> id_list = new ArrayList<>();
        List<Integer> accept_list = new ArrayList<>();
        int flag = 1;
        for(int i=0; i<cqa_list.size(); i++){
            List<CheckTaskRequestResult.AnswerDetail> ans = cqa_list.get(i).getAnswers();
            for(int j=0; j<ans.size(); j++){
                id_list.addAll(ans.get(j).getLabel_list());
            }
        }
        id_list = new ArrayList<Integer>(new LinkedHashSet<>(id_list));
        for(Integer id: id_list){
            flag = 1;
            for(int i=0; i<cqa_list.size(); i++){
                List<CheckTaskRequestResult.AnswerDetail> ans = cqa_list.get(i).getAnswers();
                for(int j=0; j<ans.size(); j++){
                    if(ans.get(j).getProportion() < threshold && ans.get(j).getLabel_list().contains(id)){
                        flag = 0;
                        break;
                    }
                }
                if(flag == 0) break;
            }
            if(flag == 1) accept_list.add(id);
        }
        new SubmitCheckTask(getContext()).execute(new SubmitCheckResultRequest(accept_list,new ArrayList<>()));
    }
    private void passAll(){
        List<CheckTaskRequestResult.CheckQA> cqa_list = cTaskDetail.getStatistics().get(mContentViewPager.getCurrentItem()).getCheckQa_list();
        List<Integer> accept_list=new ArrayList<>();
        for(int i=0; i<cqa_list.size(); i++){
            for(CheckTaskRequestResult.UserAnswer uans: cqa_list.get(i).getDetails()){
                accept_list.add(uans.getLabel_id());
            }
        }
        new SubmitCheckTask(getContext()).execute(new SubmitCheckResultRequest(accept_list,new ArrayList<>()));

    }
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("ua")).
                createMediaSource(uri);
    }

    private class SubmitCheckTask extends BaseAsyncTask<SubmitCheckResultRequest, Void, SingleMessageResponse> {
        boolean notBack=false;

        public void setNotBack(boolean notBack) {
            this.notBack = notBack;
        }

        private SubmitCheckTask(Context context){
            super(context);
        }

        @Override
        protected SingleMessageResponse doInBackground(SubmitCheckResultRequest... requests) {
            return submitCheckResult(requests[0]);
        }

        @Override
        protected void onPostExecute(SingleMessageResponse result) {
            super.onPostExecute(result);
            if(result != null){
                Log.i(TAG, "onPostExecute: "+result.toString());
                if(result.getMessage().equals("审核信息提交成功")){
                    showDialog(result.getMessage(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, context, mContentViewPager);
                    if(!notBack)popBackStack();
                }else
                    showDialog(result.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, context, mContentViewPager);
            }else{
                showDialog("信息提交失败", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mContentViewPager);
            }
        }
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
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if(!hidden){
//
//        }
//    }
}
