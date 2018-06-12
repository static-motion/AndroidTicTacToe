package com.example.tictactoe.activities;

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

import com.example.tictactoe.events.DeviceFoundEvent;
import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.utils.MultiplayerGameManager;
import com.example.tictactoe.events.OpponentMoveEvent;
import com.example.tictactoe.models.Player;
import com.example.tictactoe.events.PlayerDisconnectedEvent;
import com.example.tictactoe.tasks.ProcessMoveTask;
import com.example.tictactoe.R;
import com.example.tictactoe.events.SearchingForDevicesEvent;
import com.example.tictactoe.models.Winner;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.tasks.ProcessOpponentMoveTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MultiplayerGameActivity extends AppCompatActivity{

    private TextView mPlayerScore;
    private TextView mOpponentScore;
    private TextView mStatus;
    MultiplayerGameManager manager;
    ProgressDialog mSearchingDialog;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(manager.getGameState() == GameState.Finished){
                manager.sendRestartRequest();
                return;
            }
            ProcessMove(v.getId());
        }
    };
    private TextView mPlayer;
    private TextView mOpponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_game);
        setupManager();
        mPlayerScore = findViewById(R.id.player_1_score);
        mOpponentScore = findViewById(R.id.player_2_score);
        mStatus = findViewById(R.id.status);
        mStatus.setKeepScreenOn(true);
        mPlayer = findViewById(R.id.player_1);
        mOpponent = findViewById(R.id.player_2);
        mPlayer.setText(getIntent().getStringExtra("PLAYER_NAME"));
    }

    private void setupManager() {
        manager = new MultiplayerGameManager();
        manager.initialize(
                getApplicationContext(),
                getIntent().getBooleanExtra("IS_HOST", false),
                getIntent().getStringExtra("PLAYER_NAME"));

        manager.registerCell(R.id.btn_1, new GridCell((Button)findViewById(R.id.btn_1), 0, 0, listener));
        manager.registerCell(R.id.btn_2, new GridCell((Button)findViewById(R.id.btn_2), 0, 1, listener));
        manager.registerCell(R.id.btn_3, new GridCell((Button)findViewById(R.id.btn_3), 0, 2, listener));
        manager.registerCell(R.id.btn_4, new GridCell((Button)findViewById(R.id.btn_4), 1, 0, listener));
        manager.registerCell(R.id.btn_5, new GridCell((Button)findViewById(R.id.btn_5), 1, 1, listener));
        manager.registerCell(R.id.btn_6, new GridCell((Button)findViewById(R.id.btn_6), 1, 2, listener));
        manager.registerCell(R.id.btn_7, new GridCell((Button)findViewById(R.id.btn_7), 2, 0, listener));
        manager.registerCell(R.id.btn_8, new GridCell((Button)findViewById(R.id.btn_8), 2, 1, listener));
        manager.registerCell(R.id.btn_9, new GridCell((Button)findViewById(R.id.btn_9), 2, 2, listener));
    }

    private void ProcessMove(int id){
        new ProcessMoveTask(manager).execute(id);
    }

    @Subscribe
    public void registerOpponentMove(OpponentMoveEvent event){
        ProcessOpponentMove(event.getCellId());
    }

    private void ProcessOpponentMove(int cellId) {
        new ProcessOpponentMoveTask(manager).execute(cellId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        manager.resetGame();
        manager.shutdown();
        super.onStop();
        finish();
    }

    @Subscribe
    public void onDeviceFound(final DeviceFoundEvent event){
        EventBus.getDefault().post(new SearchingForDevicesEvent(false));
            new AlertDialog.Builder(this)
                    .setTitle("Accept connection to " + event.getEndpointName())
                    .setMessage("Confirm the code " + event.getAuthenticationToken() + " is also displayed on the other device.")
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The user confirmed, so we can accept the connection.
                            manager.connect(event.getEndpointId(),event.getEndpointName());
                            mOpponent.setText(event.getEndpointName());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The user canceled, so we should reject the connection.
                            manager.rejectConnection(event.getEndpointId());
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
    }

    @Subscribe
    public void onSearchingForDevices(SearchingForDevicesEvent event){
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
        else if (mSearchingDialog != null && mSearchingDialog.isShowing()) {
            mSearchingDialog.dismiss();
        }
    }

    @Subscribe
    public void OnGameLobbyCreated(GameLobbyCreatedEvent event){
        Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onPlayerDisconnected(PlayerDisconnectedEvent event){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your opponent has disconnected!")
                .setMessage("You will be returned back to the main menu.")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
        .setIcon(android.R.drawable.ic_dialog_alert);
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