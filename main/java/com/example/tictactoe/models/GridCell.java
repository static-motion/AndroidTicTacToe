package com.example.tictactoe.models;

import java.io.Serializable;

public class GridCell implements Serializable {

    private int mRow;
    private int mCol;

    public GridCell(int row, int col){
        mRow = row;
        mCol = col;
    }

    public int getRow(){
        return mRow;
    }

    public int getCol(){
        return mCol;
    }

    @Override
    public String toString() {
        return String.format("Row: %d, Col: %d", mRow, mCol);
    }
}
