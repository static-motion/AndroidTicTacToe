package com.example.tictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private final char mCrossChar = 'x';
    private final char mCircleChar = 'o';
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

        mCrossesScore = findViewById(R.id.crossesScore);
        mNaughtsScore = findViewById(R.id.naughtsScore);
        mStatus = findViewById(R.id.status);
    }

    @Override
    public void onClick(View v) {
        if(manager.getGameState() == GameState.Finished){
            manager.resetGame();
            return;
        }

        Winner winner = manager.processMove(v.getId());
        //Update the UI if a winner is found or 9 turns have passed (the maximum possible in tic tac toe)
        if(winner != null || manager.getGameState() == GameState.Finished){
            updateScore(winner);
        }
    }

    //Updates the score depending on which figure won and update the status text.
    //If the game ended in a tie it just does the latter.
    private void updateScore(Winner winner) {
        if(winner != null){
            if(winner.getWinningPiece() == mCrossChar){
                mCrossesScore.setText(String.valueOf(++mCrossesWinsCount));
                mStatus.setText(R.string.crosses_win_message);
                manager.highlightWinningSequence(winner);
            }
            else if(winner.getWinningPiece() == mCircleChar){
                mNaughtsScore.setText(String.valueOf(++mNaughtsWinsCount));
                mStatus.setText(R.string.naughts_win_message);
                manager.highlightWinningSequence(winner);
            }
        }
        else {
            mStatus.setText(R.string.tie_endgame_message);
        }
    }

}