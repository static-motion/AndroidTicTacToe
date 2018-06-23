package com.example.tictactoe.events;

import com.example.tictactoe.models.GridCell;

public class CellUpdatedEvent {
    private int playerFigure;
    private GridCell cell;

    public CellUpdatedEvent(GridCell cell, int playerFigure) {
        this.cell = cell;
        this.playerFigure = playerFigure;
    }

    public int getPlayerFigure() {
        return playerFigure;
    }

    public GridCell getCell() {
        return cell;
    }
}
