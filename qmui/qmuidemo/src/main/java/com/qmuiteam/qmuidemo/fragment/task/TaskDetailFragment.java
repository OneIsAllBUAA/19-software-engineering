package com.qmuiteam.qmuidemo.fragment.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseAsyncTask;
import com.qmuiteam.qmuidemo.base.BaseFragment;
import com.qmuiteam.qmuidemo.model.request.CheckTaskRequest;
import com.qmuiteam.qmuidemo.model.request.EnterTaskRequest;
import com.qmuiteam.qmuidemo.model.request.TaskIdAndUsernameRequest;
import com.qmuiteam.qmuidemo.model.response.CheckTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.EnterTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
import com.qmuiteam.qmuidemo.model.response.Task;
import com.qmuiteam.qmuidemo.utils.UserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.TaskApi.checkTask;
import static com.qmuiteam.qmuidemo.api.TaskApi.enterTask;
import static com.qmuiteam.qmuidemo.api.TaskApi.favoriteTask;
import static com.qmuiteam.qmuidemo.api.TaskApi.grabTask;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTemplateName;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTypeName;
import static com.qmuiteam.qmuidemo.utils.DialogUtils.showDialog;

public class TaskDetailFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.task_detail_list) QMUIGroupListView mGroupListView;
    @BindView(R.id.task_detail_favorite) QMUIRoundButton mFavoriteButton;
    @BindView(R.id.task_detail_grab) QMUIRoundButton mGrabButton;
    @BindView(R.id.task_detail_enter) QMUIRoundButton mEnterButton;

    private Task task;
    private static final String TAG = "TaskDetailFragment";
    private int checkFlag=0;
    public void setCheckFlag(){checkFlag=1;}
    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_task_detail, null);
        ButterKnife.bind(this, root);

        initTopBar();
        initGroupListView();
        initListeners();
        return root;
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle(getResources().getString(R.string.task_detail));
    }
    private void initGroupListView(){
        QMUICommonListItemView titleItem = mGroupListView.createItemView(getString(R.string.task_title));
        titleItem.setDetailText(task.getFields().getName());
        QMUICommonListItemView templateItem = mGroupListView.createItemView(getString(R.string.task_template));
        templateItem.setDetailText(getTemplateName(task.getFields().getTemplate()));
        QMUICommonListItemView typeItem = mGroupListView.createItemView(getString(R.string.task_type));
        typeItem.setDetailText(getTypeName(task.getFields().getType()));
        QMUICommonListItemView descriptionItem = mGroupListView.createItemView(getString(R.string.task_description));
        descriptionItem.setDetailText(task.getFields().getDetails());
        QMUICommonListItemView timeItem = mGroupListView.createItemView(getString(R.string.task_time));
        timeItem.setDetailText(task.getFields().getC_time().substring(0, 10));
        QMUICommonListItemView creditItem = mGroupListView.createItemView(getString(R.string.task_credit));
        creditItem.setDetailText(Integer.toString(task.getFields().getCredit()));
        QMUICommonListItemView userLevelItem = mGroupListView.createItemView(getString(R.string.task_user_level));
        userLevelItem.setDetailText(Integer.toString(task.getFields().getUser_level()));
        QMUICommonListItemView maxNumItem = mGroupListView.createItemView(getString(R.string.task_max_num));
        maxNumItem.setDetailText(Integer.toString(task.getFields().getMax_tagged_num()));


        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.newSection(getContext())
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(titleItem, v->{})
                .addItemView(templateItem, v->{})
                .addItemView(typeItem, v->{})
                .addItemView(descriptionItem, v->{})
                .addItemView(timeItem, v->{})
                .addItemView(creditItem, v->{})
                .addItemView(userLevelItem, v->{})
                .addItemView(maxNumItem, v->{})
                .addTo(mGroupListView);
    }

    private void initListeners(){
        mFavoriteButton.setOnClickListener(v-> {new FavoriteTask(getContext()).execute(new TaskIdAndUsernameRequest(task.getPk(), UserUtils.getUserName(getContext())));});
        mGrabButton.setOnClickListener(v-> {new GrabTask(getContext()).execute(new TaskIdAndUsernameRequest(task.getPk(), UserUtils.getUserName(getContext())));});
        mEnterButton.setOnClickListener(v -> getSubTasksAndStartDoing());
    }

    private void getSubTasksAndStartDoing(){
        if(checkFlag==1){
            new GetCheckTask(getContext()).execute(new CheckTaskRequest(task.getPk()));
        }
        else {
            new GetSubTasks(getContext()).execute(new EnterTaskRequest(task.getPk()));
        }
    }

    private class GetSubTasks extends BaseAsyncTask<EnterTaskRequest, Void, EnterTaskRequestResult> {

        private GetSubTasks(Context context){
            super(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected EnterTaskRequestResult doInBackground(EnterTaskRequest... enterTaskRequests) {
            return enterTask(enterTaskRequests[0]);
        }
        @Override
        protected void onPostExecute(EnterTaskRequestResult enterTaskRequestResult) {
            super.onPostExecute(enterTaskRequestResult);
            if(enterTaskRequestResult != null){
                Log.i(TAG, "onPostExecute: "+enterTaskRequestResult.toString());
                DoTaskFragment doTaskFragment = new DoTaskFragment();
                doTaskFragment.setTask(task);
                doTaskFragment.setTaskDetail(enterTaskRequestResult);
                startFragment(doTaskFragment);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, getContext(), mGroupListView);
            }
        }
    }

    private class GrabTask extends BaseAsyncTask<TaskIdAndUsernameRequest, Void, SingleMessageResponse> {
        private GrabTask(Context context){
            super(context);
        }

        @Override
        protected SingleMessageResponse doInBackground(TaskIdAndUsernameRequest... requests) {
            return grabTask(requests[0]);
        }
        @Override
        protected void onPostExecute(SingleMessageResponse response) {
            super.onPostExecute(response);
            if(response!=null){
                showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, getContext(), mGroupListView);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, getContext(), mGroupListView);
            }
        }
    }

    private class FavoriteTask extends BaseAsyncTask<TaskIdAndUsernameRequest, Void, SingleMessageResponse> {
        private FavoriteTask(Context context){
            super(context);
        }
        @Override
        protected SingleMessageResponse doInBackground(TaskIdAndUsernameRequest... requests) {
            return favoriteTask(requests[0]);
        }
        @Override
        protected void onPostExecute(SingleMessageResponse response) {
            super.onPostExecute(response);
            if(response!=null){
                showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, getContext(), mGroupListView);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, getContext(), mGroupListView);
            }
        }
    }

    private class GetCheckTask extends BaseAsyncTask<CheckTaskRequest, Void, CheckTaskRequestResult> {
        private GetCheckTask(Context context){
            super(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected CheckTaskRequestResult doInBackground(CheckTaskRequest... checkTaskRequests) {
            return checkTask(checkTaskRequests[0]);
        }
        @Override
        protected void onPostExecute(CheckTaskRequestResult checkTaskRequestResult) {
            Log.i(TAG, "check is on post execute");
            super.onPostExecute(checkTaskRequestResult);
            if(checkTaskRequestResult != null){
                Log.i(TAG, "onPostExecute: "+checkTaskRequestResult.toString());
                CheckTaskFragment checkTaskFragment = new CheckTaskFragment();
                checkTaskFragment.setTask(task);
                checkTaskFragment.setCTaskDetail(checkTaskRequestResult);
                startFragment(checkTaskFragment);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, getContext(), mGroupListView);
            }
        }
    }
}
