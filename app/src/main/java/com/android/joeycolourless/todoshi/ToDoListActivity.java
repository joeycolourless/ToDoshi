package com.android.joeycolourless.todoshi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.android.joeycolourless.todoshi.Fragments.ToDoFragment;
import com.android.joeycolourless.todoshi.Fragments.ToDoListFragment;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;


public class ToDoListActivity extends SingleFragmentActivity implements ToDoListFragment.Callbacks, ToDoFragment.Callbacks {

    public static final String FIRST_ENTER = "first_enter";

    public static Intent newInstance(boolean firstEnter, Context context){
        Intent intent = new Intent(context, ToDoListActivity.class);
        intent.putExtra(FIRST_ENTER, firstEnter);
        return intent;

    }
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
        return findViewById(R.id.detail_fragment_container) != null;
    }


}
