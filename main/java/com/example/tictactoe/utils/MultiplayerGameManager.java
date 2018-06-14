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

public class MultiplayerGameManager implements TicTacToeGameManager {

    private final String PLAYER_NAME;
    private final boolean IS_HOST;
    private boolean mIsOpponentsTurn = true;
    private boolean[][] mTaken = new boolean[3][3];
    private char[][] mBoard = new char[3][3];
    private int[][] mIds = new int[3][3];
    private HashMap<Integer,GridCell> mCells;
    private final int CIRCLE_DRAWABLE = R.drawable.circle;
    private final int CIRCLE_HIGHLIGHTED = R.drawable.circle_win;
    private final int CROSS_DRAWABLE = R.drawable.cross;
    private final int CROSS_HIGHLIGHTED = R.drawable.cross_win;
    private final char CROSS_CHAR = 'x';
    private final char CIRCLE_CHAR = 'o';
    private final Figure CROSS_FIGURE = new Figure(CROSS_CHAR, CROSS_DRAWABLE, CROSS_HIGHLIGHTED);
    private final Figure CIRCLE_FIGURE = new Figure(CIRCLE_CHAR, CIRCLE_DRAWABLE, CIRCLE_HIGHLIGHTED);
    private int mTurnsCount = 0;
    private GameState mGameState = GameState.InProgress;
    private final char DEFAULT_CHAR = mBoard[0][0];
    private Player mPlayer;
    private Player mOpponent;
    private int mRowId;
    private int mColId;
    private final String TAG = getClass().getSimpleName();

    public MultiplayerGameManager(boolean isHost, String playerName){
        mCells = new HashMap<>();
        IS_HOST = isHost;
        PLAYER_NAME = playerName;
    }

    public void registerPlayers(String opponentName) {
        mOpponent = new Player(opponentName, true);
        mPlayer = new Player(PLAYER_NAME, false);
        if(IS_HOST){
            mPlayer.setFigure(CROSS_FIGURE);
            mOpponent.setFigure(CIRCLE_FIGURE);
            mIsOpponentsTurn = false;
        }
        else {
            mPlayer.setFigure(CIRCLE_FIGURE);
            mOpponent.setFigure(CROSS_FIGURE);
        }
    }

    @Override
    public void registerCell(int id, GridCell cell){
        mCells.put(id, cell);
        mIds[mRowId][mColId++] = id;
        if(mColId > 2){
            mRowId++;
            mColId = 0;
        }
    }

    public CellUpdatedEvent processOpponentMove(int id){
        CellUpdatedEvent event = new CellUpdatedEvent(id, mOpponent.getPlayerFigure().getFigureDrawable());
        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();
        mIsOpponentsTurn = false;
        updateCell(row, col);
        mTurnsCount++;
        updateGameState();
        return event;
    }

    @Override
    public CellUpdatedEvent processMove(int id) {

        if(mIsOpponentsTurn) return null;

        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();

        if(mTaken[row][col]) return null;

        EventBus.getDefault().post(new MoveProcessedEvent(row, col));
        mIsOpponentsTurn = true;
        updateCell(row, col);
        mTurnsCount++;
        updateGameState();
        return new CellUpdatedEvent(id, mPlayer.getPlayerFigure().getFigureDrawable());
    }

    public int parseOpponentMove(String coordinates) {
        int row = Character.getNumericValue(coordinates.charAt(0));
        int col = Character.getNumericValue(coordinates.charAt(1));
        return mIds[row][col];
    }

    private void updateGameState() {
        if(mTurnsCount == 9){
            mGameState = GameState.Finished;
        }
    }

    //Mark the position as taken
    //and put the corresponding char in the mBoard matrix.
    private void updateCell(int row, int col) {
        if (mIsOpponentsTurn) {
            mBoard[row][col] = mPlayer.getPlayerFigure().getCharFigure();
        } else {
            mBoard[row][col] = mOpponent.getPlayerFigure().getCharFigure();
        }
        mTaken[row][col] = true;
    }

    public boolean isOpponentsTurn(){
        return mIsOpponentsTurn;
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
                if(mBoard[i][0] == mPlayer.getPlayerFigure().getCharFigure()){
                    return new Winner(mPlayer, new int[]{
                            i,0,i,1,i,2
                    });
                }
                else {
                    return new Winner(mOpponent, new int[]{
                            i,0,i,1,i,2
                    });
                }
            }
            //Columns
            if (mBoard[0][i] == mBoard[1][i] && mBoard[1][i] == mBoard[2][i] && mBoard[0][i] != DEFAULT_CHAR) {
                mGameState = GameState.Finished;
                if(mBoard[0][i] == mPlayer.getPlayerFigure().getCharFigure()){
                    return new Winner(mPlayer, new int[]{
                            0,i,1,i,2,i
                    });
                }
                else {
                    return new Winner(mOpponent, new int[]{
                            0,i,1,i,2,i
                    });
                }
            }
        }

        //Checking the two diagonals for 3 identical chars. Since the rows and columns are already checked
        //the diagonals are the only two options left for a win state.
        if(mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2] && mBoard[0][0] != DEFAULT_CHAR){
            mGameState = GameState.Finished;
            if(mBoard[0][0] == mPlayer.getPlayerFigure().getCharFigure()){
                return new Winner(mPlayer, new int[]{
                    0,0,1,1,2,2
                });
            }
            else {
                return new Winner(mOpponent, new int[]{
                        0,0,1,1,2,2
                });
            }
        }

        if(mBoard[2][0] == mBoard[1][1] && mBoard[1][1] == mBoard[0][2] && mBoard[0][2] != DEFAULT_CHAR){
            mGameState = GameState.Finished;
            if(mBoard[2][0] == mPlayer.getPlayerFigure().getCharFigure()){
                return new Winner(mPlayer, new int[]{
                        2,0,1,1,0,2
                });
            }
            else {
                return new Winner(mOpponent, new int[]{
                        2,0,1,1,0,2
                });
            }
        }
        return null;
    }

    public int getIdWithCoordinates(int row, int col){
        return mIds[row][col];
    }

    public GridCell getCellWithId(int id){
        return mCells.get(id);
    }

    @Override
    public Queue<Integer> resetGame(){
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
    public GameState getGameState(){
        return mGameState;
    }
}