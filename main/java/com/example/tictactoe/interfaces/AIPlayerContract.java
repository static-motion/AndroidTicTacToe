package com.example.tictactoe.interfaces;

import com.example.tictactoe.models.GridCell;

public interface AIPlayerContract {

    GridCell makeMove(com.example.tictactoe.interfaces.Board board);

    String getName();
}
