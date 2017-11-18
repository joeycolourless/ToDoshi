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

/**
 * Created by admin on 12.07.2017.
 */

public class DialogWindowMessageWithFinish extends DialogFragment {
    public static final String EXTRA_BOOLEAN = "com.android.joeycolourless.todoshi.extra";
    private static final String ARG_TEXT_ID = "text";

    public static DialogWindowMessageWithFinish newInstance(String question){
        Bundle args = new Bundle();
        args.putString(ARG_TEXT_ID, question);

        DialogWindowMessageWithFinish fragment = new DialogWindowMessageWithFinish();
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
                        sendResult(Activity.RESULT_OK, true);
                    }
                })
                .setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
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
