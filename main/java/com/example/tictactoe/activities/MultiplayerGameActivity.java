package com.example.tictactoe.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Window;
import android.widget.Toast;

import com.example.tictactoe.R;
import com.example.tictactoe.events.DeviceFoundEvent;
import com.example.tictactoe.events.GameLobbyCreatedEvent;
import com.example.tictactoe.events.GameResetEvent;
import com.example.tictactoe.events.MoveProcessedEvent;
import com.example.tictactoe.events.OpponentMoveEvent;
import com.example.tictactoe.events.PlayerDisconnectedEvent;
import com.example.tictactoe.events.SearchingForDevicesEvent;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.tasks.MoveTask;
import com.example.tictactoe.tasks.OpponentMoveTask;
import com.example.tictactoe.utils.ConnectionManager;
import com.example.tictactoe.utils.MultiplayerGameManager;

import org.greenrobot.eventbus.Subscribe;

public class MultiplayerGameActivity extends GameActivity{

    private final String TAG = getClass().getSimpleName();
    private ConnectionManager mConnectionManager;
    private ProgressDialog mSearchingDialog;
    private boolean mIsHost;

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
        mPlayerScore = findViewById(R.id.player_1_score);
        mOpponentScore = findViewById(R.id.player_2_score);
        mOpponent = findViewById(R.id.player_2);
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

    private void processMove(int id){
        new MoveTask(manager).execute(id);
    }

    @Subscribe
    @Override
    public void onMoveProcessed(MoveProcessedEvent event){
        mConnectionManager.sendMoveCoordinates(event.getRow(), event.getCol());
    }

    @Subscribe
    public void onOpponentMoveReceived(OpponentMoveEvent event){
        int id = parseOpponentMove(event.getCellId());
        new OpponentMoveTask(manager).execute(id);
    }

    private int parseOpponentMove(String coordinates) {
        int row = Character.getNumericValue(coordinates.charAt(0));
        int col = Character.getNumericValue(coordinates.charAt(1));
        return mIds[row][col];
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

    @Override
    protected void setupGame(String opponentName) {
        manager = new MultiplayerGameManager(mIsHost, "Goshkata");
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                manager.registerCell(mIds[row][col], new GridCell(row, col));
                mButtons[row][col] = findViewById(mIds[row][col]);
                mButtons[row][col].setOnClickListener(mListener);
            }
        }
        manager.registerPlayers(opponentName);
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
                        setupGame(event.getEndpointName());
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

    @Subscribe
    public void resetBoard(GameResetEvent event){
        super.resetBoard();
    }
}