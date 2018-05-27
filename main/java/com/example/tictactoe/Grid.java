package com.example.tictactoe;

import android.content.Context;
import android.util.AttributeSet;

public class Grid extends android.support.v7.widget.GridLayout {
    public Grid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Grid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Grid(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        setMeasuredDimension(widthSpec, widthSpec);
        super.onMeasure(widthSpec, heightSpec);
    }


}