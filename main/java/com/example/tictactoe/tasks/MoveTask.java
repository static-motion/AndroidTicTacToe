package com.example.tictactoe.tasks;

import android.os.AsyncTask;

import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.interfaces.GameManagerContract;
import com.example.tictactoe.models.Winner;

import org.greenrobot.eventbus.EventBus;

public class MoveTask extends AsyncTask<Integer, CellUpdatedEvent, Void> {

    GameManagerContract mManager;
    boolean wasValid = true;
    public MoveTask(GameManagerContract manager){
        mManager = manager;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        CellUpdatedEvent event = processMove(integers[0]);
        if(event == null){
            wasValid = false;
            return null;
        }
        publishProgress(event);
        checkForWinner();
        return null;
    }

    void checkForWinner() {
        Winner winner = mManager.checkForWinner();
        if(winner != null || mManager.isFinished()){
            EventBus.getDefault().post(new WinnerEvent(winner));
        }
        if(wasValid){
            mManager.switchCurrentPlayer();
        }
    }

    protected CellUpdatedEvent processMove(int id) {
        return mManager.processMove(id);
    }

    @Override
    protected void onProgressUpdate(CellUpdatedEvent... values) {
        EventBus.getDefault().post(values[0]);
    }
}
