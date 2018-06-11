package com.example.tictactoe.models;

import java.io.Serializable;

public class Player implements Serializable{

    private String mName;
    private boolean mIsOpponent;
    private Figure mPlayerFigure;
    private int mWinsCount;

    public Player(String name, boolean isOpponent){
        mName = name;
        mIsOpponent = isOpponent;
    }

    public String getName(){
        return mName;
    }

    public void setFigure(Figure figure){
        mPlayerFigure = figure;
    }

    public Figure getPlayerFigure() {
        return mPlayerFigure;
    }

    public int getWinsCount(){
        return mWinsCount;
    }

    public void increaseWinsCount(){
        mWinsCount++;
    }

    public boolean isOpponent(){
        return mIsOpponent;
    }
}
