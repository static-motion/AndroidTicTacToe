package com.example.tictactoe.tasks;

import android.os.AsyncTask;

import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.models.Winner;

import org.greenrobot.eventbus.EventBus;

public class ProcessMoveTask extends AsyncTask<Integer, CellUpdatedEvent, Winner> {

    TicTacToeGameManager mManager;

    public ProcessMoveTask(TicTacToeGameManager manager){
        mManager = manager;
    }

    @Override
    protected Winner doInBackground(Integer... integers) {
        CellUpdatedEvent event = mManager.processMove(integers[0]);
        if(event == null){
            return null;
        }
        publishProgress(event);
        return mManager.checkForWinner();
    }

    @Override
    protected void onPostExecute(Winner winner) {
        if(winner != null || mManager.getGameState() == GameState.Finished){
            EventBus.getDefault().post(new WinnerEvent(winner));
        }
    }

    @Override
    protected void onProgressUpdate(CellUpdatedEvent... values) {
        EventBus.getDefault().post(values[0]);
    }
}
