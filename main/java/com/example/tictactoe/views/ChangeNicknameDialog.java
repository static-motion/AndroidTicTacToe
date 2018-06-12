package com.example.tictactoe.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tictactoe.R;
import com.example.tictactoe.interfaces.ChangeNicknameDialogListener;

public class ChangeNicknameDialog extends AppCompatDialogFragment {

    private EditText mChangeNickname;
    private ChangeNicknameDialogListener mListener;

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Change Nickname")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = mChangeNickname.getText().toString();
                        if(nickname.length() < 3 || nickname.length() > 10){
                            Toast.makeText(getContext(), "Nickname needs to be between 3 and 10 symbols long.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mListener.publishNickname(mChangeNickname.getText().toString());
                    }
                });

        mChangeNickname = view.findViewById(R.id.edit_text_change_nickname);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ChangeNicknameDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ChangeNicknameDialogInterface");
        }
    }
}
