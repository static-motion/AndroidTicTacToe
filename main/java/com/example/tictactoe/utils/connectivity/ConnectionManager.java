package com.example.tictactoe.utils.connectivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.tictactoe.events.DeviceConnectedEvent;
import com.example.tictactoe.events.DeviceFoundEvent;
import com.example.tictactoe.events.GameLobbyCreatedEvent;
import com.example.tictactoe.events.GameResetEvent;
import com.example.tictactoe.events.OpponentMoveEvent;
import com.example.tictactoe.events.PlayerDisconnectedEvent;
import com.example.tictactoe.events.SearchingForDevicesEvent;
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

public class ConnectionManager {

    private final Strategy STRATEGY = Strategy.P2P_POINT_TO_POINT;
    private final String TAG = getClass().getSimpleName();
    private final String PLAYER_NAME;
    private final String SERVICE_ID = "35f858522ad661027da8c3ab9bcb64d8";
    private final String RESTART_REQUEST = "RESTART";
    private boolean mRestartRequestSent = false;
    private boolean mRestartRequestReceived = false;
    private ConnectionsClient mConnectionsClient;
    private String mOpponentEndpointId;
    private String mEndpointName;
    private PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            byte[] data = payload.asBytes();

            if(data == null) return;

            if(data.length > 2){
                mRestartRequestReceived = true;
                announceRestart();
            }
            else {
                EventBus.getDefault().post(new OpponentMoveEvent(data));
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        }
    };

    private ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            mOpponentEndpointId = endpointId;
            EventBus.getDefault().post(new DeviceFoundEvent(
                    endpointId,
                    connectionInfo.getAuthenticationToken(),
                    connectionInfo.getEndpointName()));
        }
        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
            Log.d(TAG, connectionResolution.getStatus().getStatusMessage());
            EventBus.getDefault().post(new DeviceConnectedEvent(mEndpointName));
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            EventBus.getDefault().post(new PlayerDisconnectedEvent(mEndpointName));
        }
    };

    private EndpointDiscoveryCallback mEndpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Log.d(TAG, String.format("Endpoint discovered - %s, requesting connection.",
                    discoveredEndpointInfo.getEndpointName()));

            mConnectionsClient.stopDiscovery();
            mConnectionsClient
                    .requestConnection(
                            PLAYER_NAME,
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

    public void startAdvertising(){
        AdvertisingOptions options = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        mConnectionsClient
                .startAdvertising(PLAYER_NAME, SERVICE_ID, mConnectionLifecycleCallback, options)
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

    public ConnectionManager(String playerName, Context context) {
        PLAYER_NAME = playerName;
        mConnectionsClient = Nearby.getConnectionsClient(context);
    }

    public void sendRestartRequest(){
        mConnectionsClient.sendPayload(mOpponentEndpointId, Payload.fromBytes(RESTART_REQUEST.getBytes(Charset.forName("UTF-8"))));
        mRestartRequestSent = true;
        announceRestart();
    }

    public void startDiscovery(){
        DiscoveryOptions options = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        mConnectionsClient
                .startDiscovery(
                        SERVICE_ID,
                        mEndpointDiscoveryCallback,
                        options)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Discovering...");
                        EventBus.getDefault().post(new SearchingForDevicesEvent());
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to starting discovering.");
                        e.printStackTrace();
                    }});
    }

    public void sendMoveCoordinates(int row, int col){
        mConnectionsClient.sendPayload(mOpponentEndpointId, Payload.fromBytes(new byte[]{(byte)row, (byte)col}));
    }

    private void announceRestart(){
        if(mRestartRequestSent && mRestartRequestReceived){
            mRestartRequestSent = mRestartRequestReceived = false;
            EventBus.getDefault().post(new GameResetEvent());
        }
    }

    public void connect(String endpointId, String endpointName){
        mConnectionsClient.acceptConnection(endpointId, mPayloadCallback);
        mEndpointName = endpointName;
    }

    public void rejectConnection(String endpointId){
        mConnectionsClient.rejectConnection(endpointId);
    }

    public void shutdown() {
        mConnectionsClient.stopAllEndpoints();
        mConnectionsClient.stopDiscovery();
        mConnectionsClient.stopAdvertising();
    }

    public void stopDiscovery() {
        mConnectionsClient.stopDiscovery();
    }

    public void stopAdvertising() {
        mConnectionsClient.stopAdvertising();
    }
}
