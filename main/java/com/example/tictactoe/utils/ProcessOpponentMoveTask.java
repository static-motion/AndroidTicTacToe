package com.example.tictactoe.utils;

import android.os.AsyncTask;

import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Winner;

import org.greenrobot.eventbus.EventBus;

public class ProcessOpponentMoveTask extends AsyncTask<Integer, GridCell, Winner> {
    private TicTacToeGameManager mManager;

    public ProcessOpponentMoveTask(TicTacToeGameManager manager){
        mManager = manager;
    }

    @Override
    protected Winner doInBackground(Integer... integers) {
        GridCell cell = mManager.processOpponentMove(integers[0]);
        if(cell == null){
            return null;
        }
        publishProgress(cell);
        return mManager.checkForWinner();
    }

    @Override
    protected void onPostExecute(Winner winner) {
        super.onPostExecute(winner);
        if(winner != null || mManager.getGameState() == GameState.Finished){
            EventBus.getDefault().post(new WinnerEvent(winner));
        }
    }

    @Override
    protected void onProgressUpdate(GridCell... values) {
        super.onProgressUpdate(values);
        EventBus.getDefault().post(values[0]);
    }
}
