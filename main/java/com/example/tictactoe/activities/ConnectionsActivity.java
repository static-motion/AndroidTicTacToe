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

import com.example.tictactoe.R;
import com.example.tictactoe.interfaces.ChangeNicknameDialogListener;
import com.example.tictactoe.views.ChangeNicknameDialog;

import java.util.Random;

public class ConnectionsActivity extends AppCompatActivity implements View.OnClickListener, ChangeNicknameDialogListener{

    public static final String TIC_TAC_TOE_PREFS = "TIC_TAC_TOE_PREFS";
    private Button mBtnServer;
    private Button mBtnClient;
    private Button mBtnChangeNickname;
    TextView mUsername;
    private String mNicknameString;

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
        setNickname();
    }

    @Override
    protected void onStart() {
        super.onStart();
        askForLocationPermission();
    }

    private void displayNickname() {
        mUsername.setText(mNicknameString);
    }

    private void configureUI() {
        mBtnServer = findViewById(R.id.btn_server);
        mBtnClient = findViewById(R.id.btn_client);
        mBtnChangeNickname = findViewById(R.id.btn_change_nickname);
        mBtnServer.setOnClickListener(ConnectionsActivity.this);
        mBtnClient.setOnClickListener(ConnectionsActivity.this);
        mBtnChangeNickname.setOnClickListener(ConnectionsActivity.this);
        mUsername = findViewById(R.id.nickname);
    }

    private void askForLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 500);
        }
    }

    private void setNickname() {
       mNicknameString = getSharedPreferences(TIC_TAC_TOE_PREFS, MODE_PRIVATE).getString("nickname", null);
        if(mNicknameString == null){
            mNicknameString = "Player#" + getRandomNumber();
            saveNickname(mNicknameString);
        }
        displayNickname();
    }

    private void saveNickname(String nickname) {
        getSharedPreferences(TIC_TAC_TOE_PREFS, MODE_PRIVATE)
                .edit()
                .putString("nickname", nickname)
                .apply();
    }

    @Override
    public void publishNickname(String nickname) {
        saveNickname(nickname);
        mNicknameString = nickname;
        displayNickname();
    }

    private String getRandomNumber() {
        return String.format("%04d", new Random().nextInt(9999));
    }

    private void openChangeNicknameDialog() {
        ChangeNicknameDialog dialog = new ChangeNicknameDialog();
        dialog.show(getSupportFragmentManager(), "change_nickname_dialog");
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
            case R.id.btn_change_nickname:
                openChangeNicknameDialog();
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
