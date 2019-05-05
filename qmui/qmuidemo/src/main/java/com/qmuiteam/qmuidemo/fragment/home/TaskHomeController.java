package com.qmuiteam.qmuidemo.fragment.home;

import android.content.Context;
import android.os.AsyncTask;
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
import com.qmuiteam.qmuidemo.model.response.Fields;
import com.qmuiteam.qmuidemo.model.response.Task;
import com.qmuiteam.qmuidemo.model.response.TaskListResult;
import com.qmuiteam.qmuidemo.utils.UserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.TaskApi.getAllTasks;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTemplateName;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTypeName;
import static com.qmuiteam.qmuidemo.utils.DialogUtils.showDialog;

public class TaskHomeController extends QMUIWindowInsetLayout {

    private static final String TAG = "TaskHomeController";
    private Context context;
    @BindView(R.id.task_home_topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.task_home_groupListView) QMUIGroupListView mGroupListView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;

    private HomeController.HomeControlListener mHomeControlListener;
    private int mDiffRecyclerViewSaveStateId = QMUIViewHelper.generateViewId();

    public TaskHomeController(Context context){
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.task_home_layout, this);
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
        return "任务广场";
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

    private class GetAllTasks extends BaseAsyncTask<AllTasksRequest, Void, TaskListResult> {

        private GetAllTasks(Context context){
            super(context);
        }

        @Override
        protected TaskListResult doInBackground(AllTasksRequest... allTasksRequests) {
            return getAllTasks(allTasksRequests[0]);
        }

        @Override
        protected void onPostExecute(TaskListResult taskListResult) {
            super.onPostExecute(taskListResult);
            if(taskListResult != null){
                Log.i(TAG, "onPostExecute: "+taskListResult.toString());
                initGroupListView(taskListResult);
            }else{
                showDialog("网络错误",QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mGroupListView);
            }
        }
    }
    private void initData(){
        new GetAllTasks(context).execute(new AllTasksRequest(UserUtils.getUserName(context)));
    }
    private void initGroupListView(TaskListResult taskListResult){

        mGroupListView.removeAllViews();

        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext());
        section.setTitle("全部任务").setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);

        for(Task task : taskListResult.getResultArray()){
            Fields fields = task.getFields();
            String name = fields.getName();
            String templateName = getTemplateName(fields.getTemplate());
            String typeName = getTypeName(fields.getType());
            QMUICommonListItemView item = mGroupListView.createItemView(name);
            item.setDetailText(templateName + typeName);
            item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
            section.addItemView(item, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskDetailFragment taskDetailFragment = new TaskDetailFragment();
                    taskDetailFragment.setTask(task);
                    startFragment(taskDetailFragment);
                }
            });
        }
        section.addTo(mGroupListView);
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
