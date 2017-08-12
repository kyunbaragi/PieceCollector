package com.yunkyun.piececollector.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yunkyun.piececollector.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-08-13.
 */

public class EditDialogFragment extends android.support.v4.app.DialogFragment {
    @BindView(R.id.et_input)
    EditText memoUI;
    @BindView(R.id.tv_character_counter)
    TextView lengthCounter;

    public static final String TAG = "EditDialogFragment";

    public EditDialogFragment() {
        // Required empty public constructor.
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_dialog, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditDialogFragment.this.getDialog().cancel();
                    }
                });

        ButterKnife.bind(this, view);

        setTextWatcher();

        AlertDialog alertDialog = builder.create();

        // show the keyboard as soon we get the focus ...
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                memoUI.post(new Runnable() {
                    @Override
                    public void run() {
                        final InputMethodManager imm = (InputMethodManager) memoUI.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(memoUI, InputMethodManager.SHOW_IMPLICIT);
                        memoUI.requestFocus(); // needed if you have more then one input
                    }
                });
            }
        });


        return alertDialog;
    }

    private void setTextWatcher() {
        memoUI.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String counterState = String.format(getResources().getString(R.string.counter_format), s.length());
                lengthCounter.setText(counterState);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
