package com.example.tictactoe;

public interface TicTacToeGameManager {

    void registerCell(int id, GridCell cell);

    GridCell processMove(int id);

    Winner checkForWinner();

    void highlightWinningSequence(Winner winner);

    GridCell processOpponentMove(int id);

    GameState getGameState();

    void resetGame();

}
