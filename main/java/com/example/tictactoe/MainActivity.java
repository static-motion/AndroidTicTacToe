package com.example.tictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean[][] mTaken = new boolean[3][3];
    private int[] mIds = new int[9];
    private HashMap<Integer,GridCell> mCells;
    private int mCircle = R.drawable.circle;
    private int mCross = R.drawable.cross;
    private int mTurnsCount = 0;
    private boolean mGameIsFinished = false;
    
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

        mIds[0] = R.id.btn_1;
        mIds[1] = R.id.btn_2;
        mIds[2] = R.id.btn_3;
        mIds[3] = R.id.btn_4;
        mIds[4] = R.id.btn_5;
        mIds[5] = R.id.btn_6;
        mIds[6] = R.id.btn_7;
        mIds[7] = R.id.btn_8;
        mIds[8] = R.id.btn_9;

    }

    @Override
    public void onClick(View v) {
        boolean moveIsValid = processMove(v.getId());
        if(mGameIsFinished){
            resetBoardState();
        }
        if(moveIsValid){
            mTurnsCount++;
        }
        if(mTurnsCount == 9){
            mGameIsFinished = true;
        }
    }

    private boolean processMove(int id) {
        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();
        if(mTaken[row][col]){
            return false;
        }
        mTaken[row][col] = true;
        updateCell(clickedCell.getCell());
        return true;
    }

    private void updateCell(Button button) {
        if(mTurnsCount % 2 == 0){
            button.setBackgroundResource(mCross);
        }
        else {
            button.setBackgroundResource(mCircle);
        }
    }
    private void resetBoardState(){
        int cellsIndex = 0;
        for (int i = 0; i < mTaken[0].length; i++) {
            for (int j = 0; j < mTaken[1].length; j++, cellsIndex++) {
                mTaken[i][j] = false;
                mCells.get(mIds[cellsIndex]).getCell().setBackgroundResource(R.color.colorWhite);
            }
        }
        mTurnsCount = 0;
        mGameIsFinished = false;
    }
}
