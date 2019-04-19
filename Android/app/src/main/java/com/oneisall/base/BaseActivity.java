package com.oneisall.base;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import static com.oneisall.OneIsAllApplication.getContext;

public class BaseActivity extends QMUIActivity {
    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(getContext(), 100);
    }

}
