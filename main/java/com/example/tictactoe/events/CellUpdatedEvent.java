package com.example.tictactoe.events;

public class CellUpdatedEvent {
    private int cellId;
    private int playerFigure;

    public CellUpdatedEvent(int cellId, int playerFigure) {
        this.cellId = cellId;
        this.playerFigure = playerFigure;
    }

    public int getCellId() {
        return cellId;
    }

    public int getPlayerFigure() {
        return playerFigure;
    }
}
