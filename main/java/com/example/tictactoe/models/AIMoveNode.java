package com.example.tictactoe.models;

import java.util.Locale;

public class AIMoveNode extends GridCell {
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

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "row: %d col: %d score: %d", getRow(), getCol(), this.score);
    }

    @Override
    public boolean equals(Object obj) throws ClassCastException {
        if (obj.getClass() != this.getClass()){
            throw new ClassCastException("Objects in comparison should be of the same type.");
        }
        AIMoveNode other = (AIMoveNode) obj;
        return this.getRow() == other.getRow()
                && this.getCol() == other.getCol();
    }
}
