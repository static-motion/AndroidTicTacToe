package com.example.tictactoe;

public class Player {
    private String mName;
    private char mFigure;
    private int mFigureDrawable;

    Player(String name){
        mName = name;
    }

    public String getName(){
        return mName;
    }

    public void setFigure(int id, char figure){
        mFigureDrawable = id;
        mFigure = figure;
    }

    public int getFigureDrawable(){
        return mFigureDrawable;
    }

    public char getFigureChar(){
        return mFigure;
    }
}
