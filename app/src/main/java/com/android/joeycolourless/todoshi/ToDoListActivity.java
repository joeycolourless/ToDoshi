package com.android.joeycolourless.todoshi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.joeycolourless.todoshi.Fragments.ToDoFragment;
import com.android.joeycolourless.todoshi.Fragments.ToDoListFragment;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;
import com.google.firebase.auth.FirebaseAuth;


public class ToDoListActivity extends SingleFragmentActivity implements ToDoListFragment.Callbacks, ToDoFragment.Callbacks {



    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private int count = 0;


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
    public void onBackPressed() {
        //super.onBackPressed();
        if (count == 0){
            if (mAuth.getCurrentUser() != null){
                Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();
                count++;
            }
        }else{
            count = 0;
            setResult(RESULT_OK);
            this.finish();
        }
    }

    @Override
    public boolean isTablet() {
        return findViewById(R.id.detail_fragment_container) != null;
    }


}
