package com.android.joeycolourless.todoshi.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.ToDo;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

import java.util.UUID;



public class ToDoFragmentCompleted extends Fragment {

    private static final String ARG_TODO_ID = "todo_id";
    private ToDo mToDo;
    private EditText mDetails;
    private EditText mComment;

    public static ToDoFragmentCompleted newInstance(UUID uuid){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TODO_ID, uuid);

        ToDoFragmentCompleted fragment = new ToDoFragmentCompleted();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID uuid = (UUID) getArguments().getSerializable(ARG_TODO_ID);
        mToDo = ToDoLab.get(getContext()).getTodo(uuid, ToDODbSchema.ToDoCompletedTable.NAME, ToDODbSchema.ToDoCompletedTable.Cols.UUID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_completed, container, false);

        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar_fragment_todo);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.menu_fragment_todo_completed);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_all_completed_todos:
                        ToDoLab.get(getContext()).deleteToDo(mToDo, ToDODbSchema.ToDoCompletedTable.NAME, ToDODbSchema.ToDoCompletedTable.Cols.UUID);
                        getActivity().finish();
                        return true;
                    default:
                        return false;
                }
            }
        });

        EditText title = (EditText) view.findViewById(R.id.todo_title);
        title.setText(mToDo.getTitle());

        mDetails = (EditText) view.findViewById(R.id.todo_details);
        mDetails.setText(mToDo.getDetails());
        mDetails.post(new Runnable() {
            @Override
            public void run() {
                if (mDetails.getLineCount() > 1){
                    mDetails.setMaxLines(mDetails.getLineCount() + 2);

                }
            }
        });
        mDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mDetails.getLineCount() > 1){
                    mDetails.setMaxLines(ToDoFragmentCompleted.this.mDetails.getLineCount() + 2);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextView completedTo = (TextView)view.findViewById(R.id.text_view_todo_date_completed);
        if (mToDo.getDate() == null){
            completedTo.setText(R.string.no_date);
        }else {
            completedTo.setText(mToDo.getDate().toString());
        }


        mComment = (EditText)view.findViewById(R.id.todo_comment);
        mComment.setText(mToDo.getComments());
        mComment.post(new Runnable() {
            @Override
            public void run() {
                if (mComment.getLineCount() > 1){
                    mComment.setMaxLines(mComment.getLineCount() + 2);
                }
            }
        });
        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mComment.getLineCount() > 1){
                    mComment.setMaxLines(mComment.getLineCount() + 2);
                }
                mToDo.setComments(s.toString());
                mToDo.setSync(ToDoLab.ADD_SYNC);
                ToDoLab.get(getContext()).updateToDo(mToDo, ToDODbSchema.ToDoCompletedTable.NAME, ToDODbSchema.ToDoCompletedTable.Cols.UUID, mToDo.getSync());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;


    }
}
