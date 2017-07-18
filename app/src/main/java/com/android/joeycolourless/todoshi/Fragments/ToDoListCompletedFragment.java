package com.android.joeycolourless.todoshi.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.ToDo;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

import java.util.List;


public class ToDoListCompletedFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private ToDoAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_completed, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view_completed);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTextView = (TextView) view.findViewById(R.id.text_view_todo_fragment_completed_label);
        mTextView.setVisibility(View.INVISIBLE);


        updateUI();
        return view;
    }


    public void updateUI() {
        ToDoLab todoLab = ToDoLab.get(getActivity());
        List<ToDo> toDos = todoLab.getToDos(ToDODbSchema.ToDoCompletedTable.NAME);


        if (toDos.size() == 0) {
            mTextView.setVisibility(View.VISIBLE);

        } else {
            mTextView.setVisibility(View.INVISIBLE);

        }

        if (mAdapter == null) {
            mAdapter = new ToDoAdapter(toDos);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setToDos(toDos);
            mAdapter.notifyDataSetChanged();


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class ToDoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mTitleTextView;
            private TextView mDateTextView;
            private CheckBox mPriorityCheckBox;
            private ToDo mToDo;


            public ToDoHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_todo_title_text_view);
                mDateTextView = (TextView) itemView.findViewById(R.id.list_item_todo_date_text_view);
                mPriorityCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_todo_priority_check_box);
            }

            public void bindToDo(ToDo toDo) {
                mToDo = toDo;
                mTitleTextView.setText(mToDo.getTitle());


                mPriorityCheckBox.setChecked(mToDo.isPriority());
                mPriorityCheckBox.setEnabled(false);
            }

            @Override
            public void onClick(View v) {

            }


        }


    private void updateToDo(ToDo toDo, String tableName, String colsUUID){
        ToDoLab.get(getActivity())
                .updateToDo(toDo, tableName, colsUUID);
    }

    private class ToDoAdapter extends RecyclerView.Adapter<ToDoHolder>{

        private List<ToDo> mToDos;

        public ToDoAdapter(List<ToDo> toDos){
            mToDos = toDos;
        }

        @Override
        public ToDoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_todo, parent, false);
            return new ToDoHolder(view);
        }

        @Override
        public void onBindViewHolder(ToDoHolder holder, int position) {
            ToDo toDo = mToDos.get(position);
            toDo.setPosition(position);
            updateToDo(toDo, ToDODbSchema.ToDoTable.NAME, ToDODbSchema.ToDoTable.Cols.UUID);
            holder.bindToDo(toDo);
        }

        @Override
        public int getItemCount() {
            return mToDos.size();
        }

        public void setToDos(List<ToDo> toDos){
            mToDos = toDos;
        }
    }


}