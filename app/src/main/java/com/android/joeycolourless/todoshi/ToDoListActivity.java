package com.android.joeycolourless.todoshi;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.android.joeycolourless.todoshi.Fragments.ToDoFragment;
import com.android.joeycolourless.todoshi.Fragments.ToDoFragmentCompleted;
import com.android.joeycolourless.todoshi.Fragments.ToDoListFragment;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

/**
 * Created by admin on 13.03.2017.
 */

public class ToDoListActivity extends SingleFragmentActivity implements ToDoListFragment.Callbacks, ToDoFragment.Callbacks {



    @Override
    protected Fragment createFragment() {
        return new ToDoListFragment();
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onToDoSelected(ToDo toDo) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = ToDoPagerActivity.newIntent(this, toDo.getId(), ToDODbSchema.ToDoTable.NAME);
            startActivity(intent);
        } else {
            Fragment newDetail = ToDoFragment.newInstance(toDo.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onToDoUpdated(ToDo toDo) {
        ToDoListFragment listFragment = (ToDoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }


    @Override
    public boolean isTablet() {
        if (findViewById(R.id.detail_fragment_container) == null){
            return false;
    }else{
        return true;
    }
    }


}
