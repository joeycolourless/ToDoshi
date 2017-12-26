package com.android.joeycolourless.todoshi.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.StartActivity;
import com.android.joeycolourless.todoshi.ToDo;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.android.joeycolourless.todoshi.ToDoPagerActivity;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class ToDoListCompletedFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private ToDoAdapter mAdapter;
    private DatabaseReference mFirebaseDatabaseRef;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_list_todo_completed, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_completed_todos:
                ToDoLab.get(getContext()).deleteAllToDos(ToDODbSchema.ToDoCompletedTable.NAME, true);
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list_completed, container, false);

        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view_completed);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTextView = (TextView) view.findViewById(R.id.text_view_todo_fragment_completed_label);
        mTextView.setVisibility(View.INVISIBLE);

        if (StartActivity.mFirstEnter){
            mFirebaseDatabaseRef = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid()).child(ToDODbSchema.ToDoCompletedTable.NAME);
            mFirebaseDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        ToDo toDo;
                        toDo = snapshot.getValue(ToDo.class);
                        ToDoLab.get(getContext()).addToDo(toDo, ToDODbSchema.ToDoCompletedTable.NAME);
                    }
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            updateUI();
        }



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
            private TextView mDetailsTextView;
            private TextView mSuccessTextView;
            private ToDo mToDo;


            public ToDoHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                mTitleTextView = (TextView) itemView.findViewById(R.id.textview_title_todo);
                mDetailsTextView = (TextView) itemView.findViewById(R.id.textview_details_todo);
                mSuccessTextView = (TextView) itemView.findViewById(R.id.textview_success_todo);
            }

            public void bindToDo(ToDo toDo) {
                mToDo = toDo;
                mTitleTextView.setText(mToDo.getTitle());
                if (mToDo.getDetails().equals("") || mToDo.getDetails() == null){
                    mDetailsTextView.setText(R.string.no_details);
                }else mDetailsTextView.setText(mToDo.getDetails().replaceAll("\n", " "));
                if (mToDo.isSuccess()){
                    mSuccessTextView.setText(R.string.success_button);
                    mSuccessTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }else{
                    mSuccessTextView.setText(R.string.fail_button);
                    mSuccessTextView.setTextColor(getResources().getColor((R.color.red)));
                }


            }

            @Override
            public void onClick(View v) {
                if (v.findViewById(R.id.detail_fragment_container) == null) {
                    Intent intent = ToDoPagerActivity.newIntent(getContext(), mToDo.getId(), ToDODbSchema.ToDoCompletedTable.NAME);
                    startActivity(intent);
                } else {
                    Fragment newDetail = ToDoFragmentCompleted.newInstance(mToDo.getId());

                    getFragmentManager().beginTransaction()
                            .replace(R.id.detail_fragment_container, newDetail)
                            .commit();
                }
            }


        }


    private void updateToDo(ToDo toDo, String tableName, String colsUUID){
        ToDoLab.get(getActivity())
                .updateToDo(toDo, tableName, colsUUID, toDo.getSync());
    }

    private class ToDoAdapter extends RecyclerView.Adapter<ToDoHolder>{

        private List<ToDo> mToDos;

        public ToDoAdapter(List<ToDo> toDos){
            mToDos = toDos;
        }

        @Override
        public ToDoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_completed_todo, parent, false);
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
