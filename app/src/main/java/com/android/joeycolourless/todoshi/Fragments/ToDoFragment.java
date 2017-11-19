package com.android.joeycolourless.todoshi.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.joeycolourless.todoshi.Dialogs.DialogWindow;
import com.android.joeycolourless.todoshi.Dialogs.DialogWindowDone;
import com.android.joeycolourless.todoshi.Dialogs.DialogWindowIfChanged;
import com.android.joeycolourless.todoshi.Dialogs.DialogWindowMessageWithFinish;
import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.ToDo;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoTable;



public class ToDoFragment extends Fragment implements OnBackPressedListener {



    private static final String ARG_TODO_ID = "todo_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_DELETE_MESSAGE = "DeleteMassage";
    private static final String DIALOG_SAVE_CHANGES = "SaveChanges";
    private static final String DIALOG_MESSAGE_WITH_FINISH = "WithFinish";
    private static final String DIALOG_MESSAGE_SUCCESS = "DialogComments";
    private static final String TAG = "TAG";

    private static final int REQUEST_DELETE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CHANGED_MESSAGE = 2;
    private static final int REQUEST_MESSAGE_WITH_FINISH = 3;
    private static final int REQUEST_MESSAGE_SUCCESS = 4;


    private static List<Integer> listPosition = new ArrayList<>();

    private SwitchDateTimeDialogFragment mDateTimeDialogFragment;


    private ToDo mToDo;
    private ToDo mToDoWithoutChange;
    private Button mDateButton;
    private Callbacks mCallbacks;

    @Override
    public void onBackPressed() {
        try {
                if (!mToDoWithoutChange.getTitle().equals("")){
                    if (mToDo.equals(mToDoWithoutChange)) {
                        getActivity().finish();
                    }else {
                        if (mToDo.getTitle().equals("") || mToDo.getTitle() == null){
                            callDialogWithFinish(getString(R.string.text_do_not_apply_changes));

                    }else {
                            changedMessage(getString(R.string.save_changes_message));
                        }
                    }
                }else {
                    if (mToDo.getTitle().equals("") || mToDo.getTitle() == null) {
                        if (!mToDo.equals(mToDoWithoutChange)){
                            callDeleteWindow(getString(R.string.text_dialog_window_back_button));
                        }else {
                            ToDoLab.get(getContext()).deleteToDo(mToDo, ToDoTable.NAME, ToDoTable.Cols.UUID);
                            getActivity().finish();
                        }
                    } else {
                        if (mToDoWithoutChange != null) {
                            if (mToDo.equals(mToDoWithoutChange)) {
                                getActivity().finish();
                            } else {
                                changedMessage(getString(R.string.save_changes_message));
                            }

                        }
                    }
                }


        }catch (NullPointerException e){
            callDeleteWindow(getString(R.string.text_dialog_window_back_button));
        }


    }

    private void callDialogWithFinish(String message){
        FragmentManager manager = getFragmentManager();
        DialogWindowMessageWithFinish dialogWindowMessageWithFinish = DialogWindowMessageWithFinish.newInstance(message);
        dialogWindowMessageWithFinish.setTargetFragment(ToDoFragment.this, REQUEST_MESSAGE_WITH_FINISH);
        dialogWindowMessageWithFinish.show(manager, DIALOG_MESSAGE_WITH_FINISH);
    }

    private void callDeleteWindow(String deleteMessage) {
        FragmentManager manager = getFragmentManager();
        DialogWindow dialogWindow = DialogWindow.newInstance(mToDo.getId(), deleteMessage);
        dialogWindow.setTargetFragment(ToDoFragment.this, REQUEST_DELETE);
        dialogWindow.show(manager, DIALOG_DELETE_MESSAGE);
    }

    private void changedMessage(String message){
        FragmentManager manager = getFragmentManager();
        DialogWindowIfChanged dialogWindow = DialogWindowIfChanged.newInstance(message);
        dialogWindow.setTargetFragment(ToDoFragment.this, REQUEST_CHANGED_MESSAGE);
        dialogWindow.show(manager, DIALOG_SAVE_CHANGES);
    }

    private void successMessage(){
        FragmentManager manager = getFragmentManager();
        DialogWindowDone dialogWindowDone = DialogWindowDone.newInstance(mToDo);
        dialogWindowDone.setTargetFragment(ToDoFragment.this, REQUEST_MESSAGE_SUCCESS);
        dialogWindowDone.show(manager, DIALOG_MESSAGE_SUCCESS);
    }


