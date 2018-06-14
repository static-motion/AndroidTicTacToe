package com.example.tictactoe.tasks;

import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Winner;

public class ProcessOpponentMoveTask extends ProcessMoveTask {

    public ProcessOpponentMoveTask(TicTacToeGameManager manager){
        super(manager);
    }

    @Override
    protected Winner doInBackground(Integer... integers) {
        CellUpdatedEvent cell = mManager.processOpponentMove(integers[0]);
        if(cell == null){
            return null;
        }
        publishProgress(cell);
        return mManager.checkForWinner();
    }
}
