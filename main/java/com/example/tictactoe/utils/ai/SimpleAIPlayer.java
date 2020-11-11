package com.example.tictactoe.utils.ai;

import com.example.tictactoe.interfaces.AIPlayerContract;
import com.example.tictactoe.interfaces.BoardContract;
import com.example.tictactoe.models.GridCell;

import java.util.ArrayList;
import java.util.Random;

public class SimpleAIPlayer implements AIPlayerContract {

    private String mName = "AI EASY";

    @Override
    public GridCell makeMove(BoardContract board) {
        ArrayList<GridCell> availableMoves = getAvailableMoves(board);
        return availableMoves.get(new Random().nextInt(availableMoves.size()));
    }

    private ArrayList<GridCell> getAvailableMoves(BoardContract board) {
        ArrayList<GridCell> moves = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if(!board.isTaken(row, col)){
                    moves.add(new GridCell(row, col));
                }
            }
        }
        return moves;
    }

    @Override
    public String getName() {
        return mName;
    }
}
