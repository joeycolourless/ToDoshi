package com.android.joeycolourless.todoshi;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

import java.io.File;
import java.util.UUID;

/**
 * Created by admin on 22.03.2017.
 */

public class PictureDialogFragment extends DialogFragment {

    private static final String ARG_ID = "id";
    private File mPhotoFile;
    private ImageView mImageView;

    public static PictureDialogFragment newInstance(UUID mId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, mId);
        PictureDialogFragment fragment = new PictureDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        UUID id = (UUID) getArguments().getSerializable(ARG_ID);
        ToDo toDo = ToDoLab.get(getActivity()).getTodo(id, ToDODbSchema.ToDoTable.NAME, ToDODbSchema.ToDoTable.Cols.UUID);
        mPhotoFile = ToDoLab.get(getActivity()).getPhotoFile(toDo);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final AlertDialog dialog = builder.create();

        final View dialogLayout = LayoutInflater.from(getActivity()).inflate(R.layout.picture_dialog_fragment, null);
        mImageView = (ImageView) dialogLayout.findViewById(R.id.picture_dialog_image_view);



        mImageView.setImageBitmap(PictureUtils.getScaleBitmap(mPhotoFile.getPath(), getActivity()));

        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();
        return dialog;
    }


}
