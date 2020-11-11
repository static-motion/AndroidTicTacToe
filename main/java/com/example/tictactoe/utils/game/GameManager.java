package com.example.tictactoe.utils.game;

import android.util.SparseArray;

import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.interfaces.BoardContract;
import com.example.tictactoe.interfaces.GameManagerContract;
import com.example.tictactoe.models.Figure;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Player;
import com.example.tictactoe.models.Winner;

public class GameManager implements GameManagerContract {

    Player mPlayer;
    Player mOpponent;
    Player mCurrentPlayer;
    int mTurnsCount = 0;
    boolean mIsOpponentsTurn = true;
    BoardContract mBoard;
    SparseArray<GridCell> mCells;
    GameState mGameState = GameState.InProgress;

    public GameManager(BoardContract board) {
        mBoard = board;
        mCells = new SparseArray<>();
        resetBoardPositions();
    }

    @Override
    public void registerPlayers(String playerName, String opponentName) {
        mPlayer = new Player(playerName, false);
        mPlayer.setFigure(Figure.FIGURE_CROSS);
        mOpponent = new Player(opponentName, true);
        mOpponent.setFigure(Figure.FIGURE_CIRCLE);
        mCurrentPlayer = mPlayer;
        mIsOpponentsTurn = false;
    }

    public void registerCell(int id, GridCell cell){
        mCells.put(id, cell);
    }

    @Override
    public CellUpdatedEvent processMove(int id) {
        if(mIsOpponentsTurn) return null;
        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();

        if(mBoard.isTaken(row, col)) return null;

        updateCell(row, col);
        updateGameState();
        mIsOpponentsTurn = true;
        return new CellUpdatedEvent(clickedCell, mPlayer.getFigure().getFigureDrawable());
    }

    void updateGameState() {
        mTurnsCount++;
        if (mTurnsCount == 9) {
            mGameState = GameState.Finished;
        }
    }

    @Override
    public CellUpdatedEvent processOpponentMove(int id) {
        GridCell clickedCell = mCells.get(id);
        CellUpdatedEvent event = new CellUpdatedEvent(clickedCell, mOpponent.getFigure().getFigureDrawable());
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();
        updateCell(row, col);
        updateGameState();
        mIsOpponentsTurn = false;
        return event;
    }

    void updateCell(int row, int col) {
        mBoard.setMove(row, col, mCurrentPlayer.getFigure().getCharFigure());
    }

    @Override
    public Winner checkForWinner(int row, int col) {
        //5 turns is the minimum turns count required for the game to be won.
        //No need to check the board before that.
        if(mTurnsCount < 5) {
            return null;
        }
        //Checking rows and columns for 3 identical chars.
        int[] coordinates = null;
        char currentChar = mCurrentPlayer.getFigure().getCharFigure();
        if (mBoard.figureAt(row, 0) == mBoard.figureAt(row, 1)
                && mBoard.figureAt(row, 0) == mBoard.figureAt(row, 2)
                && mBoard.figureAt(row, 0) == currentChar){
            coordinates = new int[]{row, 0, row, 1, row, 2};
        }
        else if (mBoard.figureAt(0, col) == mBoard.figureAt(1, col)
                && mBoard.figureAt(0, col) == mBoard.figureAt(2, col)
                && mBoard.figureAt(0, col) == currentChar){
            coordinates = new int[]{0, col, 1, col, 2, col};
        }
        //Checking the two diagonals for 3 identical chars. Since the rows and columns are already checked
        //the diagonals are the only two options left for a win state.
        else if(mBoard.figureAt(0,0) == mBoard.figureAt(1,1)
                && mBoard.figureAt(1,1) == mBoard.figureAt(2,2)
                && mBoard.figureAt(0,0) == currentChar) {

            coordinates = new int[]{0, 0, 1, 1, 2, 2};
        }
        else if(mBoard.figureAt(2,0) == mBoard.figureAt(1,1)
                && mBoard.figureAt(1,1) == mBoard.figureAt(0,2)
                && mBoard.figureAt(0,2) == currentChar){

            coordinates = new int[]{2,0,1,1,0,2};
        }

        if(coordinates != null){
            mGameState = GameState.Finished;
            return new Winner(mCurrentPlayer, coordinates);
        }
        return null;
    }

    public void resetGame () {
        resetBoardPositions();
        resetGameState();
    }

    void resetGameState() {
        mIsOpponentsTurn = false;
        mCurrentPlayer = mPlayer;
        mTurnsCount = 0;
        mGameState = GameState.InProgress;
    }

    private void resetBoardPositions() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                mBoard.resetPosition(row, col);
            }
        }
    }

    @Override
    public void switchCurrentPlayer() {
        if(mCurrentPlayer == mPlayer){
            mCurrentPlayer = mOpponent;
        }
        else {
            mCurrentPlayer = mPlayer;
        }
    }

    public boolean isFinished() {
        return mGameState == GameState.Finished;
    }

}