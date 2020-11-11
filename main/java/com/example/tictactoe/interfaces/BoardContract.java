package com.example.tictactoe.interfaces;

import com.example.tictactoe.models.GridCell;

import java.util.ArrayList;

public interface BoardContract {

    void setMove(int row, int col, char figure);

    boolean isTaken(int row, int col);

    char figureAt(int row, int col);

    void resetPosition(int row, int col);

	ArrayList<GridCell> availableMoves();
}
