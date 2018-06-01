package com.example.tictactoe;

import android.content.Context;
import android.util.AttributeSet;

public class GridField extends android.support.v7.widget.AppCompatButton {
    public GridField(Context context) {
        super(context);
    }

    public GridField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
