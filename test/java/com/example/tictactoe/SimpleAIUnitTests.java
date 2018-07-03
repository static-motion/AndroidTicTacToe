package com.example.tictactoe;

import com.example.tictactoe.interfaces.AIPlayerContract;
import com.example.tictactoe.interfaces.Board;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.utils.MinimaxAIPlayer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleAIUnitTests {
    private AIPlayerContract player = new MinimaxAIPlayer();

    @Test
    public void playerAboutToFork_blocksFork() {
        Board board = new BoardForkBlock();
        GridCell cell = player.makeMove(board);
        assertEquals(true, isValid(cell.getRow(), new int[]{0, 2}));
        assertEquals(1, cell.getCol());

    }

    private boolean isValid(int num, int[] valid) {
        for (int currNum : valid) {
            if (num == currNum) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void playerAboutToWin_Blocks() {
        GridCell move = player.makeMove(new BoardPlayerAboutToWin());
        int expectedRow = 0;
        int expectedCol = 0;
        assertEquals(expectedRow, move.getRow());
        assertEquals(expectedCol, move.getCol());
    }
}
