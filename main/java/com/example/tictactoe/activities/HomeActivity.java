package com.example.tictactoe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;

import com.example.tictactoe.utils.GameManager;
import com.example.tictactoe.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    Button mBtnStartGame;
    Button mBtnConnect;
    GameManager manager;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        manager = new GameManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        mBtnStartGame = findViewById(R.id.btn_start_game);
        mBtnStartGame.setOnClickListener(this);
        mBtnConnect = findViewById(R.id.btn_connect);
        mBtnConnect.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_game:
                intent = new Intent(this, GameActivity.class);
                break;
            case R.id.btn_connect:
                intent = new Intent(this, ConnectionsActivity.class);
                break;
        }
        startActivity(intent);
    }
}
