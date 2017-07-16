package com.android.joeycolourless.todoshi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by admin on 14.07.2017.
 */

public class DialogWindowDone extends DialogFragment {

    public static final String EXTRA_BOOLEAN = "com.android.joeycolourless.todoshi.booleanextra";



    private static final String ARG_UUID = "uuid";





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

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton(android.R.string.cancel, null)

                .create();


    }
}
