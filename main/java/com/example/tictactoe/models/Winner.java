package com.example.tictactoe.models;

import com.example.tictactoe.models.Player;

public class Winner {
    private Player mPlayer;
    private int[] mWinningStreakCoordinates;

    public Winner(Player player, int[] coordinates){
        mPlayer = player;
        mWinningStreakCoordinates = coordinates;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public int[] getWinningStreakCoordinates(){
        return mWinningStreakCoordinates;
    }
}
