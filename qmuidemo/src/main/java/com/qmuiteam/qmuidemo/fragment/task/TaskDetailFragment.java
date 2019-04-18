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
import com.qmuiteam.qmuidemo.model.request.EnterTaskRequest;
import com.qmuiteam.qmuidemo.model.response.EnterTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.TaskApi.enterTask;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTemplateName;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTypeName;
import static com.qmuiteam.qmuidemo.utils.DialogUtils.showDialog;

public class TaskDetailFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.task_detail_list) QMUIGroupListView mGroupListView;
    @BindView(R.id.task_detail_enter) QMUIRoundButton mEnterButton;

    private Task task;
    private static final String TAG = "TaskDetailFragment";
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
        mEnterButton.setOnClickListener(v -> getSubTasksAndStartDoing());
    }

    private void getSubTasksAndStartDoing(){
        new GetSubTasks(getContext()).execute(new EnterTaskRequest(1));
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
}
