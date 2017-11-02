package com.android.joeycolourless.todoshi.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

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
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.done)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doneToDo(true);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doneToDo(false);
                    }
                })
                .setNeutralButton(android.R.string.cancel, null)

                .create();


    }

    private void doneToDo( boolean success){
        UUID uuid = (UUID)getArguments().getSerializable(ARG_UUID);
        ToDo toDo = ToDoLab.get(getContext()).getTodo(uuid, ToDODbSchema.ToDoTable.NAME, ToDODbSchema.ToDoTable.Cols.UUID);
        toDo.setSuccess(success);
        ToDoLab.get(getActivity()).addToDo(toDo, ToDODbSchema.ToDoCompletedTable.NAME);
        ToDoLab.get(getActivity()).updateToDo(toDo, ToDODbSchema.ToDoCompletedTable.NAME, ToDODbSchema.ToDoCompletedTable.Cols.UUID, ToDoLab.ADD_SYNC);
        ToDoLab.get(getActivity()).deleteToDo(toDo, ToDODbSchema.ToDoTable.NAME, ToDODbSchema.ToDoTable.Cols.UUID);
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
