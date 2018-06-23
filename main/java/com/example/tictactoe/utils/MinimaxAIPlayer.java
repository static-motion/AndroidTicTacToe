package com.example.tictactoe.utils;

import com.example.tictactoe.interfaces.AIPlayer;
import com.example.tictactoe.models.GridCell;
import com.example.tictactoe.models.Move;

import java.util.ArrayList;
import java.util.List;

public class MinimaxAIPlayer implements AIPlayer {

    private com.example.tictactoe.interfaces.Board mBoard;
    private char aiPlayer = 'o';
    private char hPlayer = 'x';

    @Override
    public GridCell makeMove(com.example.tictactoe.interfaces.Board board) {
        mBoard = board;
        Move move =  minimax(4, 'o', Integer.MIN_VALUE, Integer.MAX_VALUE);
        return new GridCell(move.getRow(), move.getCol());
    }

    private Move minimax(int depth, char player, int alpha, int beta) {
        List<GridCell> nextMoves = availableMoves();

        int score;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves.isEmpty() || depth == 0) {
            score = evaluate();
            return new Move(bestRow, bestCol, score);
        } else {
            for (GridCell cell : nextMoves) {
                mBoard.setMove(cell.getRow(), cell.getCol(), player);
                if (player == 'o') {
                    score = minimax(depth - 1, 'x', alpha, beta).getScore();
                    if (score > alpha) {
                        alpha = score;
                        bestRow = cell.getRow();
                        bestCol = cell.getCol();
                    }
                } else {
                    score = minimax(depth - 1, 'o', alpha, beta).getScore();
                    if (score < beta) {
                        beta = score;
                        bestRow = cell.getRow();
                        bestCol = cell.getCol();
                    }
                }
                // undo move
                mBoard.resetPosition(cell.getRow(), cell.getCol());
                // cut-off
                if (alpha >= beta) break;
            }
            return new Move(bestRow, bestCol, (player == 'o') ? alpha : beta);
        }
    }

    private ArrayList<GridCell> availableMoves(){
        ArrayList<GridCell> moves = new ArrayList<>();

        if(hasWon('x') || hasWon('o')){
            return moves;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if(!mBoard.isTaken(row, col)){
                    moves.add(new GridCell(row, col));
                }
            }
        }
        return moves;
    }

    private int evaluate() {
        int score = 0;
        // Evaluate score for each of the 8 lines (3 rows, 3 columns, 2 diagonals)
        score += evaluateLine(0, 0, 0, 1, 0, 2);  // row 0
        score += evaluateLine(1, 0, 1, 1, 1, 2);  // row 1
        score += evaluateLine(2, 0, 2, 1, 2, 2);  // row 2
        score += evaluateLine(0, 0, 1, 0, 2, 0);  // col 0
        score += evaluateLine(0, 1, 1, 1, 2, 1);  // col 1
        score += evaluateLine(0, 2, 1, 2, 2, 2);  // col 2
        score += evaluateLine(0, 0, 1, 1, 2, 2);  // diagonal
        score += evaluateLine(0, 2, 1, 1, 2, 0);  // alternate diagonal
        return score;
    }

    private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3) {
        int score = 0;

        // First cell
        if (mBoard.figureAt(row1, col1) == aiPlayer) {
            score = 1;
        } else if (mBoard.figureAt(row1, col1) == hPlayer) {
            score = -1;
        }

        // Second cell
        if (mBoard.figureAt(row2, col2) == aiPlayer) {
            if (score == 1) {
                score = 10;
            } else if (score == -1) {
                return 0;
            } else {
                score = 1;
            }
        } else if (mBoard.figureAt(row2, col2) == hPlayer) {
            if (score == -1) {
                score = -10;
            } else if (score == 1) {
                return 0;
            } else {
                score = -1;
            }
        }

        // Third cell
        if (mBoard.figureAt(row3, col3) == aiPlayer) {
            if (score > 0) {
                score *= 10;
            } else if (score < 0) {
                return 0;
            } else {
                score = 1;
            }
        } else if (mBoard.figureAt(row3, col3) == hPlayer) {
            if (score < 0) {
                score *= 10;
            } else if (score > 1) {
                return 0;
            } else {
                score = -1;
            }
        }
        return score;
    }

    private boolean hasWon(char player) {
        for (int i = 0; i < 3; i++) {
            //Rows
            if (mBoard.figureAt(i, 0) == mBoard.figureAt(i, 1)
                    && mBoard.figureAt(i, 1) == mBoard.figureAt(i, 2)
                    && mBoard.figureAt(i, 0) == player) {

                return true;
            }
            //Columns
            if (mBoard.figureAt(0, i) == mBoard.figureAt(1, i)
                    && mBoard.figureAt(1, i) == mBoard.figureAt(2, i)
                    && mBoard.figureAt(0, i) == player) {

                return true;
            }
        }

        return (mBoard.figureAt(0, 0) == mBoard.figureAt(1, 1)
                && mBoard.figureAt(1, 1) == mBoard.figureAt(2, 2)
                && mBoard.figureAt(0, 0) == player)
                || (mBoard.figureAt(2, 0) == mBoard.figureAt(1, 1)
                && mBoard.figureAt(1, 1) == mBoard.figureAt(0, 2)
                && mBoard.figureAt(0, 2) == player);

    }

    @Override
    public String getName() {
        return "AI IMPOSSIBLE";
    }
}
