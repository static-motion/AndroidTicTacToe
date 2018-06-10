package com.example.tictactoe;

import java.io.Serializable;

public class Player implements Serializable{

    private String mName;
    private boolean mIsOpponent;
    private char mFigure;
    private int mFigureDrawable;
    private int mWinsCount;
    private int mHighlightedFigure;

    Player(String name, boolean isOpponent){
        mName = name;
        mIsOpponent = isOpponent;
    }

    public String getName(){
        return mName;
    }

    public void setFigure(int id, char figure, int highlightedFigure){
        mFigureDrawable = id;
        mFigure = figure;
        mHighlightedFigure = highlightedFigure;

    }

    public int getHighlightedFigure(){
        return mHighlightedFigure;
    }

    public int getFigureDrawable(){
        return mFigureDrawable;
    }

    public char getFigureChar(){
        return mFigure;
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
