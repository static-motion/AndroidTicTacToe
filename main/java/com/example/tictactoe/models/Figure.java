package com.example.tictactoe.models;

public class Figure {
    private char mFigure;
    private int mFigureDrawable;
    private int mHighlightedFigure;

    public Figure(char figure, int figureDrawable, int highlightedFigure){
        mFigure = figure;
        mFigureDrawable = figureDrawable;
        mHighlightedFigure = highlightedFigure;
    }

    public char getCharFigure() {
        return mFigure;
    }

    public int getFigureDrawable() {
        return mFigureDrawable;
    }

    public int getHighlightedFigure() {
        return mHighlightedFigure;
    }
}
