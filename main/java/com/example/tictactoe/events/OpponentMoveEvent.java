package com.example.tictactoe.events;

public class OpponentMoveEvent {
    private int mCellId;

    public OpponentMoveEvent(int cellId) {
        this.mCellId = cellId;
    }

    public int getCellId() {
        return mCellId;
    }
}
