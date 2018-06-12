package com.example.tictactoe.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.tictactoe.R;
import com.example.tictactoe.activities.GameLobbyCreatedEvent;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.DeviceFoundEvent;
import com.example.tictactoe.events.OpponentMoveEvent;
import com.example.tictactoe.events.PlayerDisconnectedEvent;
import com.example.tictactoe.events.SearchingForDevicesEvent;
import com.example.tictactoe.models.Figure;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Player;
import com.example.tictactoe.models.Winner;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.greenrobot.eventbus.EventBus;

import java.nio.charset.Charset;
import java.util.HashMap;

public class MultiplayerGameManager implements TicTacToeGameManager {

    private String mPlayerName;
    private boolean mRestartRequestSent = false;
    private boolean mRestartRequestReceived = false;
    private final String RESTART_REQUEST = "RESTART_REQUEST";
    private boolean mIsHost;
    private String mOpponentEndpointId;
    private boolean mIsOpponentsTurn = true;
    private boolean[][] mTaken = new boolean[3][3];
    private char[][] mBoard = new char[3][3];
    private int[][] mIds = new int[3][3];
    private HashMap<Integer,GridCell> mCells;
    private final int CIRCLE_DRAWABLE = R.drawable.circle;
    private final int CIRCLE_HIGHLIGHTED = R.drawable.circle_win;
    private final int CROSS_DRAWABLE = R.drawable.cross;
    private final int CROSS_HIGHLIGHTED = R.drawable.cross_win;
    private final char CROSS_CHAR = 'x';
    private final char CIRCLE_CHAR = 'o';
    private final Figure CROSS_FIGURE = new Figure(CROSS_CHAR, CROSS_DRAWABLE, CROSS_HIGHLIGHTED);
    private final Figure CIRCLE_FIGURE = new Figure(CIRCLE_CHAR, CIRCLE_DRAWABLE, CIRCLE_HIGHLIGHTED);
    private int mTurnsCount = 0;
    private GameState mGameState = GameState.InProgress;
    private final char DEFAULT_CHAR = mBoard[0][0];
    private Player mPlayer;
    private Player mOpponent;
    private int mRowId;
    private int mColId;
    private final String TAG = getClass().getSimpleName();
    private final Strategy STRATEGY = Strategy.P2P_POINT_TO_POINT;
    private final String SERVICE_ID = getClass().getCanonicalName();
    private ConnectionsClient mConnectionsClient;
    private PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            Log.d(TAG, new String(payload.asBytes(), Charset.forName("UTF-8")));
            String data = new String(payload.asBytes(), Charset.forName("UTF-8"));
            if(!data.equals("RESTART_REQUEST")){
                int cellID = parseOpponentMove(data);
                EventBus.getDefault().post(new OpponentMoveEvent(cellID));
            }
            else {
                mRestartRequestReceived = true;
                resetGame();
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        }
    };
    private EndpointDiscoveryCallback mEndpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Log.d(TAG, String.format("Endpoint discovered - %s, requesting connection.", discoveredEndpointInfo.getEndpointName()));
            mConnectionsClient.stopDiscovery();
            mConnectionsClient
                    .requestConnection(
                            mPlayerName,
                            endpointId,
                            mConnectionLifecycleCallback
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Connection requested.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
            });
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            Log.d(TAG, "Endpoint lost");
        }
    };

    private ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            EventBus.getDefault().post(new SearchingForDevicesEvent(false));
            EventBus.getDefault().post(new DeviceFoundEvent(endpointId, connectionInfo.getAuthenticationToken(), connectionInfo.getEndpointName()));
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
            Log.d(TAG, connectionResolution.getStatus().getStatusMessage());
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            EventBus.getDefault().post(new PlayerDisconnectedEvent());
        }
    };

    public MultiplayerGameManager(){
        mCells = new HashMap<>();
    }

    public void connect(String endpointId, String endpointName){
        mConnectionsClient.acceptConnection(endpointId, mPayloadCallback);
        registerPlayers(endpointName, endpointId);
    }

    public void rejectConnection(String endpointId){
        mConnectionsClient.rejectConnection(endpointId);
    }

    private void registerPlayers(String opponentName, String opponentEndpointId) {
        mOpponentEndpointId = opponentEndpointId;
        mOpponent = new Player(opponentName, true);
        mPlayer = new Player(mPlayerName, false);
        if(mIsHost){
            mPlayer.setFigure(CROSS_FIGURE);
            mOpponent.setFigure(CIRCLE_FIGURE);
            mIsOpponentsTurn = false;
        }
        else {
            mPlayer.setFigure(CIRCLE_FIGURE);
            mOpponent.setFigure(CROSS_FIGURE);
        }
    }

    private void startAdvertising(){
        AdvertisingOptions.Builder builder = new AdvertisingOptions.Builder();
        builder.setStrategy(STRATEGY);
        AdvertisingOptions options =  builder.build();
        mConnectionsClient
                .startAdvertising(mPlayerName, SERVICE_ID, mConnectionLifecycleCallback, options)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        EventBus.getDefault().post(new GameLobbyCreatedEvent("Success! Awaiting connections..."));
                    }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to start advertising");
                        e.printStackTrace();
                    }
        });
    }

    private void startDiscovery(){
        DiscoveryOptions.Builder builder = new DiscoveryOptions.Builder();
        builder.setStrategy(STRATEGY);
        DiscoveryOptions options =  builder.build();

        mConnectionsClient
                .startDiscovery(
                    SERVICE_ID,
                    mEndpointDiscoveryCallback,
                    options)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                            EventBus.getDefault().post(new SearchingForDevicesEvent(true));
                            Log.d(TAG, "Discovering...");
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Unable to starting discovering.");
                            e.printStackTrace();
                    }});
    }

    @Override
    public void registerCell(int id, GridCell cell){
        mCells.put(id, cell);
        mIds[mRowId][mColId++] = id;
        if(mColId > 2){
            mRowId++;
            mColId = 0;
        }
    }

    public GridCell processOpponentMove(int id){
        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();
        mIsOpponentsTurn = false;
        updateCell(row, col);
        mTurnsCount++;
        updateGameState();
        return clickedCell;
    }

    @Override
    public GridCell processMove(int id) {

        if(mIsOpponentsTurn) return null;

        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();

        if(mTaken[row][col]) return null;

        mIsOpponentsTurn = true;
        updateCell(row, col);
        mTurnsCount++;
        updateGameState();
        sendMoveCoordinates(row, col);
        return clickedCell;
    }

    private int parseOpponentMove(String coordinates) {
        int row = Character.getNumericValue(coordinates.charAt(0));
        int col = Character.getNumericValue(coordinates.charAt(1));
        return mCells.get(mIds[row][col]).getCell().getId();
    }

    private void sendMoveCoordinates(int row, int col){
        String coords = String.valueOf(row) + String.valueOf(col);
        mConnectionsClient.sendPayload(mOpponentEndpointId, Payload.fromBytes(coords.getBytes(Charset.forName("UTF-8"))));
    }

    public void sendRestartRequest(){
        mConnectionsClient.sendPayload(mOpponentEndpointId, Payload.fromBytes(RESTART_REQUEST.getBytes(Charset.forName("UTF-8"))));
        mRestartRequestSent = true;
        resetGame();
    }

    private void updateGameState() {
        if(mTurnsCount == 9){
            mGameState = GameState.Finished;
        }
    }

    //Mark the position as taken
    //and put the corresponding char in the mBoard matrix.
    private void updateCell(int row, int col) {
        if (mIsOpponentsTurn) {
            mBoard[row][col] = mPlayer.getPlayerFigure().getCharFigure();
        } else {
            mBoard[row][col] = mOpponent.getPlayerFigure().getCharFigure();
        }
        mTaken[row][col] = true;
    }

    public void drawFigure(GridCell clickedCell){
        if (mIsOpponentsTurn) {
            clickedCell.getCell().setBackgroundResource(mPlayer.getPlayerFigure().getFigureDrawable());
        }
        else {
            clickedCell.getCell().setBackgroundResource(mOpponent.getPlayerFigure().getFigureDrawable());
        }
    }

    public boolean isOpponentsTurn(){
        return mIsOpponentsTurn;
    }

    @Override
    public Winner checkForWinner() {
        //5 turns is the minimum turns count required for the game to be won.
        //No need to check the board before that.
        if(mTurnsCount < 5) {
            return null;
        }
        //Checking rows and columns for 3 identical chars.
        for (int i = 0; i < mBoard[0].length; i++) {
            //Rows
            if (mBoard[i][0] == mBoard[i][1] && mBoard[i][1] == mBoard[i][2] && mBoard[i][0] != DEFAULT_CHAR) {
                mGameState = GameState.Finished;
                if(mBoard[i][0] == mPlayer.getPlayerFigure().getCharFigure()){
                    return new Winner(mPlayer, new int[]{
                            i,0,i,1,i,2
                    });
                }
                else {
                    return new Winner(mOpponent, new int[]{
                            i,0,i,1,i,2
                    });
                }
            }
            //Columns
            if (mBoard[0][i] == mBoard[1][i] && mBoard[1][i] == mBoard[2][i] && mBoard[0][i] != DEFAULT_CHAR) {
                mGameState = GameState.Finished;
                if(mBoard[0][i] == mPlayer.getPlayerFigure().getCharFigure()){
                    return new Winner(mPlayer, new int[]{
                            0,i,1,i,2,i
                    });
                }
                else {
                    return new Winner(mOpponent, new int[]{
                            0,i,1,i,2,i
                    });
                }
            }
        }

        //Checking the two diagonals for 3 identical chars. Since the rows and columns are already checked
        //the diagonals are the only two options left for a win state.
        if(mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2] && mBoard[0][0] != DEFAULT_CHAR){
            mGameState = GameState.Finished;
            if(mBoard[0][0] == mPlayer.getPlayerFigure().getCharFigure()){
                return new Winner(mPlayer, new int[]{
                    0,0,1,1,2,2
                });
            }
            else {
                return new Winner(mOpponent, new int[]{
                        0,0,1,1,2,2
                });
            }
        }

        if(mBoard[2][0] == mBoard[1][1] && mBoard[1][1] == mBoard[0][2] && mBoard[0][2] != DEFAULT_CHAR){
            mGameState = GameState.Finished;
            if(mBoard[2][0] == mPlayer.getPlayerFigure().getCharFigure()){
                return new Winner(mPlayer, new int[]{
                        2,0,1,1,0,2
                });
            }
            else {
                return new Winner(mOpponent, new int[]{
                        2,0,1,1,0,2
                });
            }
        }
        return null;
    }

    @Override
    public void highlightWinningSequence(Winner winner) {
        Player player = winner.getPlayer();
        int[] coordinates = winner.getWinningStreakCoordinates();
        for (int i = 0; i < coordinates.length - 1; i += 2) {
            mCells.get(mIds[coordinates[i]][coordinates[i + 1]]).getCell().setBackgroundResource(player.getPlayerFigure().getHighlightedFigure());
        }
    }

    @Override
    //Completely resets the board state, moves count, winner and status text
    public void resetGame(){
        if(!(mRestartRequestSent && mRestartRequestReceived)){
            return;
        }

        mRestartRequestSent = false;
        mRestartRequestReceived = false;

        for (int i = 0; i < mTaken[0].length; i++) {
            for (int j = 0; j < mTaken[1].length; j++) {
                mCells.get(mIds[i][j]).getCell().setBackgroundResource(R.color.colorBlack);
                mBoard[i][j] = DEFAULT_CHAR;
                mTaken[i][j] = false;
            }
        }
        mTurnsCount = 0;
        mGameState = GameState.InProgress;
    }

    public void initialize(Context context, boolean isHost, String playerName) {
        mConnectionsClient = Nearby.getConnectionsClient(context.getApplicationContext());
        mIsHost = isHost;
        mPlayerName = playerName;
        if(isHost){
            startAdvertising();
        }
        else {
            startDiscovery();
        }
    }

    @Override
    public GameState getGameState(){
        return mGameState;
    }

    public void stopDiscovery() {
        mConnectionsClient.stopDiscovery();
    }

    public void shutdown(){
        mConnectionsClient.stopDiscovery();
        mConnectionsClient.stopAdvertising();
        mConnectionsClient.stopAllEndpoints();
        mCells.clear();
    }
}