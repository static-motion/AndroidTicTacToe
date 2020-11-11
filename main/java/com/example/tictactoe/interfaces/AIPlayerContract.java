package com.example.tictactoe.interfaces;

import com.example.tictactoe.models.GridCell;

public interface AIPlayerContract {

    GridCell makeMove(BoardContract board);

    String getName();
}
