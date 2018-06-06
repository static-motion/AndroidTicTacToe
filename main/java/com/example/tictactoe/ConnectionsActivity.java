package com.example.tictactoe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ConnectionsActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnServer;
    private Button mBtnClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connections_layout);
        askForLocationPermission();
        registerUI();
    }

    private void registerUI() {
        mBtnServer = findViewById(R.id.btn_server);
        mBtnClient = findViewById(R.id.btn_client);
        mBtnServer.setOnClickListener(this);
        mBtnClient.setOnClickListener(this);
    }

    private void askForLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 500);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MultiplayerGameActivity.class);
        switch (v.getId()){
            case R.id.btn_server:
                intent.putExtra("IS_HOST", true);
                break;
            case R.id.btn_client:
                intent.putExtra("IS_HOST", false);
                break;
        }
        startActivity(intent);
    }
}
