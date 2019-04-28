package com.qmuiteam.qmuidemo.fragment.home;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseAsyncTask;
import com.qmuiteam.qmuidemo.base.BaseFragment;
import com.qmuiteam.qmuidemo.fragment.OneIsAllAboutFragment;
import com.qmuiteam.qmuidemo.fragment.task.TaskDetailFragment;
import com.qmuiteam.qmuidemo.model.request.AllTasksRequest;
import com.qmuiteam.qmuidemo.model.request.MyTaskRequest;
import com.qmuiteam.qmuidemo.model.response.Fields;
import com.qmuiteam.qmuidemo.model.response.MyTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.Task;
import com.qmuiteam.qmuidemo.model.response.TaskListResult;
import com.qmuiteam.qmuidemo.utils.UserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.TaskApi.getAllTasks;
import static com.qmuiteam.qmuidemo.api.TaskApi.getMyTask;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTemplateName;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTypeName;
import static com.qmuiteam.qmuidemo.utils.DialogUtils.showDialog;

public class MyTaskController extends QMUIWindowInsetLayout {

    @BindView(R.id.my_task_topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.my_task_groupListView) QMUIGroupListView mGroupListView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;

    private HomeController.HomeControlListener mHomeControlListener;
    private int mDiffRecyclerViewSaveStateId = QMUIViewHelper.generateViewId();
    private Context context;
    private static final String TAG = "MyTaskController";

    public MyTaskController(Context context){
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.my_task_layout, this);
        ButterKnife.bind(this);
        initListener();
        initTopBar();
        initData();
    }

    private void initTopBar() {
        mTopBar.setTitle(getTitle());
        mTopBar.addRightImageButton(R.mipmap.icon_topbar_about, R.id.topbar_right_about_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OneIsAllAboutFragment fragment = new OneIsAllAboutFragment();
                startFragment(fragment);
            }
        });
    }
    protected String getTitle() {
        return "我的任务";
    }


    public void setHomeControlListener(HomeController.HomeControlListener homeControlListener) {
        mHomeControlListener = homeControlListener;
    }

    protected void startFragment(BaseFragment fragment) {
        if (mHomeControlListener != null) {
            mHomeControlListener.startFragment(fragment);
        }
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        int id = mGroupListView.getId();
        mGroupListView.setId(mDiffRecyclerViewSaveStateId);
        super.dispatchSaveInstanceState(container);
        mGroupListView.setId(id);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        int id = mGroupListView.getId();
        mGroupListView.setId(mDiffRecyclerViewSaveStateId);
        super.dispatchRestoreInstanceState(container);
        mGroupListView.setId(id);
    }

    private void initData(){
        new GetMyTask(context).execute(new MyTaskRequest(UserUtils.getUserName(context)));
    }

    private class GetMyTask extends BaseAsyncTask<MyTaskRequest, Void, MyTaskRequestResult> {

        private GetMyTask(Context context){
            super(context);
        }

        @Override
        protected MyTaskRequestResult doInBackground(MyTaskRequest... requests) {
            return getMyTask(requests[0]);
        }

        @Override
        protected void onPostExecute(MyTaskRequestResult result) {
            super.onPostExecute(result);
            if(result != null){
                Log.i(TAG, "onPostExecute: "+result.toString());
                initGroupListView(result);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mGroupListView);
            }
        }
    }

    private void initGroupListView(MyTaskRequestResult result){

        mGroupListView.removeAllViews();

        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.Section section1 = QMUIGroupListView.newSection(getContext());
        section1.setTitle("我收藏的任务").setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        for(Task task : result.getFavorite()){
            QMUICommonListItemView item = mGroupListView.createItemView(task.getFields().getName());
            item.setDetailText(getTemplateName(task.getFields().getTemplate()) + getTypeName(task.getFields().getType()));
            item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
            section1.addItemView(item, v->{
                TaskDetailFragment taskDetailFragment = new TaskDetailFragment();
                taskDetailFragment.setTask(task);
                startFragment(taskDetailFragment);
            });
        }
        section1.addTo(mGroupListView);

        QMUIGroupListView.Section section2 = QMUIGroupListView.newSection(getContext());
        section2.setTitle("已抢位的任务").setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        for(Task task : result.getGrabbed()){
            QMUICommonListItemView item = mGroupListView.createItemView(task.getFields().getName());
            item.setDetailText(getTemplateName(task.getFields().getTemplate()) + getTypeName(task.getFields().getType()));
            item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
            section2.addItemView(item, v->{
                TaskDetailFragment taskDetailFragment = new TaskDetailFragment();
                taskDetailFragment.setTask(task);
                startFragment(taskDetailFragment);
            });
        }
        section2.addTo(mGroupListView);

        QMUIGroupListView.Section section3 = QMUIGroupListView.newSection(getContext());
        section3.setTitle("我发布的任务").setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        for(Task task : result.getReleased()){
            QMUICommonListItemView item = mGroupListView.createItemView(task.getFields().getName());
            item.setDetailText(getTemplateName(task.getFields().getTemplate()) + getTypeName(task.getFields().getType()));
            item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
            section3.addItemView(item, v->{
                TaskDetailFragment taskDetailFragment = new TaskDetailFragment();
                taskDetailFragment.setTask(task);
                startFragment(taskDetailFragment);
            });
        }
        section3.addTo(mGroupListView);
    }

    private void initListener(){
        mPullRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {
            }

            @Override
            public void onMoveRefreshView(int offset) {
            }
            @Override
            public void onRefresh() {
                mPullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 0);
            }
        });
    }
}
