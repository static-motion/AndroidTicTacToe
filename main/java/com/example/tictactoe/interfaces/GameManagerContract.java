package com.example.tictactoe.interfaces;

import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Winner;

public interface GameManagerContract {

    void registerCell(int id, GridCell cell);

    CellUpdatedEvent processMove(int id);

    CellUpdatedEvent processOpponentMove(int id);

    Winner checkForWinner(int row, int col);

    void registerPlayers(String playerName, String opponentName);

    void resetGame();

    void switchCurrentPlayer();

    boolean isFinished();
}
