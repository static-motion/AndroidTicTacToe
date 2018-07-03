package com.example.tictactoe.interfaces;

public interface Board {

    void setMove(int row, int col, char figure);

    boolean isTaken(int row, int col);

    char figureAt(int row, int col);

    void resetPosition(int row, int col);
}
