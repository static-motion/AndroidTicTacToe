package com.example.tictactoe.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.tictactoe.R;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.events.MoveProcessedEvent;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.interfaces.AIPlayerContract;
import com.example.tictactoe.interfaces.GameManagerContract;
import com.example.tictactoe.models.Board;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Player;
import com.example.tictactoe.models.Winner;
import com.example.tictactoe.tasks.OpponentMoveTask;
import com.example.tictactoe.tasks.SinglePlayerMoveTask;
import com.example.tictactoe.utils.GameManager;
import com.example.tictactoe.utils.MinimaxAIPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private AIPlayerContract mAIPlayerContract;
    protected int[][] mIds = {{R.id.btn_1, R.id.btn_2 ,R.id.btn_3},
                            {R.id.btn_4, R.id.btn_5, R.id.btn_6},
                            {R.id.btn_7, R.id.btn_8, R.id.btn_9}};
    protected Button[][] mButtons = new Button[3][3];
    protected TextView mPlayerScore;
    protected TextView mOpponentScore;
    protected TextView mStatus;
    protected TextView mPlayer;
    protected TextView mOpponent;
    protected String mPlayerName;
    protected GameManagerContract manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_game);
        configureActivity();
    }

    protected void setupGame(String opponentName) {
        manager = new GameManager(Board.getInstance());
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                manager.registerCell(mIds[row][col], new GridCell(row, col));
                mButtons[row][col] = findViewById(mIds[row][col]);
                mButtons[row][col].setOnClickListener(this);
            }
        }
        manager.registerPlayers(mPlayerName, opponentName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    protected void configureActivity() {
        mStatus = findViewById(R.id.status);
        mStatus.setKeepScreenOn(true);
        mPlayer = findViewById(R.id.player_1);
        mPlayer.setText(getIntent().getStringExtra("PLAYER_NAME"));
        mPlayerScore = findViewById(R.id.player_1_score);
        mOpponentScore = findViewById(R.id.player_2_score);
        mOpponent = findViewById(R.id.player_2);
        mAIPlayerContract = new MinimaxAIPlayer();
        mOpponent.setText(mAIPlayerContract.getName());
        setupGame(mAIPlayerContract.getName());
    }

    protected void processMove(int id){
        new SinglePlayerMoveTask(manager).execute(id);
    }

    @Subscribe
    public void onMoveProcessed(MoveProcessedEvent event){
        new Thread(){
            @Override
            public void run() {
                GridCell cell = mAIPlayerContract.makeMove(Board.getInstance());
                int id = mIds[cell.getRow()][cell.getCol()];
                new OpponentMoveTask(manager).execute(id);
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
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
            highlightWinningSequence(winner);
        }
        else {
            mStatus.setText(R.string.tie_endgame_message);
        }
    }

    public void highlightWinningSequence(Winner winner) {
        Player player = winner.getPlayer();
        int[] coordinates = winner.getWinningStreakCoordinates();
        for (int i = 0; i < coordinates.length - 1; i += 2) {
            mButtons[coordinates[i]][coordinates[i + 1]]
                    .setBackgroundResource(player.getPlayerFigure().getHighlightedFigure());
        }
    }

    public void resetBoard(){
        manager.resetGame();
        for (Button[] mButton : mButtons) {
            for (int col = 0; col < 3; col++) {
                mButton[col].setBackgroundResource(R.color.colorTransparent);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void drawFigure(CellUpdatedEvent event) {
        mButtons[event.getCell().getRow()][event.getCell().getCol()]
                .setBackgroundResource(event.getPlayerFigure());
    }

    @Override
    public void onClick(View v) {
        if(manager.isFinished()){
            resetBoard();
            return;
        }
        processMove(v.getId());
    }
}