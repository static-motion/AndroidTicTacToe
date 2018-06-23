package com.example.tictactoe.models;

public class Move extends GridCell{

    private int score;

    public Move(int row, int col, int score) {
        super(row, col);
        this.score = score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
