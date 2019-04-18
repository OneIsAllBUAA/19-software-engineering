package com.qmuiteam.qmuidemo.fragment.home;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseFragment;
import com.qmuiteam.qmuidemo.fragment.OneIsAllAboutFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyTaskController extends QMUIWindowInsetLayout {

    @BindView(R.id.task_home_topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.task_home_groupListView)
    QMUIGroupListView mGroupListView;

    private HomeController.HomeControlListener mHomeControlListener;
    private int mDiffRecyclerViewSaveStateId = QMUIViewHelper.generateViewId();

    public MyTaskController(Context context){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.task_home_layout, this);
        ButterKnife.bind(this);
        initTopBar();
        initGroupListView();
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

    private void initGroupListView(){

        QMUICommonListItemView item1 = mGroupListView.createItemView("回答关于图片的问题");
        item1.setDetailText("图片问答式");
        item1.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView item2 = mGroupListView.createItemView("根据图片选择选项");
        item2.setDetailText("图片单选");
        item2.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView item3 = mGroupListView.createItemView("回答关于图片的问题");
        item3.setDetailText("图片问答式");
        item3.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    Toast.makeText(getContext(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                    if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                        ((QMUICommonListItemView) v).getSwitch().toggle();
                    }
                }
            }
        };

        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.newSection(getContext())
                .setTitle("我收藏的任务")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(item1, onClickListener)
                .addItemView(item2, onClickListener)
                .addTo(mGroupListView);
        QMUIGroupListView.newSection(getContext())
                .setTitle("已抢位的任务")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(item3, onClickListener)
                .addTo(mGroupListView);
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
}
