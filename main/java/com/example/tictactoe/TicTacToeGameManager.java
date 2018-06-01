package com.example.tictactoe;

public interface TicTacToeGameManager {

    void registerCell(int id, GridCell cell);

    Winner processMove(int id);

    Winner checkForWinner();

    void highlightWinningSequence(Winner winner);

    GameState getGameState();

    void resetGame();

}
