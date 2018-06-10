package com.example.tictactoe;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

public class OpponentMoveGameTask extends AsyncTask <Integer, GridCell, Winner> {

    private TicTacToeGameManager mManager;

    OpponentMoveGameTask(TicTacToeGameManager manager){
        mManager = manager;
    }

    @Override
    protected Winner doInBackground(Integer... integers) {
        GridCell cell =  mManager.processOpponentMove(integers[0]);
        publishProgress(cell);
        return mManager.checkForWinner();
    }

    @Override
    protected void onProgressUpdate(GridCell... values) {
        super.onProgressUpdate(values);
        EventBus.getDefault().post(values[0]);
    }

    @Override
    protected void onPostExecute(Winner winner) {
        super.onPostExecute(winner);
        if(winner != null || mManager.getGameState() == GameState.Finished){
            EventBus.getDefault().post(new WinnerEvent(winner));
        }
    }
}
