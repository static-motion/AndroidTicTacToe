package com.example.tictactoe.activities;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoe.R;
import com.example.tictactoe.utils.SharedPreferencesManager;

public class ConnectionsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnServer;
    private Button mBtnClient;
    private TextView mNickname;
    private String mNicknameString;
    private boolean mTipShown = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_connections);
        configureUI();
        mNicknameString = getNickname();
        displayNickname();
    }

    @Override
    protected void onStart() {
        super.onStart();
        askForLocationPermission();
    }

    private String getNickname() {
        return SharedPreferencesManager.getInstance()
                .getPreference(SharedPreferencesManager.NICKNAME);
    }

    private void displayNickname() {
        mNickname.setText(mNicknameString);
    }

    private void configureUI() {
        mBtnServer = findViewById(R.id.btn_server);
        mBtnClient = findViewById(R.id.btn_client);
        mBtnServer.setOnClickListener(this);
        mBtnClient.setOnClickListener(this);
        mNickname = findViewById(R.id.nickname);
        mNickname.setOnClickListener(this);
    }

    private void askForLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 500);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_server:
                launchGame(true);
                break;
            case R.id.btn_client:
                launchGame(false);
                break;
            case R.id.nickname:
                if(!mTipShown) {
                    mTipShown = true;
                    Toast.makeText(
                            this,
                            "Go back to the settings menu if you wish to change your nickname.",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }

    private void launchGame(boolean isHost) {
        Intent intent = new Intent(this, MultiplayerGameActivity.class);
        intent.putExtra("PLAYER_NAME", mNicknameString);
        if(isHost){
            intent.putExtra("IS_HOST", true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
        else {
            startActivity(intent);
        }
    }
}
