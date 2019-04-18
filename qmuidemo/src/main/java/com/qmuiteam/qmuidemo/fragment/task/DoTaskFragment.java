package com.qmuiteam.qmuidemo.fragment.task;

import android.view.LayoutInflater;
import android.view.View;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseFragment;
import com.qmuiteam.qmuidemo.model.response.EnterTaskRequestResult;
import com.qmuiteam.qmuidemo.model.response.Task;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoTaskFragment extends BaseFragment {
    private Task task;
    private EnterTaskRequestResult taskDetail;

    public void setTask(Task task) {
        this.task = task;
    }

    public void setTaskDetail(EnterTaskRequestResult taskDetail) {
        this.taskDetail = taskDetail;
    }

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_about, null);
        ButterKnife.bind(this, root);
        initTopBar();
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
    }}
