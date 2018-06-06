package com.example.tictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private int mCrossesWinsCount = 0;
    private int mNaughtsWinsCount = 0;
    private TextView mNaughtsScore;
    private TextView mCrossesScore;
    private TextView mStatus;
    TicTacToeGameManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpGameManager();
        mCrossesScore = findViewById(R.id.player_1_score);
        mNaughtsScore = findViewById(R.id.player_2_score);
        mStatus = findViewById(R.id.status);
    }

    private void setUpGameManager() {
        manager = (GameManager) getIntent().getSerializableExtra("GAME_MANAGER");

        manager.registerCell(R.id.btn_1, new GridCell((Button)findViewById(R.id.btn_1), 0, 0, this));
        manager.registerCell(R.id.btn_2, new GridCell((Button)findViewById(R.id.btn_2), 0, 1, this));
        manager.registerCell(R.id.btn_3, new GridCell((Button)findViewById(R.id.btn_3), 0, 2, this));
        manager.registerCell(R.id.btn_4, new GridCell((Button)findViewById(R.id.btn_4), 1, 0, this));
        manager.registerCell(R.id.btn_5, new GridCell((Button)findViewById(R.id.btn_5), 1, 1, this));
        manager.registerCell(R.id.btn_6, new GridCell((Button)findViewById(R.id.btn_6), 1, 2, this));
        manager.registerCell(R.id.btn_7, new GridCell((Button)findViewById(R.id.btn_7), 2, 0, this));
        manager.registerCell(R.id.btn_8, new GridCell((Button)findViewById(R.id.btn_8), 2, 1, this));
        manager.registerCell(R.id.btn_9, new GridCell((Button)findViewById(R.id.btn_9), 2, 2, this));
    }

    @Override
    public void onClick(View v) {
        if(manager.getGameState() == GameState.Finished){
            manager.resetGame();
        }
        //Update the UI if a winner is found or 9 turns have passed (the maximum possible in tic tac toe)
    }

    //Updates the score depending on which figure won and update the status text.
    //If the game ended in a tie it just does the latter.
    private void updateScore(Winner winner) {
        if(winner != null){
            Player player = winner.getPlayer();
            player.increaseWinsCount();
            mStatus.setText(String.format("%s won!", player.getName()));
            manager.highlightWinningSequence(winner);
            if(player.getName().equals("Crosses")){
                mCrossesScore.setText(String.valueOf(player.getWinsCount()));
            }
            else {
                mNaughtsScore.setText(String.valueOf(player.getWinsCount()));
            }
        }
        else {
            mStatus.setText(R.string.tie_endgame_message);
        }
    }

    public void drawFigure(GridCell cell) {

    }

}