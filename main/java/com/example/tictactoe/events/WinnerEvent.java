package com.example.tictactoe.events;

import com.example.tictactoe.models.Winner;

public class WinnerEvent {
    private Winner mWinner;

    public WinnerEvent(Winner winner) {
        this.mWinner = winner;
    }

    public Winner getWinner(){
        return mWinner;
    }
}
