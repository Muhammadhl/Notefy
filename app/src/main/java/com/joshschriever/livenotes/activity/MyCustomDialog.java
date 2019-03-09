package com.joshschriever.livenotes.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.project.notefy.R;

import static com.joshschriever.livenotes.activity.note.DO;
import static com.joshschriever.livenotes.activity.note.RE;
import static com.joshschriever.livenotes.activity.note.ME;
import static com.joshschriever.livenotes.activity.note.FA;
import static com.joshschriever.livenotes.activity.note.SOL;
import static com.joshschriever.livenotes.activity.note.LA;
import static com.joshschriever.livenotes.activity.note.CI;


/**
 * Created by User on 12/10/2017.
 */
enum note {
    DO, RE, ME, FA, SOL, LA, CI
}


public class MyCustomDialog extends DialogFragment {

    public String _sign = "";
    public int _value = 48;
    public int _duration = 600;
    private static final String TAG = "MyCustomDialog";


    //widgets
    private TextView mActionOk, mActionCancel;


    //vars

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_my_custom, container, false);

        // reset parameters
        _sign = "";
        _value = 48;
        _duration = 600;

        ///// exmaple how to handle button click
        final ToggleButton bemol = (ToggleButton) view.findViewById(R.id.bemol);
        //final Button bekar = (Button) view.findViewById(R.id.bekar);
        final ToggleButton diese = (ToggleButton) view.findViewById(R.id.diese);
        diese.setChecked(false);
        //final Button none = (Button) view.findViewById(R.id.none);

        final ToggleButton whole = (ToggleButton) view.findViewById(R.id.whole);

        final ToggleButton half = (ToggleButton) view.findViewById(R.id.half);
        final ToggleButton quarter = (ToggleButton) view.findViewById(R.id.quarter);
        final ToggleButton eight = (ToggleButton) view.findViewById(R.id.eight);
        final ToggleButton sixteenth = (ToggleButton) view.findViewById(R.id.sixteenth);
        final ToggleButton thirty_second = (ToggleButton) view.findViewById(R.id.thirty_second);
        final ImageView preview = (ImageView) view.findViewById(R.id.key_sig_image);

        preview.setImageResource(R.drawable.do1c);

        final Button plus = (Button) view.findViewById(R.id.plus);
        final Button minus = (Button) view.findViewById(R.id.minus);
        final Button cancel = (Button) view.findViewById(R.id.cancel);
        final Button add_note = (Button) view.findViewById(R.id.add_note);

        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAndAdd(true);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IntToNote(_value) == ME || IntToNote(_value) == CI) {
                    _value ++;
                } else {
                    _value += 2;
                }
                if(_value == 48) { preview.setImageResource(R.drawable.do1c); }
                if(_value == 50) { preview.setImageResource(R.drawable.re1c); }
                if(_value == 52) { preview.setImageResource(R.drawable.me1c); }
                if(_value == 53) { preview.setImageResource(R.drawable.fa1c); }
                if(_value == 55) { preview.setImageResource(R.drawable.sol1c); }
                if(_value == 57) { preview.setImageResource(R.drawable.la1c); }
                if(_value == 59) { preview.setImageResource(R.drawable.ci1c); }
                if(_value == 60) { preview.setImageResource(R.drawable.do2c); }
                if(_value == 62) { preview.setImageResource(R.drawable.re2c); }
                if(_value == 64) { preview.setImageResource(R.drawable.me2c); }
                if(_value == 65) { preview.setImageResource(R.drawable.fa2c); }
                if(_value == 67) { preview.setImageResource(R.drawable.sol2c); }
                if(_value == 69) { preview.setImageResource(R.drawable.la2c); }
                if(_value == 71) { preview.setImageResource(R.drawable.ci2c); }
                if(_value == 72) { preview.setImageResource(R.drawable.do3c); }

            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IntToNote(_value) == DO || IntToNote(_value) == FA) {
                    _value --;
                } else {
                    _value -= 2;
                }
                if(_value == 48) { preview.setImageResource(R.drawable.do1c); }
                if(_value == 50) { preview.setImageResource(R.drawable.re1c); }
                if(_value == 52) { preview.setImageResource(R.drawable.me1c); }
                if(_value == 53) { preview.setImageResource(R.drawable.fa1c); }
                if(_value == 55) { preview.setImageResource(R.drawable.sol1c); }
                if(_value == 57) { preview.setImageResource(R.drawable.la1c); }
                if(_value == 59) { preview.setImageResource(R.drawable.ci1c); }
                if(_value == 60) { preview.setImageResource(R.drawable.do2c); }
                if(_value == 62) { preview.setImageResource(R.drawable.re2c); }
                if(_value == 64) { preview.setImageResource(R.drawable.me2c); }
                if(_value == 65) { preview.setImageResource(R.drawable.fa2c); }
                if(_value == 67) { preview.setImageResource(R.drawable.sol2c); }
                if(_value == 69) { preview.setImageResource(R.drawable.la2c); }
                if(_value == 71) { preview.setImageResource(R.drawable.ci2c); }
                if(_value == 72) { preview.setImageResource(R.drawable.do3c); }
            }
        });




        diese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(diese.isChecked()) {
                    _sign = "";
                    diese.setChecked(false);

                }
                else {
                    _sign = "diese";
                    diese.setChecked(true);
                }
                bemol.setChecked(false);


            }
        });

        bemol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _sign = "bemol";
                bemol.setChecked(true);

                diese.setChecked(false);

            }
        });



        whole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _duration = 2400;
                whole.setEnabled(false);
                half.setEnabled(true);
                quarter.setEnabled(true);
                eight.setEnabled(true);
                sixteenth.setEnabled(true);
                thirty_second.setEnabled(true);
            }
        });
        half.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _duration = 1200;
                whole.setEnabled(true);
                half.setEnabled(false);
                quarter.setEnabled(true);
                eight.setEnabled(true);
                sixteenth.setEnabled(true);
                thirty_second.setEnabled(true);
            }
        });
        quarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _duration = 600;
                whole.setEnabled(true);
                half.setEnabled(true);
                quarter.setEnabled(false);
                eight.setEnabled(true);
                sixteenth.setEnabled(true);
                thirty_second.setEnabled(true);
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _duration = 300;
                whole.setEnabled(true);
                half.setEnabled(true);
                quarter.setEnabled(true);
                eight.setEnabled(false);
                sixteenth.setEnabled(true);
                thirty_second.setEnabled(true);
            }
        });
        sixteenth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _duration = 150;
                whole.setEnabled(true);
                half.setEnabled(true);
                quarter.setEnabled(true);
                eight.setEnabled(true);
                sixteenth.setEnabled(false);
                thirty_second.setEnabled(true);
            }
        });
        thirty_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _duration = 75;
                whole.setEnabled(true);
                half.setEnabled(true);
                quarter.setEnabled(true);
                eight.setEnabled(true);
                sixteenth.setEnabled(true);
                thirty_second.setEnabled(false);
            }
        });
        return view;
    }

    public String getSign() {
        return _sign;
    }

    public int getValue() {
        return _value;
    }

    public int getDuration() {
        return _duration;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void dismissAndAdd(boolean callback) {
        if (callback) {
            ((MyCustomDialog.Callbacks) getActivity()).onClickAdd(getValue(), getSign(), getDuration());
        }
        dismiss();
    }

    public interface Callbacks {

        void onClickAdd(int value, String sign, int duration);
    }

    private note IntToNote (int value) throws IllegalArgumentException {
        if (value % 12 == 0) { return DO; }
        else if (value % 12 == 2) { return RE; }
        else if (value % 12 == 4) { return ME; }
        else if (value % 12 == 5) { return FA; }
        else if (value % 12 == 7) { return SOL; }
        else if (value % 12 == 9) { return LA; }
        else if (value % 12 == 11){ return CI; }
        else { throw new IllegalArgumentException();}
    }
}
