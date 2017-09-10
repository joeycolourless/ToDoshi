package com.android.joeycolourless.todoshi.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.joeycolourless.todoshi.PollService;
import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.ToDo;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoTable;

/**
 * Created by admin on 13.03.2017.
 */

public class ToDoListFragment extends Fragment {

    private static final String TAG = "tag";

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";


    private RecyclerView mToDoRecyclerView;
    private ToDoAdapter mAdapter;
    private boolean mSubtitleVisible;
    private TextView mTextView;
    private Callbacks mCallbacks;
    private List<ToDo> mToDos = new ArrayList<>();


    public interface Callbacks{
        void onToDoSelected(ToDo toDo);
        boolean isTablet();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        mToDoRecyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        //Обов'язково потрібно назначити LayoutManager для RecyclerView, він займається розміщенням елементів
        mToDoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTextView = (TextView) view.findViewById(R.id.fragment_todo_list_text_view);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToDo toDo = new ToDo();
                ToDoLab.get(getActivity()).addToDo(toDo, ToDoTable.NAME);
                //updateUI();
                mCallbacks.onToDoSelected(toDo);
            }
        });

        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }




        updateUI();

        PollService.setServiceAlarm(getActivity(),true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_todo_list, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);


        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mToDos = ToDoLab.get(getActivity()).getToDosSearch(newText, ToDoTable.NAME);
                if (mToDos == null){
                    return true;
                }
                mAdapter.setToDos(mToDos);
                mAdapter.notifyDataSetChanged();

                return false;
            }
        });

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        ToDoLab toDoLab = ToDoLab.get(getActivity());
        int toDoCount = toDoLab.getToDos(ToDoTable.NAME).size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, toDoCount, toDoCount);

        if (!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI() {
        ToDoLab todoLab = ToDoLab.get(getActivity());
        List<ToDo> toDos = todoLab.getToDos(ToDoTable.NAME);


        if (toDos.size() == 0){
            mTextView.setVisibility(View.VISIBLE);

        }else {
            mTextView.setVisibility(View.INVISIBLE);
            for (ToDo toDo : toDos){
                if (toDo.getNotificationDate().getTime() == 0){

                }else {
                    if (toDo.getNotificationDate().getTime() < (new Date().getTime() - (toDo.getNotificationDate().getTime() - new Date().getTime()))) {
                        Log.i(TAG, "Times: " + toDo.getNotificationDate().getTime() + "  " + new Date().getTime());
                        ToDoLab.get(getActivity()).addToDo(toDo, ToDODbSchema.ToDoCompletedTable.NAME);
                        ToDoLab.get(getActivity()).deleteToDo(toDo, ToDoTable.NAME, ToDoTable.Cols.UUID);
                    }
                }
            }
        }

        if (mAdapter == null) {
            mAdapter = new ToDoAdapter(toDos);
            mToDoRecyclerView.setAdapter(mAdapter);
        }else {
                mAdapter.setToDos(toDos);
                mAdapter.notifyDataSetChanged();


            updateSubtitle();


        }
    }

    private class ToDoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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

        public void bindToDo(ToDo toDo){
            mToDo = toDo;
            mTitleTextView.setText(mToDo.getTitle());
            if (mToDo.getNotificationDate().getTime() < mToDo.getDate().getTime()){
                mDateTextView.setText(R.string.the_notication_date_not_introduced);
            }else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE HH:mm, dd.MM", Locale.getDefault());
                mDateTextView.setText(simpleDateFormat.format(mToDo.getNotificationDate()));
                //mDateTextView.setText(mToDo.getNotificationDate().toString());
            }

            mPriorityCheckBox.setChecked(mToDo.isPriority());
            mPriorityCheckBox.setEnabled(false);

        }

        @Override
        public void onClick(View v) {
            mCallbacks.onToDoSelected(mToDo);
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
            updateToDo(toDo, ToDoTable.NAME, ToDoTable.Cols.UUID);
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
