package com.example.tictactoe.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoe.R;
import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.events.DeviceFoundEvent;
import com.example.tictactoe.events.GameLobbyCreatedEvent;
import com.example.tictactoe.events.GameResetEvent;
import com.example.tictactoe.events.MoveProcessedEvent;
import com.example.tictactoe.events.OpponentMoveEvent;
import com.example.tictactoe.events.PlayerDisconnectedEvent;
import com.example.tictactoe.events.SearchingForDevicesEvent;
import com.example.tictactoe.events.WinnerEvent;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Player;
import com.example.tictactoe.models.Winner;
import com.example.tictactoe.tasks.ProcessMoveTask;
import com.example.tictactoe.tasks.ProcessOpponentMoveTask;
import com.example.tictactoe.utils.ConnectionManager;
import com.example.tictactoe.utils.MultiplayerGameManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Queue;

public class MultiplayerGameActivity extends AppCompatActivity{

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
    private MultiplayerGameManager manager;
    private ProgressDialog mSearchingDialog;
    private TextView mPlayer;
    private TextView mOpponent;
    private final String TAG = getClass().getSimpleName();
    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(manager.getGameState() == GameState.Finished){
                mConnectionManager.sendRestartRequest();
                return;
            }
            processMove(v.getId());
        }
    };
    private ConnectionManager mConnectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_game);
        loadUI();
    }


    private void loadUI() {
        mStatus = findViewById(R.id.status);
        mStatus.setKeepScreenOn(true);
        mPlayer = findViewById(R.id.player_1);
        mPlayer.setText(getIntent().getStringExtra("PLAYER_NAME"));
    }

    private void loadLazyUI() {
        mPlayerScore = findViewById(R.id.player_1_score);
        mOpponentScore = findViewById(R.id.player_2_score);
        mOpponent = findViewById(R.id.player_2);
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

    private void setupGame() {
        new Thread(){
            @Override
            public void run() {
                boolean isHost = getIntent().getBooleanExtra("IS_HOST", false);
                mConnectionManager = new ConnectionManager(mPlayer.getText().toString(), getApplicationContext());

                if(isHost){
                    mConnectionManager.startAdvertising();
                }
                else {
                    mConnectionManager.startDiscovery();
                }

                manager = new MultiplayerGameManager(isHost, mPlayer.getText().toString());
                manager = new MultiplayerGameManager(isHost, mPlayer.getText().toString());

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
        }.start();
    }

    private void processMove(int id){
        new ProcessMoveTask(manager).execute(id);
    }

    @Subscribe
    public void onMoveProcessed(MoveProcessedEvent event){
        mConnectionManager.sendMoveCoordinates(event.getRow(), event.getCol());
    }

    @Subscribe
    public void onOpponentMoveReceived(OpponentMoveEvent event){
        int id = manager.parseOpponentMove(event.getCellId());
        new ProcessOpponentMoveTask(manager).execute(id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupGame();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        manager.resetGame();
        mConnectionManager.shutdown();
        super.onStop();
        finish();
    }

    @Subscribe
    public void onSearchingForDevices(SearchingForDevicesEvent event){
        mSearchingDialog = ProgressDialog.show(this, "Searching for other devices...", "Press back to cancel",
                true, true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mConnectionManager.stopDiscovery();
                        Toast.makeText(MultiplayerGameActivity.this,
                                "Searching for other devices canceled", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Subscribe
    public void onDeviceFound(final DeviceFoundEvent event){
        if (mSearchingDialog != null && mSearchingDialog.isShowing()) {
            mSearchingDialog.dismiss();
        }
        mConnectionManager.stopDiscovery();
        mConnectionManager.stopAdvertising();
        new AlertDialog.Builder(this)
                .setTitle("Accept connection to " + event.getEndpointName())
                .setMessage("Confirm the code " + event.getAuthenticationToken() + " is also displayed on the other device.")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The user confirmed, so we can accept the connection.
                        mConnectionManager.connect(event.getEndpointId(),event.getEndpointName());
                        manager.registerPlayers(event.getEndpointName());
                        loadLazyUI();
                        mOpponent.setText(event.getEndpointName());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The user canceled, so we should reject the connection.
                        mConnectionManager.rejectConnection(event.getEndpointId());
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Subscribe
    public void OnGameLobbyCreated(GameLobbyCreatedEvent event){
        Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onPlayerDisconnected(PlayerDisconnectedEvent event){
        new AlertDialog.Builder(this)
                .setTitle(event.getPlayerName() + " has disconnected!")
                .setMessage("You will be returned back to the menu.")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
        .setIcon(android.R.drawable.ic_dialog_alert)
        .create()
        .show();
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

    @Subscribe
    public void resetBoard(GameResetEvent event){
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