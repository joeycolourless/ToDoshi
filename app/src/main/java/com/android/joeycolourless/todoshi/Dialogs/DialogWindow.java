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

import java.util.UUID;

import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoTable;

/**
 * Created by admin on 19.03.2017.
 */

public class DialogWindow extends DialogFragment {

    public static final String EXTRA_BOOLEAN = "com.android.joeycolourless.todoshi.boolean";

    private static final String ARG_ID = "id";
    private static final String ARG_TEXT_ID = "text";






    public static DialogWindow newInstance(UUID mId, String question){
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, mId);
        args.putString(ARG_TEXT_ID, question);

        DialogWindow fragment = new DialogWindow();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())

                .setMessage(getArguments().getString(ARG_TEXT_ID))
                .setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UUID id = (UUID) getArguments().getSerializable(ARG_ID);
                        ToDo toDo;
                        toDo = ToDoLab.get(getActivity()).getTodo(id, ToDoTable.NAME, ToDoTable.Cols.UUID);
                        ToDoLab.get(getActivity()).deleteToDo(toDo, ToDoTable.NAME, ToDoTable.Cols.UUID);
                        sendResult(Activity.RESULT_OK, true);
                    }
                })
                .setNegativeButton(R.string.dialog_button_no, null)

                .create();

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
