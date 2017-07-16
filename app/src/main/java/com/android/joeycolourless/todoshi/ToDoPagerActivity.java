package com.android.joeycolourless.todoshi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.android.joeycolourless.todoshi.Fragments.OnBackPressedListener;
import com.android.joeycolourless.todoshi.Fragments.ToDoFragment;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

import java.util.List;
import java.util.UUID;

/**
 * Created by admin on 15.03.2017.
 */

public class ToDoPagerActivity extends AppCompatActivity implements ToDoFragment.Callbacks {
    private static final String EXTRA_TODO_ID = "com.android.joeycolourless.todoshi.todo_id";

    private ViewPager mViewPager;
    private List<ToDo> mToDos;

    public static Intent newIntent (Context packageContext, UUID toDoId){
        Intent intent = new Intent(packageContext, ToDoPagerActivity.class);
        intent.putExtra(EXTRA_TODO_ID, toDoId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_pager);

        UUID toDoId = (UUID) getIntent().getSerializableExtra(EXTRA_TODO_ID);

        mViewPager = (ViewPager)findViewById(R.id.activity_todo_pager_view_pager);

        mToDos = ToDoLab.get(this).getToDos(ToDODbSchema.ToDoTable.NAME);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                ToDo toDo = mToDos.get(position);
                return ToDoFragment.newInstance(toDo.getId());
            }

            @Override
            public int getCount() {
                return mToDos.size();
            }
        });
        for (int i = 0; i < mToDos.size(); i++){
            if (mToDos.get(i).getId().equals(toDoId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }


    @Override
    public void onToDoUpdated(ToDo toDo) {

    }

    @Override
    public boolean isTablet() {
        return false;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        OnBackPressedListener backPressedListener = null;
        for (Fragment fragment: fm.getFragments()) {
            if (fragment instanceof  OnBackPressedListener) {
                backPressedListener = (OnBackPressedListener) fragment;
                break;
            }
        }

        if (backPressedListener != null) {
            backPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
