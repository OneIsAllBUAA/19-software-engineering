package com.oneisall.views;


import android.os.Bundle;

import com.oneisall.base.BaseFragment;
import com.oneisall.base.BaseFragmentActivity;
import com.oneisall.views.fragment.home.HomeFragment;

public class HomeActivity extends BaseFragmentActivity {
    @Override
    protected int getContextViewId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            BaseFragment fragment = getFirstFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(getContextViewId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    private BaseFragment getFirstFragment() {
        BaseFragment fragment = new HomeFragment();
        return fragment;
    }
}
