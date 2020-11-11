package com.example.tictactoe.utils;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class Messenger {

    private WeakReference<Context> mContext;
    private Toast mToast;
    private int mLength;

    public Messenger(Context context) {
        this.mContext = new WeakReference<>(context);
        mLength = Toast.LENGTH_SHORT;
    }
    public void alert(String message){
        if(mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext.get(), message, mLength);
        mToast.show();
    }

    private void setLength(int length){
        if(length < 0){
            throw new IllegalArgumentException("Message duration length cannot be less than zero.");
        }
        mLength = length;
    }
}
