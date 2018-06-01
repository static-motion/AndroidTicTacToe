package com.example.tictactoe;

import android.app.Activity;
import android.widget.Button;

import java.io.Serializable;
import java.util.HashMap;

public class GameManager implements TicTacToeGameManager, Serializable {

    private boolean[][] mTaken = new boolean[3][3];
    private char[][] mBoard = new char[3][3];
    private int[][] mIds = new int[3][3];
    private HashMap<Integer,GridCell> mCells;
    private final int mCircle = R.drawable.circle;
    private final int mCircleWinner = R.drawable.circle_win;
    private final int mCross = R.drawable.cross;
    private final int mCrossWinner = R.drawable.cross_win;
    private int mTurnsCount = 0;
    private GameState mGameState = GameState.InProgress;
    private char mDefault = mBoard[0][0];
    private final char mCrossChar = 'x';
    private final char mCircleChar = 'o';
    private int mRowId;
    private int mColId;

    GameManager(){
        mCells = new HashMap<>();
    }

    public void registerCell(int id, GridCell cell){
        this.mCells.put(id, cell);
        registerId(id);
    }

    private void registerId(int id) {
        mIds[mRowId][mColId++] = id;
        if(mColId > 2){
            mRowId++;
            mColId = 0;
        }
    }

    public Winner processMove(int id) {
        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();
        if(!mTaken[row][col]){
            updateCell(clickedCell.getCell(), row, col);
            mTurnsCount++;
            updateGameState();
        }
        return checkForWinner();
    }

    private void updateGameState() {
        if(mTurnsCount == 9){
            mGameState = GameState.Finished;
        }
    }

    //Place vector background on clicked button, mark the position as taken
    //and put the corresponding char in the mBoard matrix.
    private void updateCell(Button button, int row, int col) {
        if (mTurnsCount % 2 == 0) {
            button.setBackgroundResource(mCross);
            mBoard[row][col] = mCrossChar;
        } else {
            button.setBackgroundResource(mCircle);
            mBoard[row][col] = mCircleChar;
        }
        mTaken[row][col] = true;
    }
    public Winner checkForWinner() {
        //5 turns is the minimum turns count required for the game to be won.
        //No need to check the board before that.
        if(mTurnsCount < 5) {
            return null;
        }
        //Checking rows and columns for 3 identical chars.
        for (int i = 0; i < mBoard[0].length; i++) {
            //Rows
            if (mBoard[i][0] == mBoard[i][1] && mBoard[i][1] == mBoard[i][2] && mBoard[i][0] != mDefault) {
                mGameState = GameState.Finished;
                return new Winner(mBoard[i][0], i, WinningPosition.Row);
            }
            //Columns
            if (mBoard[0][i] == mBoard[1][i] && mBoard[1][i] == mBoard[2][i] && mBoard[0][i] != mDefault) {
                mGameState = GameState.Finished;
                return new Winner(mBoard[0][i], i, WinningPosition.Column);
            }
        }
        //Checking the two diagonals for 3 identical chars. Since the rows and columns are already checked
        //the diagonals are the only two options left for a win state.
        if(mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2] && mBoard[0][0] != mDefault){
            mGameState = GameState.Finished;
            return new Winner(mBoard[0][0], 0, WinningPosition.Diagonal);
        }
        if(mBoard[2][0] == mBoard[1][1] && mBoard[1][1] == mBoard[0][2] && mBoard[0][2] != mDefault){
            mGameState = GameState.Finished;
            return new Winner(mBoard[2][0], 2, WinningPosition.Diagonal);
        }
        return null;
    }

    public void highlightWinningSequence(Winner winner) {

        char winningPiece = winner.getWinningPiece();
        int resource;

        if(winningPiece == mCrossChar){
            resource = mCrossWinner;
        }
        else {
            resource = mCircleWinner;
        }

        if(winner.getWinningPosition() == WinningPosition.Column){
            for (int i = 0; i < mIds[0].length; i++) {
                mCells.get(mIds[i][winner.getStartingPosition()]).getCell().setBackgroundResource(resource);
            }
        }else if(winner.getWinningPosition() == WinningPosition.Row){
            for (int i = 0; i < mIds[0].length; i++) {
                mCells.get(mIds[winner.getStartingPosition()][i]).getCell().setBackgroundResource(resource);
            }
        }
        else {
            if(winner.getStartingPosition() == 0){
                mCells.get(mIds[0][0]).getCell().setBackgroundResource(resource);
                mCells.get(mIds[1][1]).getCell().setBackgroundResource(resource);
                mCells.get(mIds[2][2]).getCell().setBackgroundResource(resource);
            }
            else {
                mCells.get(mIds[0][2]).getCell().setBackgroundResource(resource);
                mCells.get(mIds[1][1]).getCell().setBackgroundResource(resource);
                mCells.get(mIds[2][0]).getCell().setBackgroundResource(resource);
            }
        }
    }

    //Completely resets the board state, moves count, winner and status text
    public void resetGame(){
        for (int i = 0; i < mTaken[0].length; i++) {
            for (int j = 0; j < mTaken[1].length; j++) {
                mCells.get(mIds[i][j]).getCell().setBackgroundResource(R.color.colorBlack);
                mBoard[i][j] = mDefault;
                mTaken[i][j] = false;
            }
        }
        mTurnsCount = 0;
        mGameState = GameState.InProgress;
    }
    public GameState getGameState(){
        return this.mGameState;
    }
}
