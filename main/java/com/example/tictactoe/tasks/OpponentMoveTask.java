package com.example.tictactoe.tasks;

import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.models.Winner;

public class OpponentMoveTask extends MoveTask {

    public OpponentMoveTask(TicTacToeGameManager manager){
        super(manager);
    }

    @Override
    protected CellUpdatedEvent processMove(int id) {
        return mManager.processOpponentMove(id);
    }
}
