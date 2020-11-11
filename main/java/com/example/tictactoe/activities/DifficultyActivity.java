package com.example.tictactoe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.tictactoe.R;
import com.example.tictactoe.utils.SharedPreferencesManager;
import com.example.tictactoe.utils.ai.DifficultySettings;

public class DifficultyActivity extends AppCompatActivity implements View.OnClickListener{

    Button mBtnEasy;
    Button mBtnNormal;
    Button mBtnHard;
    Button mBtnImpossible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_difficulty);
        mBtnEasy = findViewById(R.id.btn_diff_easy);
        mBtnNormal = findViewById(R.id.btn_diff_normal);
        mBtnHard = findViewById(R.id.btn_diff_hard);
        mBtnImpossible = findViewById(R.id.btn_diff_impossible);
        mBtnEasy.setOnClickListener(this);
        mBtnNormal.setOnClickListener(this);
        mBtnHard.setOnClickListener(this);
        mBtnImpossible.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        DifficultySettings settings;
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("PLAYER_NAME",
                SharedPreferencesManager
                        .getInstance()
                        .getPreference(SharedPreferencesManager.NICKNAME));

        switch (v.getId()){
            case R.id.btn_diff_easy:
                settings = DifficultySettings.EASY;
                break;
            case R.id.btn_diff_hard:
                settings = DifficultySettings.HARD;
                break;
            case R.id.btn_diff_impossible:
                settings = DifficultySettings.GODLIKE;
                break;
            case R.id.btn_diff_normal:
            default:
                settings = DifficultySettings.NORMAL;
                break;
        }

        intent.putExtra("DIFFICULTY", settings);
        startActivity(intent);
    }
}
