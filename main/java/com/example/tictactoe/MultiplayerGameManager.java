package com.example.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;

public class MultiplayerGameManager implements TicTacToeGameManager, Serializable{

    private boolean isHost;
    private String mOpponentEndpointId;
    private boolean isOpponentsTurn = true;
    private boolean[][] mTaken = new boolean[3][3];
    private char[][] mBoard = new char[3][3];
    private int[][] mIds = new int[3][3];
    private HashMap<Integer,GridCell> mCells;
    private final int mCircle = R.drawable.circle;
    private final int mCircleWinner = R.drawable.circle_win;
    private final int mCross = R.drawable.cross;
    private final int mCrossWinner = R.drawable.cross_win;
    private int mTurnsCount = 0;
    private GameState mGameState = GameState.InProgress;
    private char mDefault = mBoard[0][0];
    private Player mPlayer;
    private Player mOpponent;
    private final char mCrossChar = 'x';
    private final char mCircleChar = 'o';
    private int mRowId;
    private int mColId;
    private Context mContext;
    private ConnectionsClient mConnectionsClient;
    private final String TAG = getClass().getSimpleName();
    private final Strategy STRATEGY = Strategy.P2P_POINT_TO_POINT;
    private final String SERVICE_ID = getClass().getCanonicalName();
    private final PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            Log.d(TAG, new String(payload.asBytes(), Charset.forName("UTF-8")));
            processOpponentMove(new String(payload.asBytes(), Charset.forName("UTF-8")));
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };



    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull final String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Log.d(TAG, String.format("Endpoint discovered - %s, requesting connection.", discoveredEndpointInfo.getEndpointName()));
            mConnectionsClient.stopDiscovery();
            mConnectionsClient
                    .requestConnection(
                            "Gosho",
                            endpointId,
                            mConnectionLifecycleCallback
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Connection requested.");
                            Toast.makeText(mContext, String.format("Connection to %s requested.", endpointId), Toast.LENGTH_SHORT).show();
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

    private final ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull final String endpointId, @NonNull final ConnectionInfo connectionInfo) {
            mConnectionsClient.stopAdvertising();
            new AlertDialog.Builder(mContext)
                    .setTitle("Accept connection to " + connectionInfo.getEndpointName())
                    .setMessage("Confirm the code " + connectionInfo.getAuthenticationToken() + " is also displayed on the other device.")
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The user confirmed, so we can accept the connection.
                            mConnectionsClient.acceptConnection(endpointId, mPayloadCallback);
                            registerPlayers(connectionInfo.getEndpointName(), endpointId);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The user canceled, so we should reject the connection.
                            mConnectionsClient.rejectConnection(endpointId);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
            Log.d(TAG, connectionResolution.getStatus().getStatusMessage());
            Toast.makeText(mContext, String.format("%s %s", endpointId, connectionResolution.getStatus().getStatusMessage()), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            //TODO
        }
    };

    private void registerPlayers(String opponentName, String opponentEndpointId) {
        mOpponentEndpointId = opponentEndpointId;
        mOpponent = new Player(opponentName);
        mPlayer = new Player("Pesho");
        if(isHost){
            mPlayer.setFigure(mCross, mCrossChar);
            mOpponent.setFigure(mCircle, mCircleChar);
            isOpponentsTurn = false;
        }
        else {
            mPlayer.setFigure(mCircle, mCircleChar);
            mOpponent.setFigure(mCross, mCrossChar);
        }
    }

    MultiplayerGameManager(){
        mCells = new HashMap<>();
    }

    private void startAdvertising(){
        AdvertisingOptions.Builder builder = new AdvertisingOptions.Builder();
        builder.setStrategy(STRATEGY);
        AdvertisingOptions options =  builder.build();

        mConnectionsClient
                .startAdvertising("pesho", SERVICE_ID, mConnectionLifecycleCallback, options)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "Advertising...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Advertising...");
                    }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to start advertising");
                        e.printStackTrace();
                        Toast.makeText(mContext, "Unable to start advertising.", Toast.LENGTH_SHORT).show();
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
                            Log.d(TAG, "Discovering...");
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Unable to starting discovering.");
                            e.printStackTrace();
                    }});
    }

    public void stopAllEndpoints(){
        mConnectionsClient.stopAllEndpoints();
    }

    @Override
    public void registerCell(int id, GridCell cell){
        this.mCells.put(id, cell);
        registerId(id);
    }

    private void registerId(int id) {
        mIds[mRowId][mColId++] = id;
        if(mColId > 2){
            mRowId++;
            mColId = 0;
        }
    }
    @Override
    public Winner processMove(int id) {
        if(isOpponentsTurn){
            return null;
        }
        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();
        if(!mTaken[row][col]){
            isOpponentsTurn = true;
            updateCell(clickedCell.getCell(), row, col);
            mTurnsCount++;
            updateGameState();
            sendMoveCoordinates(row, col);
        }
        return checkForWinner();
    }

    private void processOpponentMove(String coordinates) {
        int row = Character.getNumericValue(coordinates.charAt(0));
        int col = Character.getNumericValue(coordinates.charAt(1));
        GridCell clickedCell = mCells.get(mIds[row][col]);
        updateCell(clickedCell.getCell(), row, col);
        mTurnsCount++;
        updateGameState();
        isOpponentsTurn = false;

    }

    private void sendMoveCoordinates(int row, int col){
        String coords = String.valueOf(row) + String.valueOf(col);
        mConnectionsClient.sendPayload(mOpponentEndpointId, Payload.fromBytes(coords.getBytes(Charset.forName("UTF-8"))));
    }

    private void updateGameState() {
        if(mTurnsCount == 9){
            mGameState = GameState.Finished;
        }
    }

    //Place vector background on clicked button, mark the position as taken
    //and put the corresponding char in the mBoard matrix.
    private void updateCell(Button button, int row, int col) {
        if (!isOpponentsTurn) {
            button.setBackgroundResource(mPlayer.getFigureDrawable());
            mBoard[row][col] = mPlayer.getFigureChar();
        } else {
            button.setBackgroundResource(mOpponent.getFigureDrawable());
            mBoard[row][col] = mOpponent.getFigureChar();
        }
        mTaken[row][col] = true;
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
            if (mBoard[i][0] == mBoard[i][1] && mBoard[i][1] == mBoard[i][2] && mBoard[i][0] != mDefault) {
                mGameState = GameState.Finished;
                return new Winner(mBoard[i][0], i, WinningPosition.Row);
            }
            //Columns
            if (mBoard[0][i] == mBoard[1][i] && mBoard[1][i] == mBoard[2][i] && mBoard[0][i] != mDefault) {
                mGameState = GameState.Finished;
                return new Winner(mBoard[0][i], i, WinningPosition.Column);
            }
        }
        //Checking the two diagonals for 3 identical chars. Since the rows and columns are already checked
        //the diagonals are the only two options left for a win state.
        if(mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2] && mBoard[0][0] != mDefault){
            mGameState = GameState.Finished;
            return new Winner(mBoard[0][0], 0, WinningPosition.Diagonal);
        }
        if(mBoard[2][0] == mBoard[1][1] && mBoard[1][1] == mBoard[0][2] && mBoard[0][2] != mDefault){
            mGameState = GameState.Finished;
            return new Winner(mBoard[2][0], 2, WinningPosition.Diagonal);
        }
        return null;
    }
    @Override
    public void highlightWinningSequence(Winner winner) {

        char winningPiece = winner.getWinningPiece();
        int resource;

        if(winningPiece == mCrossChar){
            resource = mCrossWinner;
        }
        else {
            resource = mCircleWinner;
        }

        if(winner.getWinningPosition() == WinningPosition.Column){
            for (int i = 0; i < mIds[0].length; i++) {
                mCells.get(mIds[i][winner.getStartingPosition()]).getCell().setBackgroundResource(resource);
            }
        }else if(winner.getWinningPosition() == WinningPosition.Row){
            for (int i = 0; i < mIds[0].length; i++) {
                mCells.get(mIds[winner.getStartingPosition()][i]).getCell().setBackgroundResource(resource);
            }
        }
        else {
            if(winner.getStartingPosition() == 0){
                mCells.get(mIds[0][0]).getCell().setBackgroundResource(resource);
                mCells.get(mIds[1][1]).getCell().setBackgroundResource(resource);
                mCells.get(mIds[2][2]).getCell().setBackgroundResource(resource);
            }
            else {
                mCells.get(mIds[0][2]).getCell().setBackgroundResource(resource);
                mCells.get(mIds[1][1]).getCell().setBackgroundResource(resource);
                mCells.get(mIds[2][0]).getCell().setBackgroundResource(resource);
            }
        }
    }
    @Override
    //Completely resets the board state, moves count, winner and status text
    public void resetGame(){
        for (int i = 0; i < mTaken[0].length; i++) {
            for (int j = 0; j < mTaken[1].length; j++) {
                mCells.get(mIds[i][j]).getCell().setBackgroundResource(R.color.colorBlack);
                mBoard[i][j] = mDefault;
                mTaken[i][j] = false;
            }
        }
        mTurnsCount = 0;
        mGameState = GameState.InProgress;
    }

    public void initialize(Activity gameActivity, boolean isHost) {
        this.mContext = gameActivity;
        mConnectionsClient = Nearby.getConnectionsClient(mContext);
        this.isHost = isHost;
        if(isHost){
            startAdvertising();
        }
        else {
            startDiscovery();
        }
    }

    @Override
    public GameState getGameState(){
        return this.mGameState;
    }
}