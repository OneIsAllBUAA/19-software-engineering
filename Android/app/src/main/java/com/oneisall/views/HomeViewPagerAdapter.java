package com.oneisall.views;

//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;


import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 *
 */
public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<HomeFragment> fragments = new ArrayList<>();
    private HomeFragment currentFragment;

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(HomeFragment.newInstance(0));
        fragments.add(HomeFragment.newInstance(1));
        fragments.add(HomeFragment.newInstance(2));
    }

    @Override
    public HomeFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((HomeFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /**
     * Get the current fragment
     */
    public HomeFragment getCurrentFragment() {
        return currentFragment;
    }
}