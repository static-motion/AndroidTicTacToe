package com.example.tictactoe;

import android.content.Context;
import android.util.AttributeSet;

public class GridBlock extends android.support.v7.widget.AppCompatButton {
    public GridBlock(Context context) {
        super(context);
    }

    public GridBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
