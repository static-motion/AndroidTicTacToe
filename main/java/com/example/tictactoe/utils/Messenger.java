package com.example.tictactoe.utils;

import android.content.Context;
import android.widget.Toast;

public class Messenger {

    private Context mContext;
    private Toast mToast;
    private int mLength;

    public Messenger(Context context) {
        this.mContext = context;
        mLength = Toast.LENGTH_SHORT;
    }

    public Messenger(Context context, int length){
        this.mContext = context;
        mLength = length;
    }

    public void alert(String message){
        if(mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext, message, mLength);
        mToast.show();
    }

    private void setLength(int length){
        if(length < 0){
            throw new IllegalArgumentException("Message duration length cannot be less than zero.");
        }
        mLength = length;
    }
}
