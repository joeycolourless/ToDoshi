package com.android.joeycolourless.todoshi.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.joeycolourless.todoshi.PollService;
import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.StartActivity;
import com.android.joeycolourless.todoshi.ToDo;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private static final String FIRST_START_UI_WITH_NEW_LOGIN = "first_start_ui_with_new_login";


    private RecyclerView mToDoRecyclerView;
    private ToDoAdapter mAdapter;
    private boolean mSubtitleVisible;
    private TextView mTextView;
    private Callbacks mCallbacks;
    private List<ToDo> mToDos = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabaseRef;


    public interface Callbacks{
        void onToDoSelected(ToDo toDo);
        boolean isTablet();


    }

    @Override
    public void onStart() {
        super.onStart();
        if (StartActivity.isOnline(getContext())) {
            List<ToDo> list = ToDoLab.get(getContext()).getToDos(ToDODbSchema.ToDoDeletedTable.NAME);
            if (list.size() != 0) {
                for (ToDo toDo : list) {
                    ToDoLab.get(getContext()).firebaseSyncToDO(toDo, ToDoTable.NAME, ToDoLab.DELETE, getContext());
                }
            }
        }
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

        //Necessarily set layoutManager for RecyclerView
        mToDoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();

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


        if (StartActivity.mFirstEnter){

            firebaseStartSync();

        }else updateUI();

        //updateUI();

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
            case R.id.menu_item_logout:
                ToDoLab.get(getContext()).deleteAllToDos(ToDoTable.NAME);
                ToDoLab.get(getContext()).deleteAllToDos(ToDODbSchema.ToDoCompletedTable.NAME);
                mAuth.signOut();
                getActivity().finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void firebaseStartSync(){
        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid()).child(ToDoTable.NAME);

        mFirebaseDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ToDo toDo;
                    toDo = snapshot.getValue(ToDo.class);
                    ToDoLab.get(getContext()).addToDo(toDo, ToDoTable.NAME);
                }
            updateUI();
                StartActivity.mFirstEnter = false;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


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
                        toDo.setSuccess(false);
                        ToDoLab.get(getActivity()).doneToDo(toDo);
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
        //private CheckBox mPriorityCheckBox;
        private ImageView mNotificationStatusImage;
        private ImageView mPriorityStatusImage;
        private ToDo mToDo;

        public ToDoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_todo_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_todo_date_text_view);
            mNotificationStatusImage = (ImageView) itemView.findViewById(R.id.notification_status_image_view);
            mPriorityStatusImage = (ImageView) itemView.findViewById(R.id.priority_status_image_view);
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
            if (mToDo.isPriority()){
                mPriorityStatusImage.setVisibility(View.VISIBLE);
            }else {
                mPriorityStatusImage.setVisibility(View.INVISIBLE);
            }
            if (mToDo.isNotification()){

                mNotificationStatusImage.setImageResource(R.drawable.ic_notifications_active_black_24dp);

            }else{
                mNotificationStatusImage.setImageResource(R.drawable.ic_notifications_off_black_24dp);
            }

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
