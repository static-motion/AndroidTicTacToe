package com.example.tictactoe.models;

public class AIMoveNode extends GridCell{


    private int score;

    public AIMoveNode(int row, int col, int score) {
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
