package com.example.tictactoe.utils;

import com.example.tictactoe.R;
import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.interfaces.TicTacToeGameManager;
import com.example.tictactoe.models.Board;
import com.example.tictactoe.models.Figure;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Player;
import com.example.tictactoe.models.Winner;

import java.util.HashMap;

public class GameManager implements TicTacToeGameManager {


    private final int CIRCLE_DRAWABLE = R.drawable.circle;
    private final int CIRCLE_HIGHLIGHTED = R.drawable.circle_win;
    private final int CROSS_DRAWABLE = R.drawable.cross;
    private final int CROSS_HIGHLIGHTED = R.drawable.cross_win;
    private final char CROSS_CHAR = 'x';
    private final char CIRCLE_CHAR = 'o';
    protected Player mPlayer;
    protected Player mOpponent;
    final String PLAYER_NAME;
    final Figure FIGURE_CROSS = new Figure(CROSS_CHAR, CROSS_DRAWABLE, CROSS_HIGHLIGHTED);
    final Figure FIGURE_CIRCLE = new Figure(CIRCLE_CHAR, CIRCLE_DRAWABLE, CIRCLE_HIGHLIGHTED);
    com.example.tictactoe.interfaces.Board mBoard = new Board();
    HashMap<Integer, GridCell> mCells;
    private int[][] mIds = new int[3][3];
    GameState mGameState = GameState.InProgress;
    int mTurnsCount = 0;
    boolean mIsOpponentsTurn = false;
    Player mCurrentPlayer;

    public GameManager(String playerName) {
        mCells = new HashMap<>();
        PLAYER_NAME = playerName;
    }

    @Override
    public void registerPlayers(String opponentName) {
        mPlayer = new Player(PLAYER_NAME, false);
        mPlayer.setFigure(FIGURE_CROSS);
        mOpponent = new Player(opponentName, true);
        mOpponent.setFigure(FIGURE_CIRCLE);
        mCurrentPlayer = mPlayer;
    }

    public void registerCell(int id, GridCell cell){
        mCells.put(id, cell);
        mIds[cell.getRow()][cell.getCol()] = id;
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
        return new CellUpdatedEvent(clickedCell, mPlayer.getPlayerFigure().getFigureDrawable());
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
        CellUpdatedEvent event = new CellUpdatedEvent(clickedCell, mOpponent.getPlayerFigure().getFigureDrawable());
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();
        updateCell(row, col);
        updateGameState();
        mIsOpponentsTurn = false;
        return event;
    }

    void updateCell(int row, int col) {
        mBoard.setMove(row, col, mCurrentPlayer.getPlayerFigure().getCharFigure());
    }

    @Override
    public Winner checkForWinner() {
        //5 turns is the minimum turns count required for the game to be won.
        //No need to check the board before that.
        if(mTurnsCount < 5) {
            return null;
        }
        //Checking rows and columns for 3 identical chars.
        int[] coordinates = null;
        char currentChar = mCurrentPlayer.getPlayerFigure().getCharFigure();
        for (int i = 0; i < 3; i++) {
            //Rows
            if (mBoard.figureAt(i,0) == mBoard.figureAt(i,1)
                    && mBoard.figureAt(i,1) == mBoard.figureAt(i,2)
                    && mBoard.figureAt(i,0) == currentChar) {

                coordinates = new int[] {i,0,i,1,i,2};
            }
            //Columns
            if (mBoard.figureAt(0,i) == mBoard.figureAt(1,i)
                    && mBoard.figureAt(1,i) == mBoard.figureAt(2,i)
                    && mBoard.figureAt(0, i) == currentChar) {

                coordinates = new int[]{0,i,1,i,2,i};
            }
        }

        //Checking the two diagonals for 3 identical chars. Since the rows and columns are already checked
        //the diagonals are the only two options left for a win state.
        if(mBoard.figureAt(0,0) == mBoard.figureAt(1,1)
                && mBoard.figureAt(1,1) == mBoard.figureAt(2,2)
                && mBoard.figureAt(0,0) == currentChar) {

            coordinates = new int[]{0, 0, 1, 1, 2, 2};
        }

        if( mBoard.figureAt(2,0) == mBoard.figureAt(1,1)
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

    //Completely resets the board state, moves count, winner and status text
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

    public com.example.tictactoe.interfaces.Board getBoard() {
        return mBoard;
    }
}