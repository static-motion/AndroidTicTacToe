package com.example.tictactoe.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.tictactoe.R;
import com.example.tictactoe.utils.SharedPreferencesManager;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Button mBtnStartGame;
    Button mBtnConnect;
    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        mBtnStartGame = findViewById(R.id.btn_start_game);
        mBtnStartGame.setOnClickListener(this);
        mBtnConnect = findViewById(R.id.btn_multiplayer);
        mBtnConnect.setOnClickListener(this);
        Button btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_game:
                intent = new Intent(this, GameActivity.class);
                intent.putExtra("PLAYER_NAME",
                        SharedPreferencesManager
                                .getInstance()
                                .getPreference(SharedPreferencesManager.NICKNAME));
                break;
            case R.id.btn_multiplayer:
                intent = new Intent(this, ConnectionsActivity.class);
                break;
            case R.id.btn_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
        else {
            startActivity(intent);
        }
    }
}
