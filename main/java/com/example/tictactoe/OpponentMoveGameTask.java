package com.example.tictactoe;

import android.os.AsyncTask;

public class OpponentMoveGameTask extends AsyncTask <Integer, GridCell, Winner> {
    private UserInterface userInterface;
    private MultiplayerGameManager mManager;

    OpponentMoveGameTask(UserInterface ui, MultiplayerGameManager manager){
        this.userInterface = ui;
        mManager = manager;
    }

    @Override
    protected Winner doInBackground(Integer... integers) {
        GridCell cell =  mManager.processOpponentMove(integers[0]);
        publishProgress(cell);
        return mManager.checkForWinner();
    }

    @Override
    protected void onPostExecute(Winner winner) {
        super.onPostExecute(winner);
        if(winner != null || mManager.getGameState() == GameState.Finished){
            userInterface.updateScore(winner);
        }
    }

    @Override
    protected void onProgressUpdate(GridCell... values) {
        super.onProgressUpdate(values);
        userInterface.drawFigure(values[0]);
    }
}
