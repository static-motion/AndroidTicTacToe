package com.example.tictactoe.tasks;

import com.example.tictactoe.events.MoveProcessedEvent;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.models.Winner;

import org.greenrobot.eventbus.EventBus;

public class SinglePlayerMoveTask extends MoveTask {
    public SinglePlayerMoveTask(TicTacToeGameManager manager) {
        super(manager);
    }

    @Override
    protected void onPostExecute(Winner winner) {
        if(winner != null || mManager.isFinished()){
            EventBus.getDefault().post(new WinnerEvent(winner));
        }
        else if(wasValid){
            EventBus.getDefault().post(new MoveProcessedEvent(0,0));
            mManager.switchCurrentPlayer();
        }
    }
}
