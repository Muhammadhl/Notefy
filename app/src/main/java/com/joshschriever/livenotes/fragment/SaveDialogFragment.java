package com.joshschriever.livenotes.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import com.project.notefy.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SaveDialogFragment extends DialogFragment {

    private String filename;
    private EditText user_input;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        filename = "Composition_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.US)
                .format(Calendar.getInstance().getTime());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.save_dialog, null);
        Dialog dialog = new AlertDialog.Builder(getContext()).setView(view)
                .setTitle(R.string.save_musicxml_file)
                .setPositiveButton(R.string.save, (d, w) -> dismiss(true))
                .setNegativeButton(R.string.cancel, (d, w) -> dismiss(false))
                .create();

        setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        user_input = (EditText)view.findViewById(R.id.file_name);
        user_input.setText(filename);
        return dialog;
    }

    private void dismiss(boolean save) {
        if (save) {
            String value = user_input.getText().toString();
            if(!value.isEmpty()) filename = value + ".xml";
            else {
                filename += ".xml";
            }
            ((Callbacks) getActivity()).onSave(filename);
        } else {
            ((Callbacks) getActivity()).onCancelSaving();
        }
        dismiss();
    }

    public interface Callbacks {

        void onSave(String fileName);

        void onCancelSaving();
    }

}
