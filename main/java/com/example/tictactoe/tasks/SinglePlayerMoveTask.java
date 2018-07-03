package com.example.tictactoe.tasks;

import com.example.tictactoe.events.MoveProcessedEvent;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.interfaces.GameManagerContract;
import com.example.tictactoe.models.Winner;

import org.greenrobot.eventbus.EventBus;

public class SinglePlayerMoveTask extends MoveTask {
    public SinglePlayerMoveTask(GameManagerContract manager) {
        super(manager);
    }

    @Override
    void checkForWinner() {
        Winner winner = mManager.checkForWinner();
        if(winner != null || mManager.isFinished()){
            EventBus.getDefault().post(new WinnerEvent(winner));
        }
        else if(wasValid){
            EventBus.getDefault().post(new MoveProcessedEvent(0,0));
            mManager.switchCurrentPlayer();
        }
    }
}
