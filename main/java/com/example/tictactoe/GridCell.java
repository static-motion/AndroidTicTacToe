package com.example.tictactoe;

import android.view.View;
import android.widget.Button;

//Button wrapper class. It contains the button's coordinates in the grid
//allowing for a convenient way to update the game board matrices.

public class GridCell {

    private Button mCell;
    private int mRow;
    private int mCol;

    GridCell(Button button, int row, int col, View.OnClickListener listener){
        mCell = button;
        mRow = row;
        mCol = col;
        this.setOnClickListener(listener);
    }

    public int getRow(){
        return mRow;
    }

    public int getCol(){
        return mCol;
    }

    public Button getCell() {
        return mCell;
    }

    private void setOnClickListener(View.OnClickListener listener){
        mCell.setOnClickListener(listener);
    }
}
