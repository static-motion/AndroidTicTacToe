package com.example.tictactoe.utils;

import com.example.tictactoe.R;
import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.events.MoveProcessedEvent;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.models.Figure;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Player;
import com.example.tictactoe.models.Winner;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class GameManager implements TicTacToeGameManager {

    private boolean[][] mTaken = new boolean[3][3];
    private char[][] mBoard = new char[3][3];
    private int[][] mIds = new int[3][3];
    private HashMap<Integer, GridCell> mCells;
    private int mTurnsCount = 0;
    private GameState mGameState = GameState.InProgress;
    private final char DEFAULT_CHAR = mBoard[0][0];
    private final int CIRCLE_DRAWABLE = R.drawable.circle;
    private final int CIRCLE_HIGHLIGHTED = R.drawable.circle_win;
    private final int CROSS_DRAWABLE = R.drawable.cross;
    private final int CROSS_HIGHLIGHTED = R.drawable.cross_win;
    private final char CROSS_CHAR = 'x';
    private final char CIRCLE_CHAR = 'o';
    private final Player CROSSES_PLAYER = new Player("Crosses", false);
    private final Player CIRCLES_PLAYER = new Player("Naughts", true);
    private final Figure CROSS_FIGURE = new Figure(CROSS_CHAR, CROSS_DRAWABLE, CROSS_HIGHLIGHTED);
    private final Figure CIRCLE_FIGURE = new Figure(CIRCLE_CHAR, CIRCLE_DRAWABLE, CIRCLE_HIGHLIGHTED);
    private int mRowId;
    private int mColId;

    public GameManager() {
        mCells = new HashMap<>();
        CROSSES_PLAYER.setFigure(CROSS_FIGURE);
        CIRCLES_PLAYER.setFigure(CIRCLE_FIGURE);
    }

    public void registerCell(int id, GridCell cell){
        mCells.put(id, cell);
        mIds[mRowId][mColId++] = id;
        if(mColId > 2){
            mRowId++;
            mColId = 0;
        }
    }

    @Override
    public CellUpdatedEvent processMove(int id) {
        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();

        if(mTaken[row][col]) return null;

        EventBus.getDefault().post(new MoveProcessedEvent(row, col));
        mTurnsCount++;
        updateCell(row, col);
        updateGameState();
        if (mTurnsCount % 2 != 0) {
            return new CellUpdatedEvent(id, CROSSES_PLAYER.getPlayerFigure().getFigureDrawable());
        } else {
            return new CellUpdatedEvent(id, CIRCLES_PLAYER.getPlayerFigure().getFigureDrawable());
        }
    }

    private void updateGameState() {
        if (mTurnsCount == 9) {
            mGameState = GameState.Finished;
        }
    }

    //Place vector background on clicked button, mark the position as taken
    //and put the corresponding char in the mBoard matrix.
    private void updateCell(int row, int col) {
        if (mTurnsCount % 2 != 0) {
            mBoard[row][col] = CROSSES_PLAYER.getPlayerFigure().getCharFigure();
        } else {
            mBoard[row][col] = CIRCLES_PLAYER.getPlayerFigure().getCharFigure();
        }
        mTaken[row][col] = true;

    }

    @Override
    public Winner checkForWinner() {
        //5 turns is the minimum turns count required for the game to be won.
        //No need to check the board before that.
        if(mTurnsCount < 5) {
            return null;
        }
        //Checking rows and columns for 3 identical chars.
        for (int i = 0; i < mBoard[0].length; i++) {
            //Rows
            if (mBoard[i][0] == mBoard[i][1] && mBoard[i][1] == mBoard[i][2] && mBoard[i][0] != DEFAULT_CHAR) {
                mGameState = GameState.Finished;
                if(mBoard[i][0] == CROSSES_PLAYER.getPlayerFigure().getCharFigure()){
                    return new Winner(CROSSES_PLAYER, new int[]{
                            i,0,i,1,i,2
                    });
                }
                else {
                    return new Winner(CIRCLES_PLAYER, new int[]{
                            i,0,i,1,i,2
                    });
                }
            }
            //Columns
            if (mBoard[0][i] == mBoard[1][i] && mBoard[1][i] == mBoard[2][i] && mBoard[0][i] != DEFAULT_CHAR) {
                mGameState = GameState.Finished;
                if(mBoard[0][i] == CROSSES_PLAYER.getPlayerFigure().getCharFigure()){
                    return new Winner(CROSSES_PLAYER, new int[]{
                            0,i,1,i,2,i
                    });
                }
                else {
                    return new Winner(CIRCLES_PLAYER, new int[]{
                            0,i,1,i,2,i
                    });
                }
            }
        }

        //Checking the two diagonals for 3 identical chars. Since the rows and columns are already checked
        //the diagonals are the only two options left for a win state.
        if(mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2] && mBoard[0][0] != DEFAULT_CHAR){
            mGameState = GameState.Finished;
            if(mBoard[0][0] == CROSSES_PLAYER.getPlayerFigure().getCharFigure()){
                return new Winner(CROSSES_PLAYER, new int[]{
                        0,0,1,1,2,2
                });
            }
            else {
                return new Winner(CIRCLES_PLAYER, new int[]{
                        0,0,1,1,2,2
                });
            }
        }

        if(mBoard[2][0] == mBoard[1][1] && mBoard[1][1] == mBoard[0][2] && mBoard[0][2] != DEFAULT_CHAR){
            mGameState = GameState.Finished;
            if(mBoard[2][0] == CROSSES_PLAYER.getPlayerFigure().getCharFigure()){
                return new Winner(CROSSES_PLAYER, new int[]{
                        2,0,1,1,0,2
                });
            }
            else {
                return new Winner(CIRCLES_PLAYER, new int[]{
                        2,0,1,1,0,2
                });
            }
        }
        return null;
    }

    @Override
    public CellUpdatedEvent processOpponentMove(int id) {
        return null;
    }

    public int getIdWithCoordinates(int row, int col){
        return mIds[row][col];
    }

    //Completely resets the board state, moves count, winner and status text
    public Queue<Integer> resetGame () {
        Queue<Integer> ids = new LinkedList<>();
        for (int row = 0; row < mTaken[0].length; row++) {
            for (int col = 0; col < mTaken[1].length; col++) {
                ids.add(mIds[row][col]);
                mBoard[row][col] = DEFAULT_CHAR;
                mTaken[row][col] = false;
            }
        }
        mTurnsCount = 0;
        mGameState = GameState.InProgress;
        return ids;
    }

    @Override
    public boolean isOpponentsTurn() {
        return false;
    }

    public GameState getGameState () {
        return this.mGameState;
    }
}