package com.example.tictactoe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MultiplayerGameActivity extends AppCompatActivity implements View.OnClickListener, UserInterface{

    private TextView mCrossesScore;
    private TextView mNaughtsScore;
    private TextView mStatus;
    MultiplayerGameManager manager;
    OpponentMoveReceiver mMoveReceiver;
    public static final String BROADCAST_ACTION = "com.example.tictactoe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.AppTheme);
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

        mCrossesScore = findViewById(R.id.crossesScore);
        mNaughtsScore = findViewById(R.id.naughtsScore);
        mStatus = findViewById(R.id.status);
        registerMoveReceiver();
        startService(new Intent(this, MultiplayerGameManager.class));
    }

    private void registerMoveReceiver() {
        mMoveReceiver = new OpponentMoveReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
        registerReceiver(mMoveReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        if(manager.getGameState() == GameState.Finished){
            manager.resetGame();
            return;
        }
        registerMove(v.getId());
    }

    private void registerMove(int id){
        new GameTask(this, manager).execute(id);
    }

    private void registerOpponentMove(int id){
        new OpponentMoveGameTask(this, manager).execute(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMoveReceiver);
    }

    @Override
    protected void onStop() {
        manager.stopAllEndpoints();
        manager.resetGame();
        super.onStop();
    }

    //Updates the score depending on which figure won and update the status text.
    //If the game ended in a tie it just does the latter.
    public void updateScore(Winner winner) {
        if(winner != null){
            Player player = winner.getPlayer();
            mCrossesScore.setText(String.valueOf(player.getWinsCount()));
            mStatus.setText(String.format("%s wins!", player.getName()));
            manager.highlightWinningSequence(winner);
        }
        else {
            mStatus.setText(R.string.tie_endgame_message);
        }
    }

    public void drawFigure(GridCell cell) {
        manager.drawFigure(cell);
    }

    private class OpponentMoveReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int cellId = intent.getIntExtra("CELL_ID", -1);
            registerOpponentMove(cellId);
        }
    }
}