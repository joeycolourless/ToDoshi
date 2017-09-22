package com.android.joeycolourless.todoshi.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.ToDo;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

import java.util.UUID;



public class ToDoFragmentCompleted extends Fragment {

    private static final String ARG_TODO_ID = "todo_id";
    private ToDo mTodo;

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
        mTodo = ToDoLab.get(getContext()).getTodo(uuid, ToDODbSchema.ToDoCompletedTable.NAME, ToDODbSchema.ToDoCompletedTable.Cols.UUID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_completed, container, false);

        return view;


    }
}
