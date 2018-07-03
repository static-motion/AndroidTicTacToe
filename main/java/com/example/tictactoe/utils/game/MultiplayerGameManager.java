package com.example.tictactoe.utils.game;

import com.example.tictactoe.enums.GameState;
import com.example.tictactoe.events.CellUpdatedEvent;
import com.example.tictactoe.events.MoveProcessedEvent;
import com.example.tictactoe.interfaces.Board;
import com.example.tictactoe.models.Figure;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Player;

import org.greenrobot.eventbus.EventBus;

public class MultiplayerGameManager extends GameManager {

    private final boolean IS_HOST;
    private final String TAG = getClass().getSimpleName();

    public MultiplayerGameManager(boolean isHost, Board board){
        super(board);
        IS_HOST = isHost;
    }

    @Override
    public void registerPlayers(String playerName, String opponentName) {
        mOpponent = new Player(opponentName, true);
        mPlayer = new Player(playerName, false);
        if(IS_HOST){
            mPlayer.setFigure(Figure.FIGURE_CROSS);
            mOpponent.setFigure(Figure.FIGURE_CIRCLE);
            mIsOpponentsTurn = false;
            mCurrentPlayer = mPlayer;
        }
        else {
            mPlayer.setFigure(Figure.FIGURE_CIRCLE);
            mOpponent.setFigure(Figure.FIGURE_CROSS);
            mCurrentPlayer = mOpponent;
        }
    }

    @Override
    public CellUpdatedEvent processMove(int id) {

        if(mIsOpponentsTurn) return null;

        GridCell clickedCell = mCells.get(id);
        int row = clickedCell.getRow();
        int col = clickedCell.getCol();

        if(mBoard.isTaken(row, col)) return null;
        EventBus.getDefault().post(new MoveProcessedEvent(clickedCell.getRow(), clickedCell.getCol()));
        updateCell(row, col);
        mIsOpponentsTurn = true;
        updateGameState();
        return new CellUpdatedEvent(clickedCell, mPlayer.getPlayerFigure().getFigureDrawable());
    }

    @Override
    void resetGameState() {
        mTurnsCount = 0;
        mGameState = GameState.InProgress;
    }
}