package com.example.tictactoe.tasks;

import android.os.AsyncTask;

import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.models.Winner;

import org.greenrobot.eventbus.EventBus;

public class MoveTask extends AsyncTask<Integer, CellUpdatedEvent, Winner> {

    TicTacToeGameManager mManager;
    boolean wasValid = true;
    public MoveTask(TicTacToeGameManager manager){
        mManager = manager;
    }

    @Override
    protected Winner doInBackground(Integer... integers) {
        CellUpdatedEvent event = processMove(integers[0]);
        if(event == null){
            wasValid = false;
            return null;
        }
        publishProgress(event);
        return mManager.checkForWinner();
    }

    protected CellUpdatedEvent processMove(int id) {
        return mManager.processMove(id);
    }

    @Override
    protected void onPostExecute(Winner winner) {
        if(winner != null || mManager.isFinished()){
            EventBus.getDefault().post(new WinnerEvent(winner));
        }
        if(wasValid){
            mManager.switchCurrentPlayer();
        }
    }

    @Override
    protected void onProgressUpdate(CellUpdatedEvent... values) {
        EventBus.getDefault().post(values[0]);
    }
}