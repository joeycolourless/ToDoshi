package com.android.joeycolourless.todoshi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.joeycolourless.todoshi.Fragments.ToDoListCompletedFragment;
import com.android.joeycolourless.todoshi.Fragments.ToDoListFragment;



public class TabPagerFragmentAdapter extends FragmentPagerAdapter {
    private String[] tabs;
    FragmentManager mFragmentManager;

    public TabPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        tabs = new String[]{
                "Actual",
                "Completed",

        };
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FragmentManager fm = mFragmentManager;
                Fragment fragment = fm.findFragmentById(R.id.fragment_container);

                if(fragment == null) {
                   return new ToDoListFragment();
                }
            case 1:
                return new ToDoListCompletedFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
