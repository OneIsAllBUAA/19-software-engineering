package com.oneisall.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.oneisall.R;
import com.oneisall.base.BaseAsyncTask;
import com.oneisall.model.request.SubmitCheckResultRequest;
import com.oneisall.model.response.CheckTaskRequestResult;
import com.oneisall.model.response.SingleMessageResponse;
import com.oneisall.model.response.Task;
import com.oneisall.utils.DialogUtils;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.oneisall.api.TaskApi.submitCheckResult;
import static com.oneisall.constants.TaskTypes.TYPES_LABEL;

public class CheckTaskActivity extends SwipeBackActivity implements OnTabSelectListener {
    final private static String TAG = "CheckTaskActivity";

    private CheckTaskRequestResult result;
    private Task task;
    private SwipeBackLayout mSwipeBackLayout;

    //
    @BindView(R.id.do_task_title)
    TextView mTaskName;
    @BindView(R.id.tab_4_subtasks)
    SlidingTabLayout mTabs;
    @BindView(R.id.subtask_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.do_task_back_button)
    ImageView mBack;
    @BindView(R.id.scale_img)
    PhotoView mScaleImg;
    private static Info mRectF;
    //
    private CheckTaskActivity.MyPagerAdapter mAdapter;
    private List<String> mTitles = new ArrayList<>();
    private List<CheckSubTaskFragment> mFragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        ButterKnife.bind(this);
        mSwipeBackLayout = getSwipeBackLayout();
        Intent intent = getIntent();
        result = (CheckTaskRequestResult) intent.getSerializableExtra("result");
        task = (Task)intent.getSerializableExtra("task");
        //还没有成果
        if(result.getSubTasks().size()==0){
            DialogUtils.showDialogFinish("还没有成果！",QMUITipDialog.Builder.ICON_TYPE_FAIL, this, mViewPager);
        }
        //开启图片缩放功能
        mScaleImg.enable();
        mScaleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 让img2从自身位置变换到原来img1图片的位置大小
                mScaleImg.animaTo(mRectF, new Runnable() {
                    @Override
                    public void run() {
                        mScaleImg.setVisibility(View.GONE);
                    }
                });
            }
        });
        //init
        initSubTasks();
        //监听返回
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        getSubTask();
//        initView();
    }
    void initSubTasks(){
        //tab 标题, 对应的view pager中的fragments
        mTaskName.setText(task.getFields().getName());
        for(int i=0; i<result.getSubTasks().size(); i++){
            if(task.getFields().getType()!=TYPES_LABEL) mTitles.add("子任务"+(i+1));
            else mTitles.add("标注"+(i+1));
            mFragments.add(CheckSubTaskFragment.getInstance(result.getSubTasks().get(i),result.getStatistics().get(i).getCheckQa_list(),task,mScaleImg,this));
        }
        //TODO: 提交审核结果
        //设置view pager的适配器
        mAdapter = new CheckTaskActivity.MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        //关联
        mTabs.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        mTabs.setOnTabSelectListener(this);
    }
    public static void setInfo(Info info){
        mRectF = info;
    }
    @Override
    public void onTabSelect(int position) {
//        Toast.makeText(this, "onTabSelect&position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabReselect(int position) {
//        Toast.makeText(this, "onTabReselect&position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Jzvd.resetAllVideos();
    }

    @Override
    public void onBackPressed() {
        //如果当前是放大图片模式，那么返回键的功能就是回到原图界面
        if(mScaleImg.getVisibility()==View.VISIBLE){
            mScaleImg.animaTo(mRectF, new Runnable() {
                @Override
                public void run() {
                    mScaleImg.setVisibility(View.GONE);
                }
            });
            return;
        }
        //否则退出activity
        for(int i=0; i<mFragments.size(); i++){
            mFragments.get(i).onBackPressed();
        }
        super.onBackPressed();
    }


    //
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    //network utils
    class SubmitCheckTask extends BaseAsyncTask<SubmitCheckResultRequest, Void, SingleMessageResponse> {
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
                    DialogUtils.showDialogFinish(result.getMessage(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, context, mViewPager);
                }else
                    DialogUtils.showDialog(result.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, context, mViewPager);
            }else{
                DialogUtils.showDialog("信息提交失败", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mViewPager);
            }
        }
    }
}
