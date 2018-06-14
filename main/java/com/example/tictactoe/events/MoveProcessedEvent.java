package com.example.tictactoe.events;

public class MoveProcessedEvent {
    private final int row;
    private final int col;

    public MoveProcessedEvent(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
