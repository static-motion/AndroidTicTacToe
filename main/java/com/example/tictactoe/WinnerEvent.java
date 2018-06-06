package com.example.tictactoe;

public class WinnerEvent {
    private Winner mWinner;

    WinnerEvent(Winner winner) {
        this.mWinner = winner;
    }

    public Winner getWinner(){
        return mWinner;
    }
}
