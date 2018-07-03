package com.example.tictactoe.models;

public class Board implements com.example.tictactoe.interfaces.Board {
    protected char[][] mBoard;
    protected boolean[][] mTaken;
    protected char DEFAULT_CHAR = '\u0000';
    private static Board instance;

    private Board() {
        this.mBoard = new char[3][3];
        mTaken = new boolean[3][3];
    }

    public static Board getInstance(){
        if(instance == null){
            instance = new Board();
        }
        return instance;
    }

    public void setMove(int row, int col, char figure){
        mBoard[row][col] = figure;
        mTaken[row][col] = true;
    }

    public boolean isTaken(int row, int col){
        return mTaken[row][col];
    }

    public char figureAt(int row, int col){
        return mBoard[row][col];
    }

    public void resetPosition(int row, int col){
        mBoard[row][col] = DEFAULT_CHAR;
        mTaken[row][col] = false;
    }
}