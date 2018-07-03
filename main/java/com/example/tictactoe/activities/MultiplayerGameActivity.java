package com.example.tictactoe.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;

import com.example.tictactoe.R;
import com.example.tictactoe.events.DeviceConnectedEvent;
import com.example.tictactoe.events.DeviceFoundEvent;
import com.example.tictactoe.events.GameLobbyCreatedEvent;
import com.example.tictactoe.events.GameResetEvent;
import com.example.tictactoe.events.MoveProcessedEvent;
import com.example.tictactoe.events.OpponentMoveEvent;
import com.example.tictactoe.events.PlayerDisconnectedEvent;
import com.example.tictactoe.events.SearchingForDevicesEvent;
import com.example.tictactoe.models.Board;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.tasks.MoveTask;
import com.example.tictactoe.tasks.OpponentMoveTask;
import com.example.tictactoe.utils.connectivity.ConnectionManager;
import com.example.tictactoe.utils.Messenger;
import com.example.tictactoe.utils.game.MultiplayerGameManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MultiplayerGameActivity extends GameActivity{

    private final String TAG = getClass().getSimpleName();
    private ConnectionManager mConnectionManager;
    private ProgressDialog mSearchingDialog;
    private boolean mIsHost;
    private Messenger mMessenger;

    @Override
    public void onClick(View v) {
        if(manager.isFinished()){
            mConnectionManager.sendRestartRequest();
            return;
        }
        processMove(v.getId());
    }

    @Override
    protected void configureActivity() {
        mStatus = findViewById(R.id.status);
        mStatus.setKeepScreenOn(true);
        mPlayer = findViewById(R.id.player_1);
        mPlayerName = getIntent().getStringExtra("PLAYER_NAME");
        mPlayer.setText(mPlayerName);
        mPlayerScore = findViewById(R.id.player_1_score);
        mOpponentScore = findViewById(R.id.player_2_score);
        mOpponent = findViewById(R.id.player_2);
        mMessenger = new Messenger(this);
    }

    protected void setupConnectivity() {
        new Thread(){
            @Override
            public void run() {
                mIsHost = getIntent().getBooleanExtra("IS_HOST", false);
                mConnectionManager = new ConnectionManager(mPlayer.getText().toString(), getApplicationContext());
                if(mIsHost){
                    mConnectionManager.startAdvertising();
                }
                else {
                    mConnectionManager.startDiscovery();
                }
            }
        }.start();
    }

    @Override
    protected void processMove(int id){
        new MoveTask(manager).execute(id);
    }

    @Subscribe
    @Override
    public void onMoveProcessed(MoveProcessedEvent event){
        mConnectionManager.sendMoveCoordinates(event.getRow(), event.getCol());
    }

    @Subscribe
    public void onOpponentMoveReceived(OpponentMoveEvent event){
        byte[] coords = event.getCoordinates();
        int id = mIds[coords[0]][coords[1]];
        new OpponentMoveTask(manager).execute(id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupConnectivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mConnectionManager.shutdown();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchingForDevices(SearchingForDevicesEvent event){
        mSearchingDialog = new ProgressDialog(this, R.style.AppTheme_ProgressDialog);
        mSearchingDialog.setCancelable(true);
        mSearchingDialog.setIndeterminate(true);
        mSearchingDialog.setTitle("Searching for other devices...");
        mSearchingDialog.setCanceledOnTouchOutside(true);
        mSearchingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mConnectionManager.shutdown();
                mMessenger.alert("Cancelled.");
            }
        });
        mSearchingDialog.setMessage("Press back to cancel.");
        mSearchingDialog.show();
    }

    @Override
    protected void setupGame(String opponentName) {

        manager = new MultiplayerGameManager(mIsHost, Board.getInstance());
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                manager.registerCell(mIds[row][col], new GridCell(row, col));
                mButtons[row][col] = findViewById(mIds[row][col]);
                mButtons[row][col].setOnClickListener(this);
            }
        }
        manager.registerPlayers(mPlayerName, opponentName);
    }

    @Subscribe
    public void onDeviceFound(final DeviceFoundEvent event){
        if (mSearchingDialog != null && mSearchingDialog.isShowing()) {
            mSearchingDialog.dismiss();
        }
        mConnectionManager.stopDiscovery();
        mConnectionManager.stopAdvertising();
        new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setTitle("Accept connection to " + event.getEndpointName())
                .setMessage("Confirm the code " + event.getAuthenticationToken() + " is also displayed on the other device.")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The user confirmed, so we can accept the connection.
                        mConnectionManager.connect(event.getEndpointId(),event.getEndpointName());
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
    public void onDeviceConnected(DeviceConnectedEvent event){
        mOpponent.setText(event.getEndpointName());
        setupGame(event.getEndpointName());
    }

    @Subscribe
    public void OnGameLobbyCreated(GameLobbyCreatedEvent event){
        mMessenger.alert(event.getMessage());
    }

    @Subscribe
    public void onPlayerDisconnected(PlayerDisconnectedEvent event){
        new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
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

    @Subscribe
    public void resetBoard(GameResetEvent event){
        super.resetBoard();
    }
}