package com.android.joeycolourless.todoshi.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.ToDo;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

import java.util.UUID;

/**
 * Created by admin on 14.07.2017.
 */

public class DialogWindowDone extends DialogFragment {

    private static final String ARG_UUID = "uuid";
    public static final String EXTRA_BOOLEAN = "com.android.joeycolourless.todoshi.boolean";

    private TextView mTextView;
    private EditText mEditText;

    private ToDo mToDo;

    public static DialogWindowDone newInstance(ToDo toDo){
        Bundle args = new Bundle();
        args.putSerializable(ARG_UUID, toDo.getId());
        DialogWindowDone dialogWindowDone = new DialogWindowDone();
        dialogWindowDone.setArguments(args);
        return dialogWindowDone;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        UUID uuid = (UUID)getArguments().getSerializable(ARG_UUID);
        mToDo = ToDoLab.get(getContext()).getTodo(uuid, ToDODbSchema.ToDoTable.NAME, ToDODbSchema.ToDoTable.Cols.UUID);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_done_view, null);
        mTextView = (TextView) view.findViewById(R.id.dialog_done_view_textview);
        mEditText = (EditText) view.findViewById(R.id.dialog_done_view_edittext);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mToDo.setComments(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return new AlertDialog.Builder(getActivity())
                //.setTitle(R.string.done)
                .setView(view)
                .setPositiveButton(R.string.success_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doneToDo(true);
                    }
                })
                .setNegativeButton(R.string.fail_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doneToDo(false);
                    }
                })
                .setNeutralButton(R.string.dialog_button_cancel, null)

                .create();


    }

    private void doneToDo( boolean success){


        mToDo.setSuccess(success);
        ToDoLab.get(getActivity()).addToDo(mToDo, ToDODbSchema.ToDoCompletedTable.NAME);
        ToDoLab.get(getActivity()).updateToDo(mToDo, ToDODbSchema.ToDoCompletedTable.NAME, ToDODbSchema.ToDoCompletedTable.Cols.UUID, ToDoLab.ADD_SYNC);
        ToDoLab.get(getActivity()).deleteToDo(mToDo, ToDODbSchema.ToDoTable.NAME, ToDODbSchema.ToDoTable.Cols.UUID);
        sendResult(Activity.RESULT_OK, true);

    }

    private void sendResult(int resultCode, boolean isOk){
        if (getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_BOOLEAN, isOk);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
