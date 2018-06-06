package com.example.tictactoe;

public class CellIdEvent {
    private int mCellId;

    CellIdEvent(int cellId) {
        this.mCellId = cellId;
    }

    public int getCellId() {
        return mCellId;
    }
}
