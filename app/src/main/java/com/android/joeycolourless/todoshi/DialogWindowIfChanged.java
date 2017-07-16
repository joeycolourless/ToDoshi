package com.android.joeycolourless.todoshi;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by admin on 08.07.2017.
 */

public class DialogWindowIfChanged extends DialogFragment {
    public static final String EXTRA_BOOLEAN = "com.android.joeycolourless.todoshi.extraboolean";


    private static final String ARG_TEXT_ID = "text";






    public static DialogWindowIfChanged newInstance(String question){
        Bundle args = new Bundle();
        args.putString(ARG_TEXT_ID, question);

        DialogWindowIfChanged fragment = new DialogWindowIfChanged();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())

                .setMessage(getArguments().getString(ARG_TEXT_ID))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, true);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, true);
                    }
                })

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