    public interface Callbacks {
        void onToDoUpdated(ToDo toDo);
        boolean isTablet();
    }



    public static ToDoFragment newInstance(UUID toDoId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TODO_ID, toDoId);

        ToDoFragment fragment = new ToDoFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    public static void addListPosition(int position){
        listPosition.add(position);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID toDoId = (UUID) getArguments().getSerializable(ARG_TODO_ID);
        mToDo = ToDoLab.get(getActivity()).getTodo(toDoId, ToDoTable.NAME, ToDoTable.Cols.UUID);
        try {
            if (mToDo.getTitle().equals(getString(R.string.empty))){
            mToDo.setTitle("");
            ToDoLab.get(getActivity()).updateToDo(mToDo, ToDoTable.NAME, ToDoTable.Cols.UUID);
            }
        }catch (NullPointerException e){
            mToDo.setTitle("");
            ToDoLab.get(getActivity()).updateToDo(mToDo, ToDoTable.NAME, ToDoTable.Cols.UUID);
        }


        try {
            mToDoWithoutChange = mToDo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (ToDoLab.get(getContext()).getTodo(mToDo.getId(), ToDoTable.NAME, ToDoTable.Cols.UUID) != null){
                if (mToDo.getTitle().equals("") || mToDo.getTitle() == null) {
                    mToDo.setTitle(getString(R.string.empty));
                    mToDo.setNotification(false);
                    mToDo.setNotificationDate(null);
                    updateToDo();
                }else updateToDo();
            }

        }catch (NullPointerException e){
            //mToDo.setTitle(getString(R.string.empty));
           // mToDo.setNotification(false);
           // mToDo.setNotificationDate(null);
            //updateToDo();
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_todo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.delete_todo:
                callDeleteWindow(getString(R.string.delete_message_delete_button));
                return true;
            case R.id.share_todo:
                ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(getActivity());
                builder.setType("text/plain").setText(getToDoForShare());
                builder.setSubject(getString(R.string.todo_share_title));
                builder.setChooserTitle(R.string.send_todo);
                builder.startChooser();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo, container, false);

        Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar_fragment_todo);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_todo:
                        callDeleteWindow(getString(R.string.delete_message_delete_button));
                        return true;
                    case R.id.share_todo:
                        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(getActivity());
                        builder.setType("text/plain").setText(getToDoForShare());
                        builder.setSubject(getString(R.string.todo_share_title));
                        builder.setChooserTitle(R.string.send_todo);
                        builder.startChooser();
                    case R.id.finish_todo:
                        try {
                            if (mToDo.getTitle().equals("") || mToDo.getTitle() == null) {
                                callDeleteWindow(getString(R.string.text_if_save_without_title));
                            } else {
                               updateToDo();
                            }
                        }catch (NullPointerException e){
                            callDeleteWindow(getString(R.string.text_if_save_without_title));
                        }

