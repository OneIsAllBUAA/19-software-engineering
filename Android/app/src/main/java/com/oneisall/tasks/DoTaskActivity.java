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
import com.oneisall.model.request.SubmitTaskRequest;
import com.oneisall.model.response.EnterTaskRequestResult;
import com.oneisall.model.response.SingleMessageResponse;
import com.oneisall.model.response.Task;
import com.oneisall.utils.DialogUtils;
import com.oneisall.utils.UserUtils;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.oneisall.api.TaskApi.submitTask;

//import android.support.v7.app.AppCompatActivity;

//import android.support.v7.widget.RecyclerView;

//import android.view.View;

//import android.support.v7.widget.LinearLayoutManager;


/**
 * 需添加网络应答为空的验证机制
 */
public class DoTaskActivity extends SwipeBackActivity implements OnTabSelectListener {

    private static final String TAG = "DoTaskActivity";
    private EnterTaskRequestResult result;
    private Task task;
    private SwipeBackLayout mSwipeBackLayout;

    //
    @BindView(R.id.do_task_title) TextView mTaskName;
    @BindView(R.id.tab_4_subtasks) SlidingTabLayout mTabs;
    @BindView(R.id.subtask_viewpager) ViewPager mViewPager;
    @BindView(R.id.do_task_back_button) ImageView mBack;
    @BindView(R.id.scale_img) PhotoView mScaleImg;
    private static Info mRectF;
    //
    private MyPagerAdapter mAdapter;
    private List<String> mTitles = new ArrayList<>();
    private List<DoSubTaskFragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        ButterKnife.bind(this);
        mSwipeBackLayout = getSwipeBackLayout();
        Intent intent = getIntent();
        result = (EnterTaskRequestResult) intent.getSerializableExtra("result");
        task = (Task)intent.getSerializableExtra("task");
        //标题过长滚动
//        mTaskName.setMovementMethod(new ScrollingMovementMethod());
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
            mTitles.add("子任务"+(i+1));
            mFragments.add(DoSubTaskFragment.getInstance(result.getSubTasks().get(i),result.getQa_list(),task,mScaleImg,i+1==result.getSubTasks().size(),this));
        }
        //提交答案
        (mFragments.get(result.getSubTasks().size()-1)).setSubmitTaskResultInterface(new DoSubTaskFragment.SubmitTaskResultInterface() {
            @Override
            public void submitTaskResult() {
                List<String> taskResult = new ArrayList<>();
                for(int i=0; i<result.getSubTasks().size(); i++){
                    String tmp = mFragments.get(i).getResult();
                    if(tmp.matches("error:\\d+")){
                        String[] ms = tmp.split(":");
                        DialogUtils.showDialog2s("[子任务"+(i+1)+"]\t第"+ms[1]+"题答案不能为空！", QMUITipDialog.Builder.ICON_TYPE_FAIL, DoTaskActivity.this, mViewPager);
                        return ;
                    }
                    taskResult.add(tmp);
                }
                new SubmitTask(DoTaskActivity.this)
                        .execute(new SubmitTaskRequest(UserUtils.getUserName(DoTaskActivity.this),task.getPk(),taskResult));
            }
        });
        //设置view pager的适配器
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
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
                    DialogUtils.showDialogFinish(result.getMessage(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, DoTaskActivity.this, mViewPager);

                }else
                    DialogUtils.showDialog(result.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, DoTaskActivity.this, mViewPager);
            }else{
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, DoTaskActivity.this, mViewPager);
            }
        }
    }

}
