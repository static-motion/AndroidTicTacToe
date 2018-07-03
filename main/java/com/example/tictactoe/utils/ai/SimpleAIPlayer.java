package com.example.tictactoe.utils.ai;

import com.example.tictactoe.interfaces.AIPlayerContract;
import com.example.tictactoe.models.GridCell;

public class SimpleAIPlayer implements AIPlayerContract {

    private String mName = "AI EASY";
    private int[][] edges = {{0,0},{0,2},{2,0},{2,2}};
    private int[][] sides = {{0,1}, {1,0}, {1,2}, {2,1}};

    @Override
    public GridCell makeMove(com.example.tictactoe.interfaces.Board board) {
        if(!board.isTaken(1,1)){
            return new GridCell(1, 1);
        }
        for (int[] edge : edges) {
            if (!board.isTaken(edge[0], edge[1])) {
                return new GridCell(edge[0], edge[1]);
            }
        }
        for (int[] side : sides) {
            if (!board.isTaken(side[0], side[1])) {
                return new GridCell(side[0], side[1]);
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return mName;
    }
}
