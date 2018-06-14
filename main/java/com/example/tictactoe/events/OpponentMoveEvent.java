package com.example.tictactoe.events;

public class OpponentMoveEvent {
    private String mCellId;

    public OpponentMoveEvent(String cellId) {
        this.mCellId = cellId;
    }

    public String getCellId() {
        return mCellId;
    }
}
