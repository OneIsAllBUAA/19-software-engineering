package com.oneisall.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.oneisall.R;
import com.oneisall.base.BaseAsyncTask;
import com.oneisall.model.request.CheckTaskRequest;
import com.oneisall.model.request.EnterTaskRequest;
import com.oneisall.model.request.TaskIdAndUsernameRequest;
import com.oneisall.model.request.TaskUserRequest;
import com.oneisall.model.response.CheckTaskRequestResult;
import com.oneisall.model.response.EnterTaskRequestResult;
import com.oneisall.model.response.SingleMessageResponse;
import com.oneisall.model.response.Task;
import com.oneisall.model.response.TaskUserRequestResponse;
import com.oneisall.utils.DialogUtils;
import com.oneisall.utils.UserUtils;
import com.oneisall.views.custom.ItemTaskView;
import com.oneisall.views.custom.TaskAttributeView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vondear.rxui.view.dialog.RxDialogSure;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.oneisall.api.TaskApi.checkTask;
import static com.oneisall.api.TaskApi.enterTask;
import static com.oneisall.api.TaskApi.favoriteTask;
import static com.oneisall.api.TaskApi.getTaskUser;
import static com.oneisall.api.TaskApi.grabTask;
import static com.oneisall.api.TaskApi.undoFavorite;
import static com.oneisall.api.TaskApi.undoGrab;
import static com.oneisall.constants.TaskTypes.TYPES_LABEL;
import static com.oneisall.constants.TaskTypes.getTemplateName;
import static com.oneisall.constants.TaskTypes.getTypeName;

public class TaskDetailActivity extends SwipeBackActivity {
    final private static String TAG = "TaskDetailActivity";

