package com.example.tictactoe.interfaces;

import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Winner;

public interface TicTacToeGameManager {

    void registerCell(int id, GridCell cell);

    CellUpdatedEvent processMove(int id);

    Winner checkForWinner();

    CellUpdatedEvent processOpponentMove(int id);

    boolean isFinished();

    void resetGame();

    void switchCurrentPlayer();

     Board getBoard();

     void registerPlayers(String opponentName);
}
