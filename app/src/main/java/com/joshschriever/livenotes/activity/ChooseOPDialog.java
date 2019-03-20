package com.joshschriever.livenotes.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.notefy.R;


public class ChooseOPDialog extends DialogFragment {

    private static final String TAG = "ChooseOPDialog";
    //vars
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_opdialog, container, false);

        final Button delete = (Button) view.findViewById(R.id.delete);
        final Button add = (Button) view.findViewById(R.id.add_note);
        final Button save = (Button) view.findViewById(R.id.save);
        final Button cancel = (Button) view.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ChooseOPDialog.Callbacks) getActivity()).chooseAdd();
                getDialog().dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ChooseOPDialog.Callbacks) getActivity()).chooseSave();
                getDialog().dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ChooseOPDialog.Callbacks) getActivity()).chooseDelete();
                getDialog().dismiss();
            }
        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }



    public interface Callbacks {

        void chooseAdd();
        void chooseDelete();
        void chooseSave();



    }

}