    private boolean taskLoading=true;
    private boolean forCheck = false;
    private boolean canEnter = true;
    private Task task;
    private TaskUserRequestResponse taskUser;
    private SwipeBackLayout mSwipeBackLayout;
    @BindView(R.id.task_detail_linear) LinearLayout mTaskLinear;
    @BindView(R.id.favorite_this_task_button) ImageView mFavImg;
    @BindView(R.id.favorite_text) TextView mFavText;
    @BindView(R.id.grab_this_task_button) ImageView mGrabImg;
    @BindView(R.id.grab_text) TextView mGrabText;
    @BindView(R.id.enter_task_button) TextView mEnterBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        task = (Task)intent.getSerializableExtra("task");
        Log.i(TAG, task.toString());
        forCheck = intent.getBooleanExtra("forCheck",false);
        //滑动返回
        mSwipeBackLayout = getSwipeBackLayout();
        //
        initUI();
    }
    private void initUI(){
        //任务条
        ItemTaskView taskItem = new ItemTaskView(this, task);
        taskItem.setBottomDividerVisibilty(View.GONE);
        //人员要求
        TaskAttributeView workerAttr = new TaskAttributeView(this);
        workerAttr.setAttrName(R.string.worker_requirements_note);
        workerAttr.setmAttrText(task.getFields().getUser_level());
        workerAttr.setMarginTop(true);
        //任务类型
        TaskAttributeView typeAttr = new TaskAttributeView(this);
        typeAttr.setAttrName(R.string.task_type_note);
        typeAttr.setmAttrText(getTemplateName(task.getFields().getTemplate())+getTypeName(task.getFields().getType()));
        typeAttr.setMarginTop(true);
        //发布时间
        TaskAttributeView dateAttr = new TaskAttributeView(this);
        dateAttr.setAttrName(R.string.task_date_note);
        dateAttr.setmAttrText(task.getFields().getC_time().substring(0, 10));
        //任务要求
        TaskAttributeView detailAttr = new TaskAttributeView(this);
        detailAttr.setAttrName(R.string.task_detail_note);
        detailAttr.setmAttrText(task.getFields().getDetails());
        //任务要求
        TaskAttributeView stateAttr = new TaskAttributeView(this);
        stateAttr.setAttrName(R.string.task_state_note);
        stateAttr.setmAttrText(task.getFields().isIs_closed()?"已关闭":"开放");
        //TODO: 添加附件下载区，这个setMarginTop(true)

        //加入父布局
        mTaskLinear.addView(taskItem);
        mTaskLinear.addView(workerAttr);
        mTaskLinear.addView(typeAttr);
        mTaskLinear.addView(dateAttr);
        mTaskLinear.addView(detailAttr);
        mTaskLinear.addView(stateAttr);

        //设置控件状态
        //进入、收藏与抢位按钮在xml里初始化，获得网络响应后再更改
        new GetTaskUser(this).execute(new TaskUserRequest(UserUtils.getUserName(this),task.getPk()));

        //设置下拉刷新
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout_person_info);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mEnterBtn.setText(R.string.task_loading_status);
                mEnterBtn.setBackground(getResources().getDrawable(R.drawable.forbid_enter_task_border_view));
                taskLoading = true;
                new GetTaskUser(TaskDetailActivity.this).execute(new TaskUserRequest(UserUtils.getUserName(TaskDetailActivity.this),task.getPk()));
                refreshLayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
            }
        });

    }

    private void setEnterButtonState(TaskUserRequestResponse response){
        //审核界面
        if(forCheck){
            if(response.getStatus()==2){
                mEnterBtn.setText(R.string.task_checking);
                mEnterBtn.setBackground(getResources().getDrawable(R.drawable.forbid_enter_task_border_view));
                canEnter = false;
            }
            //accepted说明审核已完成
            else if(response.getStatus()==3){
                mEnterBtn.setText(R.string.task_check_done);
                mEnterBtn.setBackground(getResources().getDrawable(R.drawable.forbid_enter_task_border_view));
                canEnter = false;
            }
            mEnterBtn.setText(R.string.task_go_check);
            mEnterBtn.setBackground(getResources().getDrawable(R.drawable.enter_task_border_view));
            canEnter = true;
        }
        //做过该任务
        //status的含义卸载api接口函数里了，可以抽象成常量
        else if(response.getStatus()==1){
            mEnterBtn.setText(R.string.task_redo_enter);
            mEnterBtn.setBackground(getResources().getDrawable(R.drawable.enter_task_border_view));
            canEnter = true;
        }
        else if(response.getStatus()==2){
            mEnterBtn.setText(R.string.task_checking);
            mEnterBtn.setBackground(getResources().getDrawable(R.drawable.forbid_enter_task_border_view));
            canEnter = false;
        }
        else if(response.getStatus()==3){
            mEnterBtn.setText(R.string.task_done_enter);
            mEnterBtn.setBackground(getResources().getDrawable(R.drawable.forbid_enter_task_border_view));
            canEnter = false;
        }
        //进入任务按钮
        else if(task.getFields().isIs_closed()){
            mEnterBtn.setText(getResources().getString(R.string.task_closed));
            mEnterBtn.setBackground(getResources().getDrawable(R.drawable.forbid_enter_task_border_view));
            canEnter = false;
        }
        else if(task.getFields().getNum_worker()>=task.getFields().getMax_tagged_num()){
            mEnterBtn.setText(getResources().getString(R.string.task_full_and_forbid_enter));
            mEnterBtn.setBackground(getResources().getDrawable(R.drawable.forbid_enter_task_border_view));
            canEnter = false;
        }
        else{
            mEnterBtn.setText(getResources().getString(R.string.task_enter_text));
            mEnterBtn.setBackground(getResources().getDrawable(R.drawable.enter_task_border_view));
            canEnter = true;
        }
        //刷新numworker
        ((ItemTaskView)mTaskLinear.getChildAt(0)).setTaskCapa(response.getNum_worker(), task.getFields().getMax_tagged_num());
        //加载完成
        taskLoading = false;
    }

    @OnClick({R.id.task_detail_back_button,R.id.favorite_linear,R.id.grab_linear,R.id.enter_task_button})
    public void onClick(View v){
        //设置控件监听器
        switch (v.getId()){
            case R.id.task_detail_back_button:{
//                Toast.makeText(this,"back",Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
            case R.id.favorite_linear:{
                //获取到用户关于此任务的状态：是否收藏，是否抢位
                if(taskUser.getIsFavorite()==0){
                    new FavoriteTask(this).execute(new TaskIdAndUsernameRequest(task.getPk(),UserUtils.getUserName(this)));
                }
                else{
                    //提示弹窗
                    final RxDialogSure rxDialogSure = new RxDialogSure(TaskDetailActivity.this);
                    rxDialogSure.getTitleView().setVisibility(View.GONE);
                    TextView content = rxDialogSure.getContentView();
                    content.setTextSize(16);
                    content.setTextColor(getResources().getColor(R.color.qmui_config_color_75_pure_black));
                    rxDialogSure.setContent("取消收藏?");
                    rxDialogSure.getSureView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new UndoFavorite(TaskDetailActivity.this).execute(new TaskIdAndUsernameRequest(task.getPk(),UserUtils.getUserName(TaskDetailActivity.this)));
                            rxDialogSure.cancel();
                        }
                    });
                    rxDialogSure.show();
                }
//                Toast.makeText(this,"已收藏",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.grab_linear:{
                //
                if(taskUser.getIsGrab()>1) return;
                if(taskUser.getIsGrab()==0){
                    new GrabTask(this).execute(new TaskIdAndUsernameRequest(task.getPk(),UserUtils.getUserName(this)));
                }
                else{
                    //提示弹窗
                    final RxDialogSure rxDialogSure = new RxDialogSure(TaskDetailActivity.this);
                    rxDialogSure.getTitleView().setVisibility(View.GONE);
                    TextView content = rxDialogSure.getContentView();
                    content.setTextSize(16);
                    content.setTextColor(getResources().getColor(R.color.qmui_config_color_75_pure_black));
                    rxDialogSure.setContent("放弃抢位?");
                    rxDialogSure.getSureView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new UndoGrab(TaskDetailActivity.this).execute(new TaskIdAndUsernameRequest(task.getPk(),UserUtils.getUserName(TaskDetailActivity.this)));
                            rxDialogSure.cancel();
                        }
                    });
                    rxDialogSure.show();
                }
