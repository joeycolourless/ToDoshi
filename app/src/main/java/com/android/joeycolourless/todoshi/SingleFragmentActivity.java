package com.android.joeycolourless.todoshi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.joeycolourless.todoshi.Fragments.AuthFragment;


public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();
    TabLayout mTabLayout;
    ViewPager mViewPager;

    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_container);
        setSupportActionBar(toolbar);
        initTab();

        /*FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }*/
    }

    private void initTab() {
        mViewPager = (ViewPager)findViewById(R.id.view_pager_fragment_container);
        TabPagerFragmentAdapter adapter = new TabPagerFragmentAdapter(getSupportFragmentManager());
        try {
            mViewPager.setAdapter(adapter);
            mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
            mTabLayout.setupWithViewPager(mViewPager);
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), "NullPointerException", Toast.LENGTH_SHORT).show();
        }

    }
}


