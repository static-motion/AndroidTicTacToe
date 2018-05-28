package com.example.tictactoe;

public class Winner {
    private char mWinningPiece;
    private int mStartPos;
    private WinningPosition mWinningPosition;

    Winner(char winningPiece, int startPos, WinningPosition winningPosition){
        mWinningPiece = winningPiece;
        mStartPos = startPos;
        mWinningPosition = winningPosition;
    }

    public WinningPosition getWinningPosition() {
        return mWinningPosition;
    }

    public char getWinningPiece(){
        return mWinningPiece;
    }

    public int getStartingPosition(){
        return mStartPos;
    }
}
