package com.example.tictactoe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MultiplayerGameActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mPlayerScore;
    private TextView mOpponentScore;
    private TextView mStatus;
    MultiplayerGameManager manager;
    ProgressDialog mSearchingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new MultiplayerGameManager();
        manager.initialize(MultiplayerGameActivity.this, getIntent().getBooleanExtra("IS_HOST", false));

        manager.registerCell(R.id.btn_1, new GridCell((Button)findViewById(R.id.btn_1), 0, 0, this));
        manager.registerCell(R.id.btn_2, new GridCell((Button)findViewById(R.id.btn_2), 0, 1, this));
        manager.registerCell(R.id.btn_3, new GridCell((Button)findViewById(R.id.btn_3), 0, 2, this));
        manager.registerCell(R.id.btn_4, new GridCell((Button)findViewById(R.id.btn_4), 1, 0, this));
        manager.registerCell(R.id.btn_5, new GridCell((Button)findViewById(R.id.btn_5), 1, 1, this));
        manager.registerCell(R.id.btn_6, new GridCell((Button)findViewById(R.id.btn_6), 1, 2, this));
        manager.registerCell(R.id.btn_7, new GridCell((Button)findViewById(R.id.btn_7), 2, 0, this));
        manager.registerCell(R.id.btn_8, new GridCell((Button)findViewById(R.id.btn_8), 2, 1, this));
        manager.registerCell(R.id.btn_9, new GridCell((Button)findViewById(R.id.btn_9), 2, 2, this));

        mPlayerScore = findViewById(R.id.player_1_score);
        mOpponentScore = findViewById(R.id.player_2_score);
        mStatus = findViewById(R.id.status);
        mStatus.setKeepScreenOn(true);
    }

    @Override
    public void onClick(View v) {
        if(manager.getGameState() == GameState.Finished){
            manager.sendRestartRequest();
            return;
        }
        registerMove(v.getId());
    }

    private void registerMove(int id){
        new GameTask(manager).execute(id);
    }

    @Subscribe
    public void registerOpponentMove(CellIdEvent event){
        new OpponentMoveGameTask(manager).execute(event.getCellId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        manager.stopAllEndpoints();
        manager.resetGame();
        super.onStop();
    }

    @Subscribe
    public void showProgressDialog(SearchingForDevicesEvent event){
        if(event.isSearching()){
            final Context context = this;
            mSearchingDialog = ProgressDialog.show(this, "Searching for game rooms...", "Press back to cancel",
                    true, true, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            manager.stopDiscovery();
                            Toast.makeText(context, "Searching for other devices canceled", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            mSearchingDialog.dismiss();
        }
    }

    @Subscribe
    public void showPlayerDisocnnected(PlayerDisconnectedEvent event){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your opponent has disconnected!")
                .setMessage("You will be returned back to the main menu.")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog playerDisconnectedDialog = builder.create();
        playerDisconnectedDialog.show();

    }

    //Updates the score depending on which figure won and update the status text.
    //If the game ended in a tie it just does the latter.
    @Subscribe
    public void updateScore(WinnerEvent event) {
        Winner winner = event.getWinner();
        if(winner != null){
            Player player = winner.getPlayer();
            player.increaseWinsCount();
            if(player.isOpponent()){
                mOpponentScore.setText(String.valueOf(player.getWinsCount()));
            }
            else {
                mPlayerScore.setText(String.valueOf(player.getWinsCount()));
            }
            mStatus.setText(String.format("%s wins!", player.getName()));
            manager.highlightWinningSequence(winner);
        }
        else {
            mStatus.setText(R.string.tie_endgame_message);
        }
    }
    @Subscribe
    public void drawFigure(GridCell cell) {
        manager.drawFigure(cell);
    }
}