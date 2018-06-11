package com.example.tictactoe.utils;

import android.widget.Button;

import com.example.tictactoe.R;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.models.Figure;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Player;
import com.example.tictactoe.models.Winner;

import java.util.HashMap;

public class GameManager implements TicTacToeGameManager {

    private boolean[][] mTaken = new boolean[3][3];
    private char[][] mBoard = new char[3][3];
    private int[][] mIds = new int[3][3];
    private HashMap<Integer, GridCell> mCells;
    private int mTurnsCount = 0;
    private GameState mGameState = GameState.InProgress;
    private char mDefault = mBoard[0][0];
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

    public void registerCell(int id, GridCell cell) {
        this.mCells.put(id, cell);
        registerId(id);
    }

    private void registerId(int id) {
        mIds[mRowId][mColId++] = id;
        if (mColId > 2) {
            mRowId++;
            mColId = 0;
        }
    }

    public GridCell processMove(int id) {

        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();

        if(mTurnsCount < 5 || !mTaken[row][col]){
            return null;
        }

        updateCell(clickedCell.getCell(), row, col);
        mTurnsCount++;
        updateGameState();
        return clickedCell;
    }

    private void updateGameState() {
        if (mTurnsCount == 9) {
            mGameState = GameState.Finished;
        }
    }

    //Place vector background on clicked button, mark the position as taken
    //and put the corresponding char in the mBoard matrix.
    private void updateCell(Button button, int row, int col) {
        if (mTurnsCount % 2 == 0) {
            button.setBackgroundResource(CROSSES_PLAYER.getPlayerFigure().getFigureDrawable());
            mBoard[row][col] = CROSSES_PLAYER.getPlayerFigure().getCharFigure();
        } else {
            button.setBackgroundResource(CIRCLES_PLAYER.getPlayerFigure().getFigureDrawable());
            mBoard[row][col] = CIRCLES_PLAYER.getPlayerFigure().getCharFigure();
        }
        mTaken[row][col] = true;
    }

    public Winner checkForWinner() {
        //5 turns is the minimum turns count required for the game to be won.
        //No need to check the board before that.
        if (mTurnsCount < 5) {
            return null;
        }
        //Checking rows and columns for 3 identical chars.
        for (int i = 0; i < mBoard[0].length; i++) {
            //Rows
            if (mBoard[i][0] == mBoard[i][1] && mBoard[i][1] == mBoard[i][2] && mBoard[i][0] != mDefault) {
                mGameState = GameState.Finished;
                if (mBoard[i][0] == CROSSES_PLAYER.getPlayerFigure().getCharFigure()) {
                    return new Winner(CROSSES_PLAYER, new int[]{
                            i, 0, i, 1, i, 2
                    });
                } else {
                    return new Winner(CIRCLES_PLAYER, new int[]{
                            i, 0, i, 1, i, 2
                    });
                }

            }
            //Columns
            if (mBoard[0][i] == mBoard[1][i] && mBoard[1][i] == mBoard[2][i] && mBoard[0][i] != mDefault) {
                mGameState = GameState.Finished;
                if (mBoard[0][i] == CROSSES_PLAYER.getPlayerFigure().getCharFigure()) {
                    return new Winner(CROSSES_PLAYER, new int[]{
                            0, i, 1, i, 2, i
                    });
                } else {
                    return new Winner(CIRCLES_PLAYER, new int[]{
                            0, i, 1, i, 2, i
                    });
                }
            }
        }
        //Checking the two diagonals for 3 identical chars. Since the rows and columns are already checked
        //the diagonals are the only two options left for a win state.
        if (mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2] && mBoard[0][0] != mDefault) {
            mGameState = GameState.Finished;
            if (mBoard[0][0] == CROSSES_PLAYER.getPlayerFigure().getCharFigure()) {
                return new Winner(CROSSES_PLAYER, new int[]{
                        0, 0, 1, 1, 2, 2
                });
            } else {
                return new Winner(CIRCLES_PLAYER, new int[]{
                        0, 0, 1, 1, 2, 2
                });
            }
        }
        if (mBoard[2][0] == mBoard[1][1] && mBoard[1][1] == mBoard[0][2] && mBoard[0][2] != mDefault) {
            mGameState = GameState.Finished;
            if (mBoard[2][0] == CROSSES_PLAYER.getPlayerFigure().getCharFigure()) {
                return new Winner(CROSSES_PLAYER, new int[]{
                        2, 0, 1, 1, 0, 2
                });
            } else {
                return new Winner(CIRCLES_PLAYER, new int[]{
                        2, 0, 1, 1, 0, 2
                });
            }
        }
        return null;
    }

    public void highlightWinningSequence(Winner winner) {
        Player player = winner.getPlayer();
        int[] coordinates = winner.getWinningStreakCoordinates();
        for (int i = 0; i < coordinates.length - 1; i += 2) {
            mCells.get(
                    mIds[coordinates[i]][coordinates[i + 1]])
                    .getCell()
                    .setBackgroundResource(player
                            .getPlayerFigure()
                            .getFigureDrawable());
        }
    }

    @Override
    public GridCell processOpponentMove(int id) {
        return null;
    }

    //Completely resets the board state, moves count, winner and status text
    public void resetGame () {
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

    @Override
    public boolean isOpponentsTurn() {
        return false;
    }

    public GameState getGameState () {
        return this.mGameState;
    }
}