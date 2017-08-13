package com.yunkyun.piececollector.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
    public static final String BUNDLE_USER_MEMO_KEY = "USER_MEMO";

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String text);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private NoticeDialogListener dialogListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialogListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }

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
                        dialogListener.onDialogPositiveClick(EditDialogFragment.this, memoUI.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditDialogFragment.this.getDialog().cancel();
                    }
                });

        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String userMemo = bundle.getString(EditDialogFragment.BUNDLE_USER_MEMO_KEY);
            memoUI.setText(userMemo);
            memoUI.setSelection(memoUI.getText().length());

            String counterState = String.format(getResources().getString(R.string.counter_format), memoUI.getText().length());
            lengthCounter.setText(counterState);
        }


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