//                Toast.makeText(this,"grab",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.enter_task_button:{
                //如果加载中
                if(taskLoading){
                    Toast.makeText(this,getResources().getString(R.string.task_loading_status),Toast.LENGTH_SHORT).show();
                    return ;
                }
                //如果是灰色按钮,不能进入的话
                if(!canEnter){
                    Toast.makeText(this,mEnterBtn.getText(),Toast.LENGTH_SHORT).show();
                }
                //如果是审核人员(审核人员可以在app端审核标注类任务）
                else if(forCheck){
                    //进入审核界面
                    new GetCheckTask(this).execute(new CheckTaskRequest(task.getPk()));
                }
                //如果是标注类任务
                else if(task.getFields().getType()==TYPES_LABEL){
                    //提示弹窗
                    final RxDialogSure rxDialogSure = new RxDialogSure(TaskDetailActivity.this);
                    rxDialogSure.getTitleView().setVisibility(View.GONE);
                    TextView content = rxDialogSure.getContentView();
                    content.setTextSize(16);
                    content.setTextColor(getResources().getColor(R.color.qmui_config_color_75_pure_black));
                    rxDialogSure.setContent("标注类任务只能在PC端完成！");
                    rxDialogSure.getSureView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rxDialogSure.cancel();
                        }
                    });
                    rxDialogSure.show();
                }
                else if(task.getFields().getUser_level() > UserUtils.getUserLevel(TaskDetailActivity.this)){
                    //提示弹窗
                    final RxDialogSure rxDialogSure = new RxDialogSure(TaskDetailActivity.this);
                    rxDialogSure.getTitleView().setVisibility(View.GONE);
                    TextView content = rxDialogSure.getContentView();
                    content.setTextSize(16);
                    content.setTextColor(getResources().getColor(R.color.qmui_config_color_75_pure_black));
                    rxDialogSure.setContent("您的任务等级不足！");
                    rxDialogSure.getSureView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rxDialogSure.cancel();
                        }
                    });
                    rxDialogSure.show();
                }
                else new GetSubTasks(TaskDetailActivity.this).execute(new EnterTaskRequest(task.getPk()));
                break;
            }
        }
    }
    private void refreshFG(TaskUserRequestResponse response){
        taskUser = response;
        //收藏，两种情况
        if(response.getIsFavorite()==0){
            mFavImg.setImageDrawable(getResources().getDrawable(R.mipmap.favorite_icon));
            mFavText.setText(R.string.favorite_this_task);
        }
        else{
            mFavImg.setImageDrawable(getResources().getDrawable(R.mipmap.favorited_icon));
            mFavText.setText(R.string.already_favorited);
        }
        //抢位，五种情况
        if(task.getFields().isIs_closed() && response.getIsGrab()==0){
            mGrabImg.setImageDrawable(getResources().getDrawable(R.mipmap.grab_failed_icon));
            mGrabText.setText(R.string.grabbing_forbidden);
        }
        else if(response.getIsGrab()==0){
            mGrabImg.setImageDrawable(getResources().getDrawable(R.mipmap.grab_icon));
            mGrabText.setText(R.string.grab_this_task);
        }
        else if(response.getIsGrab()==1){
            mGrabImg.setImageDrawable(getResources().getDrawable(R.mipmap.grabbed_icon));
            mGrabText.setText(R.string.grabbing_this_task);
        }
        else if(response.getIsGrab()==2){
            mGrabImg.setImageDrawable(getResources().getDrawable(R.mipmap.grabbed_icon));
            mGrabText.setText(R.string.grabbed_successfully);
        }
        else if(response.getIsGrab()==3){
            mGrabImg.setImageDrawable(getResources().getDrawable(R.mipmap.grab_failed_icon));
            mGrabText.setText(R.string.grabbed_failed);
        }
        //进入任务

    }
    private void startDoTaskActivity(EnterTaskRequestResult result){
        Intent intent = new Intent(this, DoTaskActivity.class);
        intent.putExtra("result",result);
        intent.putExtra("task",task);
        startActivity(intent);
    }
    private void startCheckTaskActivity(CheckTaskRequestResult result){
        Intent intent = new Intent(this, CheckTaskActivity.class);
        intent.putExtra("result",result);
        intent.putExtra("task",task);
        startActivity(intent);
    }


    /**
     * network utils
     */

    private class GetTaskUser extends BaseAsyncTask<TaskUserRequest, Void, TaskUserRequestResponse> {

        private GetTaskUser(Context context){
            super(context);
        }

        @Override
        protected TaskUserRequestResponse doInBackground(TaskUserRequest... requests) {
            return getTaskUser(requests[0]);
        }
        @Override
        protected void onPostExecute(TaskUserRequestResponse response) {
            super.onPostExecute(response);
            if(response != null){
                Log.i(TAG, "onPostExecute: "+response.toString());
                refreshFG(response);
                setEnterButtonState(response);
            }else{
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mTaskLinear);
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
                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, TaskDetailActivity.this, mTaskLinear);
                taskUser.setIsFavorite(1);
                refreshFG(taskUser);
            }else{
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, TaskDetailActivity.this, mTaskLinear);
            }
        }
    }

    private class UndoFavorite extends BaseAsyncTask<TaskIdAndUsernameRequest, Void, SingleMessageResponse> {
        private UndoFavorite(Context context){
            super(context);
        }
        @Override
        protected SingleMessageResponse doInBackground(TaskIdAndUsernameRequest... requests) {
            return undoFavorite(requests[0]);
        }
        @Override
        protected void onPostExecute(SingleMessageResponse response) {
            super.onPostExecute(response);
            if(response!=null){
                taskUser.setIsFavorite(0);
                refreshFG(taskUser);
//                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, TaskDetailActivity.this, mTaskLinear);
            }else{
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, TaskDetailActivity.this, mTaskLinear);
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
                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, TaskDetailActivity.this, mTaskLinear);
                if(response.equals("已为您预约抢位！")){
                    taskUser.setIsGrab(1);
                    refreshFG(taskUser);
                }
            }else{
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, TaskDetailActivity.this, mTaskLinear);
            }
        }
    }

    private class UndoGrab extends BaseAsyncTask<TaskIdAndUsernameRequest, Void, SingleMessageResponse> {
        private UndoGrab(Context context){
            super(context);
        }
        @Override
        protected SingleMessageResponse doInBackground(TaskIdAndUsernameRequest... requests) {
            return undoGrab(requests[0]);
        }
        @Override
        protected void onPostExecute(SingleMessageResponse response) {
            super.onPostExecute(response);
            if(response!=null){
                taskUser.setIsGrab(0);
                refreshFG(taskUser);
//                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, TaskDetailActivity.this, mTaskLinear);
            }else{
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, TaskDetailActivity.this, mTaskLinear);
            }
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
                startDoTaskActivity(enterTaskRequestResult);
            }else{
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, TaskDetailActivity.this, mTaskLinear);
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
                startCheckTaskActivity(checkTaskRequestResult);
            }else{
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, TaskDetailActivity.this, mTaskLinear);
            }
        }
    }

}
