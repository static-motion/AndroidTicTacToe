package com.example.tictactoe.tasks;

import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.interfaces.GameManagerContract;

public class OpponentMoveTask extends MoveTask {

    public OpponentMoveTask(GameManagerContract manager){
        super(manager);
    }

    @Override
    protected CellUpdatedEvent processMove(int id) {
        return mManager.processOpponentMove(id);
    }
}
