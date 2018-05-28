package com.example.tictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean[][] mTaken = new boolean[3][3];
    private char[][] mBoard = new char[3][3];
    private int[][] mIds = new int[3][3];
    private boolean mMoveProcessed = true;
    private HashMap<Integer,GridCell> mCells;
    private final int mCircle = R.drawable.circle;
    private final int mCircleWinner = R.drawable.circle_win;
    private final int mCross = R.drawable.cross;
    private final int mCrossWinner = R.drawable.cross_win;
    private int mTurnsCount = 0;
    private boolean mGameIsFinished = false;
    private char mDefault = mBoard[0][0];
    private Winner mWinner = null;
    private final char mCrossChar = 'x';
    private final char mCircleChar = 'o';
    private int mCrossesWinsCount = 0;
    private int mNaughtsWinsCount = 0;
    private TextView mNaughtsScore;
    private TextView mCrossesScore;
    private TextView mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCells = new HashMap<>();
        mCells.put(R.id.btn_1, new GridCell((Button)findViewById(R.id.btn_1), 0, 0, this));
        mCells.put(R.id.btn_2, new GridCell((Button)findViewById(R.id.btn_2), 0, 1, this));
        mCells.put(R.id.btn_3, new GridCell((Button)findViewById(R.id.btn_3), 0, 2, this));
        mCells.put(R.id.btn_4, new GridCell((Button)findViewById(R.id.btn_4), 1, 0, this));
        mCells.put(R.id.btn_5, new GridCell((Button)findViewById(R.id.btn_5), 1, 1, this));
        mCells.put(R.id.btn_6, new GridCell((Button)findViewById(R.id.btn_6), 1, 2, this));
        mCells.put(R.id.btn_7, new GridCell((Button)findViewById(R.id.btn_7), 2, 0, this));
        mCells.put(R.id.btn_8, new GridCell((Button)findViewById(R.id.btn_8), 2, 1, this));
        mCells.put(R.id.btn_9, new GridCell((Button)findViewById(R.id.btn_9), 2, 2, this));

        mIds[0][0] = R.id.btn_1;
        mIds[0][1] = R.id.btn_2;
        mIds[0][2] = R.id.btn_3;
        mIds[1][0] = R.id.btn_4;
        mIds[1][1] = R.id.btn_5;
        mIds[1][2] = R.id.btn_6;
        mIds[2][0] = R.id.btn_7;
        mIds[2][1] = R.id.btn_8;
        mIds[2][2] = R.id.btn_9;

        mCrossesScore = findViewById(R.id.crossesScore);
        mNaughtsScore = findViewById(R.id.naughtsScore);
        mStatus = findViewById(R.id.status);
    }

    @Override
    public void onClick(View v) {
        if(!mMoveProcessed)
            return;

        mMoveProcessed = false;

        boolean moveIsValid = processMove(v.getId());
        if(mGameIsFinished){
            resetGame();
            mMoveProcessed = true;
            return;
        }
        if(moveIsValid){
            mTurnsCount++;
        }
        //5 turns is the minimum turns count required for the game to be won. No need to check the board before that.
        if(mTurnsCount >= 5){
            mWinner = checkForWinner();
        }
        //If a winner is found or 9 moves have passed (the maximum possible moves in tic tac toe)
        //mark the game state as finished and update the UI.
        if(mWinner != null || mTurnsCount == 9){
            mGameIsFinished = true;
            updateScore();
        }
        mMoveProcessed = true;
    }

    //Updates the score depending on which figure won and update the status text.
    //If the game ended in a tie it just does the latter.
    private void updateScore() {
        if(mWinner != null){
            if(mWinner.getWinningPiece() == mCrossChar){
                mCrossesScore.setText(String.valueOf(++mCrossesWinsCount));
                mStatus.setText(R.string.crosses_win_message);
                highlightWinningSequence();
            }
            else if(mWinner.getWinningPiece() == mCircleChar){
                mNaughtsScore.setText(String.valueOf(++mNaughtsWinsCount));
                mStatus.setText(R.string.naughts_win_message);
                highlightWinningSequence();
            }
        }
        else {
            mStatus.setText(R.string.tie_endgame_message);
        }
    }

    private void highlightWinningSequence() {

        char winningPiece = mWinner.getWinningPiece();
        int resource;
        if(winningPiece == mCrossChar){
            resource = mCrossWinner;
        }
        else {
            resource = mCircleWinner;
        }

        if(mWinner.getWinningPosition() == WinningPosition.Column){
            //TODO remove hardcoded upper limit
            for (int i = 0; i < 3; i++) {
                mCells.get(mIds[i][mWinner.getStartingPosition()]).getCell().setBackgroundResource(resource);
            }
        }else if(mWinner.getWinningPosition() == WinningPosition.Row){
            for (int i = 0; i < 3; i++) {
                mCells.get(mIds[mWinner.getStartingPosition()][i]).getCell().setBackgroundResource(resource);
            }
        }
        else {
            if(mWinner.getStartingPosition() == 0){
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

    private Winner checkForWinner() {
        //Checking rows and columns for 3 identical chars.
        for (int i = 0; i < mBoard[0].length; i++) {
            //Rows
            if (mBoard[i][0] == mBoard[i][1] && mBoard[i][1] == mBoard[i][2] && mBoard[i][0] != mDefault) {
                return new Winner(mBoard[i][0], i, WinningPosition.Row);
            }
            //Columns
            if (mBoard[0][i] == mBoard[1][i] && mBoard[1][i] == mBoard[2][i] && mBoard[0][i] != mDefault) {
                return new Winner(mBoard[0][i], i, WinningPosition.Column);
            }
        }
        //Checking the two diagonals for 3 identical chars. Since the rows and columns are already checked
        //the diagonals are the only two options left for a win state.
        if(mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2] && mBoard[0][0] != mDefault){
            return new Winner(mBoard[0][0], 0, WinningPosition.Diagonal);
        }
        if(mBoard[2][0] == mBoard[1][1] && mBoard[1][1] == mBoard[0][2] && mBoard[0][2] != mDefault){
           return new Winner(mBoard[2][0], 2, WinningPosition.Diagonal);
        }
        return null;
    }

    //Gets the clicked cell position in the matrix. If the button has already been clicked this method returns false
    //(i.e invalid move). Otherwise it updates the cell and the matrices.
    private boolean processMove(int id) {
        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();
        if(mTaken[row][col]){
            return false;
        }
        updateCell(clickedCell.getCell(), row, col);
        return true;
    }

    //Place vector background on clicked button, mark the position as taken
    //and put the corresponding char in the mBoard matrix.
    private void updateCell(Button button, int row, int col) {
        if(mTurnsCount % 2 == 0){
            button.setBackgroundResource(mCross);
            mBoard[row][col] = mCrossChar;
        }
        else {
            button.setBackgroundResource(mCircle);
            mBoard[row][col] = mCircleChar;
        }
        mTaken[row][col] = true;
    }

    //Completely resets the board state, moves count, winner and status text
    private void resetGame(){
        for (int i = 0; i < mTaken[0].length; i++) {
            for (int j = 0; j < mTaken[1].length; j++) {
                mCells.get(mIds[i][j]).getCell().setBackgroundResource(R.color.colorBlack);
                mBoard[i][j] = mDefault;
                mTaken[i][j] = false;
            }
        }
        mTurnsCount = 0;
        mGameIsFinished = false;
        mWinner = null;
        mStatus.setText(R.string.starting_tip);
    }
}