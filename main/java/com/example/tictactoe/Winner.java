package com.example.tictactoe;

public class Winner {
    private Player mPlayer;
    private int[] mWinningStreakCoordinates;

    Winner(Player player, int[] coordinates){
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
