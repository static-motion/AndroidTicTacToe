package com.example.tictactoe;

import android.app.Application;

import com.example.tictactoe.utils.SharedPreferencesManager;

import java.util.Random;

public class GameApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        setNickname();
    }

    private void setNickname() {
        SharedPreferencesManager manager = SharedPreferencesManager.createInstance(this);
        String nickname = manager.getPreference(SharedPreferencesManager.NICKNAME);
        if(nickname == null){
            nickname = "Player#" + getRandomNumber();
            manager.savePreference(SharedPreferencesManager.NICKNAME, nickname);
        }
    }

    private String getRandomNumber() {
        return String.format("%04d", new Random().nextInt(9999));
    }
}