                    default:
                        return false;
                }
            }
        });
        toolbar.inflateMenu(R.menu.menu_fragment_todo);




        EditText titleField = (EditText) v.findViewById(R.id.todo_title);
        titleField.setText(mToDo.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mToDo.setTitle(s.toString());
                //updateToDo();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EditText details = (EditText) v.findViewById(R.id.todo_details);
        details.setText(mToDo.getDetails());
        details.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mToDo.setDetails(s.toString());
                //updateToDo();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button)v.findViewById(R.id.todo_date);

        if (mToDo.getNotificationDate().getTime() < mToDo.getDate().getTime()){
            mDateButton.setText(R.string.pls_enter_deadline);
        }else {
           mDateButton.setText(new SimpleDateFormat("MMMM dd, HH:mm"   ,Locale.getDefault()).format(mToDo.getNotificationDate()));
        }

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateTimeDialogFragment.show(getFragmentManager(), DIALOG_DATE);
            }
        });

        CheckBox priorityCheckBox = (CheckBox) v.findViewById(R.id.todo_priority);

        priorityCheckBox.setChecked(mToDo.isPriority());
        priorityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Action after changed checkBox
                mToDo.setPriority(isChecked);

            }
        });

        final CheckBox notificationCheckbox = (CheckBox) v.findViewById(R.id.checkbox_todo_notification);
        if (mToDo.getNotificationDate().getTime() < mToDo.getDate().getTime()){
            notificationCheckbox.setEnabled(false);
        }
        notificationCheckbox.setChecked(mToDo.isNotification());
        notificationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mToDo.setNotification(isChecked);
            }
        });

        addListPosition(mToDo.getPosition());

        mDateTimeDialogFragment = (SwitchDateTimeDialogFragment) getFragmentManager().findFragmentByTag(DIALOG_DATE);
        if (mDateTimeDialogFragment == null){
            mDateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.date_picker_title),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }

        mDateTimeDialogFragment.startAtCalendarView();
        mDateTimeDialogFragment.set24HoursMode(true);
        mDateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        mDateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        mDateTimeDialogFragment.setDefaultDateTime(new Date());

        try {
            mDateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        mDateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                mToDo.setNotificationDate(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, HH:mm"   ,Locale.getDefault());


                mDateButton.setText(simpleDateFormat.format(date));
                notificationCheckbox.setEnabled(true);
                //updateToDo();
            }

            @Override
            public void onNegativeButtonClick(Date date) {

            }
        });

        Button doneButton = (Button)v.findViewById(R.id.done_button);
        if (mToDo.getTitle().equals("")){
            doneButton.setVisibility(View.INVISIBLE);
        }else doneButton.setVisibility(View.VISIBLE);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToDo.setDate(new Date());
                ToDoLab.get(getContext()).updateToDo(mToDo, ToDoTable.NAME, ToDoTable.Cols.UUID);
                successMessage();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            if (requestCode == REQUEST_CHANGED_MESSAGE){
                mToDo = mToDoWithoutChange;
                if (mToDo.getTitle().equals("")){
                    ToDoLab.get(getActivity()).deleteToDo(mToDo, ToDoTable.NAME, ToDoTable.Cols.UUID);
                    getActivity().finish();
                }else updateToDo();

            }

            return;
        }

        if (requestCode == REQUEST_DELETE) {
            if (mCallbacks.isTablet()) {
            //updateToDo();
                List<ToDo> list;
                list = ToDoLab.get(getActivity()).getToDos(ToDoTable.NAME);
                if (list.size() != 0) {
                    ToDo toDo = list.get(0);
                    Fragment newDetail = ToDoFragment.newInstance(toDo.getId());

                    getFragmentManager().beginTransaction()
                            .replace(R.id.detail_fragment_container, newDetail)
                            .commit();

                }else {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.detail_fragment_container, new Fragment())
                            .commit();


                }
            }else {
                getActivity().finish();

            }
        }
        if (requestCode == REQUEST_CHANGED_MESSAGE){
            updateToDo();
        }
        if (requestCode == REQUEST_MESSAGE_WITH_FINISH){
            mToDo = mToDoWithoutChange;
            updateToDo();
            getActivity().finish();
        }
        if (requestCode == REQUEST_MESSAGE_SUCCESS){
            getActivity().finish();
        }
    }



    private void updateToDo(){
        try {
            if (mToDo.getTitle().equals("") || mToDo.getTitle() == null) {
                callDeleteWindow(getString(R.string.text_dialog_window_back_button));
            } else {
                    mToDo.setSync(ToDoLab.ADD_SYNC);
                    ToDoLab.get(getActivity()).updateToDo(mToDo, ToDoTable.NAME, ToDoTable.Cols.UUID, mToDo.getSync());
                }

                //mCallbacks.onToDoUpdated(mToDo);
                getActivity().finish();

        }catch (NullPointerException e){
            callDeleteWindow(getString(R.string.text_dialog_window_back_button));
        }




    }

    private String getToDoForShare(){
        String priorityString;
        if (mToDo.isPriority()){
            priorityString = getString(R.string.todo_share_priority_yes);
        }else {
            priorityString = getString(R.string.todo_share_priority_no);
        }

        String timeFormat = "H:mm";
        String time = android.text.format.DateFormat.format(timeFormat, mToDo.getDate()).toString();

        String dayFormat = "EEEE";
        String day = android.text.format.DateFormat.format(dayFormat, mToDo.getDate()).toString();

        String dateFormat = "d.MM.yyyy";
        String date = android.text.format.DateFormat.format(dateFormat, mToDo.getDate()).toString();

        String share = getString(R.string.todo_share, mToDo.getTitle(), time, day, date, priorityString);

        if (mToDo.getDetails() != null){
            share += "\n" + getString(R.string.todo_details_label) + ": " + mToDo.getDetails();
        }

        return share;
    }


}
