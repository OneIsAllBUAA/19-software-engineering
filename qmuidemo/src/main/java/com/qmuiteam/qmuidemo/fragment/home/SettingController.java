package com.qmuiteam.qmuidemo.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseAsyncTask;
import com.qmuiteam.qmuidemo.base.BaseFragment;
import com.qmuiteam.qmuidemo.fragment.OneIsAllAboutFragment;
import com.qmuiteam.qmuidemo.model.request.LogoutRequest;
import com.qmuiteam.qmuidemo.model.request.UserInfoRequest;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
import com.qmuiteam.qmuidemo.model.response.UserInfoRequestResponse;
import com.qmuiteam.qmuidemo.utils.DialogUtils;
import com.qmuiteam.qmuidemo.utils.UserUtils;
import com.qmuiteam.qmuidemo.view.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.UserApi.getUserInfo;
import static com.qmuiteam.qmuidemo.api.UserApi.logout;
import static com.qmuiteam.qmuidemo.utils.DialogUtils.showDialog;

public class SettingController extends QMUIWindowInsetLayout {

    private static final String TAG = "SettingController";
    private Context context;
    @BindView(R.id.setting_topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.setting_groupListView) QMUIGroupListView mGroupListView;
    @BindView(R.id.setting_logout_button) QMUIRoundButton mLogoutButton;
    private HomeController.HomeControlListener mHomeControlListener;
    private int mDiffRecyclerViewSaveStateId = QMUIViewHelper.generateViewId();

    public SettingController(Context context){
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.setting_layout, this);
        ButterKnife.bind(this);
        initListeners();
        initTopBar();
        initData();
    }

    private void initListeners(){
        mLogoutButton.setOnClickListener(v->{
            new UserLogoutTask(context).execute(new LogoutRequest(UserUtils.getUserName(context), UserUtils.getPassword(context)));
        });
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
        return "个人设置";
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

    private class GetUserInfo extends BaseAsyncTask<UserInfoRequest, Void, UserInfoRequestResponse> {

        private GetUserInfo(Context context){
            super(context);
        }

        @Override
        protected UserInfoRequestResponse doInBackground(UserInfoRequest... requests) {
            return getUserInfo(requests[0]);
        }
        @Override
        protected void onPostExecute(UserInfoRequestResponse response) {
            super.onPostExecute(response);
            if(response != null){
                Log.i(TAG, "onPostExecute: "+response.toString());
                initGroupListView(response);
            }else{
                showDialog("网络错误",QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mGroupListView);
            }
        }
    }
    private void initData(){
        new GetUserInfo(context).execute(new UserInfoRequest(UserUtils.getUserName(context)));
    }
    private void initGroupListView(UserInfoRequestResponse response){
        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext());
        section.setTitle("个人信息").setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        QMUICommonListItemView usernameItem = mGroupListView.createItemView("邮箱");
        usernameItem.setDetailText(response.getEmail());
        QMUICommonListItemView emailItem = mGroupListView.createItemView("用户名");
        emailItem.setDetailText(response.getUsername());
        QMUICommonListItemView creditItem = mGroupListView.createItemView("积分");
        creditItem.setDetailText(Integer.toString(response.getTotal_credits()));
        section.addItemView(usernameItem,v->{});
        section.addItemView(emailItem,v->{});
        section.addItemView(creditItem, v->{});
        section.addTo(mGroupListView);
    }

    public class UserLogoutTask extends BaseAsyncTask<LogoutRequest, Void, SingleMessageResponse> {

        UserLogoutTask(Context context) {
            super(context);
        }

        @Override
        protected SingleMessageResponse doInBackground(LogoutRequest... params) {
            return logout(params[0]);
        }

        @Override
        protected void onPostExecute(final SingleMessageResponse response) {
            super.onPostExecute(response);
            if (response.getMessage().equals("注销成功")) {
                UserUtils.clearUserInfo(context);
                context.startActivity(new Intent(context, LoginActivity.class));
            } else {
                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mGroupListView);
            }
        }
    }
}
