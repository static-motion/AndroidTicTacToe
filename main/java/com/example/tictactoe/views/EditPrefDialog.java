package com.example.tictactoe.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tictactoe.R;
import com.example.tictactoe.interfaces.ChangePrefDialogListener;

import java.util.Locale;

public class EditPrefDialog extends AppCompatDialogFragment {

    private EditText mEditPref;
    private ChangePrefDialogListener mListener;
    private String mPreference = "Preference";
    private int mMinLength = 0;
    private int mMaxLength = Integer.MAX_VALUE;

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        mEditPref = view.findViewById(R.id.edit_text_change_nickname);
        mEditPref.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxLength)});
        builder.setView(view)
                .setTitle("Set new " + mPreference)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String preference = mEditPref.getText().toString();
                        if(preference.length() < mMinLength || preference.length() > mMaxLength){
                            Toast.makeText(getContext(),
                                    String.format(Locale.ENGLISH, "%s needs to be between %d and %d symbols long.",
                                            mPreference, mMinLength, mMaxLength),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mListener.publishPreference(mPreference, preference);
                    }
                });


        return builder.create();
    }

    public EditPrefDialog setPreference(String pref){
        mPreference = pref;
        return this;
    }

    public EditPrefDialog setLengthLimit(int minLength, int maxLength){
        mMinLength = minLength;
        mMaxLength = maxLength;
        return this;
    }

    public EditPrefDialog setListener(ChangePrefDialogListener listener){
        mListener = listener;
        return this;
    }
}
