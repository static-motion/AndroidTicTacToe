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
import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Player;
import com.example.tictactoe.models.Winner;
import com.example.tictactoe.tasks.ProcessMoveTask;
import com.example.tictactoe.utils.GameManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Queue;

public class GameActivity extends AppCompatActivity{

    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private TextView mPlayerScore;
    private TextView mOpponentScore;
    private TextView mStatus;
    private TextView mPlayer;
    private TextView mOpponent;
    GameManager manager;
    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(manager.getGameState() == GameState.Finished){
                resetBoard();
                return;
            }
            processMove(v.getId());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_game);
        configureUI();
        setupManager();
    }

    private void setupManager() {
        manager = new GameManager();
        manager.registerCell(R.id.btn_1, new GridCell(0, 0));
        manager.registerCell(R.id.btn_2, new GridCell(0, 1));
        manager.registerCell(R.id.btn_3, new GridCell(0, 2));
        manager.registerCell(R.id.btn_4, new GridCell(1, 0));
        manager.registerCell(R.id.btn_5, new GridCell(1, 1));
        manager.registerCell(R.id.btn_6, new GridCell(1, 2));
        manager.registerCell(R.id.btn_7, new GridCell(2, 0));
        manager.registerCell(R.id.btn_8, new GridCell(2, 1));
        manager.registerCell(R.id.btn_9, new GridCell(2, 2));
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

    private void configureUI() {
        mStatus = findViewById(R.id.status);
        mStatus.setKeepScreenOn(true);
        mPlayer = findViewById(R.id.player_1);
        mPlayer.setText("CROSSES");
        mPlayerScore = findViewById(R.id.player_1_score);
        mOpponentScore = findViewById(R.id.player_2_score);
        mOpponent = findViewById(R.id.player_2);
        mOpponent.setText("NAUGHTS");
        mButton1 = findViewById(R.id.btn_1);
        mButton2 = findViewById(R.id.btn_2);
        mButton3 = findViewById(R.id.btn_3);
        mButton4 = findViewById(R.id.btn_4);
        mButton5 = findViewById(R.id.btn_5);
        mButton6 = findViewById(R.id.btn_6);
        mButton7 = findViewById(R.id.btn_7);
        mButton8 = findViewById(R.id.btn_8);
        mButton9 = findViewById(R.id.btn_9);
        mButton1.setOnClickListener(mListener);
        mButton2.setOnClickListener(mListener);
        mButton3.setOnClickListener(mListener);
        mButton4.setOnClickListener(mListener);
        mButton5.setOnClickListener(mListener);
        mButton6.setOnClickListener(mListener);
        mButton7.setOnClickListener(mListener);
        mButton8.setOnClickListener(mListener);
        mButton9.setOnClickListener(mListener);
    }

    private void processMove(int id){
        new ProcessMoveTask(manager).execute(id);
    }

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
            mStatus.setText(String.format("%s win!", player.getName()));
            highlightWinningSequence(winner);
        }
        else {
            mStatus.setText(R.string.tie_endgame_message);
        }
    }

    public void highlightWinningSequence(Winner winner) {
        Player player = winner.getPlayer();
        int[] coordinates = winner.getWinningStreakCoordinates();
        int id;
        for (int i = 0; i < coordinates.length - 1; i += 2) {
            id = manager.getIdWithCoordinates(coordinates[i], coordinates[i + 1]);
            findViewById(id).setBackgroundResource(player.getPlayerFigure().getHighlightedFigure());
        }
    }

    public void resetBoard(){
        Queue<Integer> ids = manager.resetGame();
        int size = ids.size();
        for (int i = 0; i < size; i++) {
            findViewById(ids.remove()).setBackgroundResource(R.color.colorTransparent);
        }
    }

    @Subscribe
    public void drawFigure(CellUpdatedEvent event) {
        findViewById(event.getCellId()).setBackgroundResource(event.getPlayerFigure());
    }
}