package com.example.tictactoe.interfaces;

import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Winner;

import java.util.Queue;

public interface TicTacToeGameManager {

    void registerCell(int id, GridCell cell);

    CellUpdatedEvent processMove(int id);

    Winner checkForWinner();

    CellUpdatedEvent processOpponentMove(int id);

    GameState getGameState();

    Queue<Integer> resetGame();

    boolean isOpponentsTurn();
}
