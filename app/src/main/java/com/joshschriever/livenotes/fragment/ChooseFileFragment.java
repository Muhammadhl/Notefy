package com.joshschriever.livenotes.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ToggleButton;


import com.joshschriever.livenotes.musicxml.KeySigHandler;
import com.project.notefy.R;

import java.io.File;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class ChooseFileFragment extends DialogFragment
        implements NumberPicker.OnValueChangeListener {






    private NumberPicker file_name;
    String[] files_list = getExternalStoragePublicDirectory("Notefy/Scores").list();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.choose_file_dialog_title)
                .setView(R.layout.dialog_choose_file)
                .setPositiveButton(R.string.ok, (d, w) -> dismiss(false))
                .setNegativeButton(R.string.new_file, (d, w) -> dismiss(true))
                .create();

        setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeViews(getDialog());
    }

    private void initializeViews(Dialog dialog) {




        file_name = (NumberPicker) dialog.findViewById(R.id.key);
        file_name.setWrapSelectorWheel(false);
        file_name.setMinValue(0);
        file_name.setMaxValue(files_list.length - 1);
        file_name.setOnValueChangedListener(this);
        file_name.setDisplayedValues(files_list);
        file_name.setValue(6);
    }

    @Override
    public void onResume() {
        super.onResume();
        onValueChange(file_name, 6, 6);
    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
    }


    private void dismiss(boolean new_file) {
        if (new_file) {
            ((Callbacks) getActivity()).onFileSet(null, true);

        }
        else {
            ((Callbacks) getActivity()).onFileSet(files_list[file_name.getValue()], false);
        }
        dismiss();
    }

    public interface Callbacks {

        void onFileSet(String filename, boolean new_file);
    }

}
