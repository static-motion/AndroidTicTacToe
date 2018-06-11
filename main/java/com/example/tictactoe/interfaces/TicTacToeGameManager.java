package com.example.tictactoe.interfaces;

import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Winner;

public interface TicTacToeGameManager {

    void registerCell(int id, GridCell cell);

    GridCell processMove(int id);

    Winner checkForWinner();

    void highlightWinningSequence(Winner winner);

    GridCell processOpponentMove(int id);

    GameState getGameState();

    void resetGame();

    boolean isOpponentsTurn();
}
