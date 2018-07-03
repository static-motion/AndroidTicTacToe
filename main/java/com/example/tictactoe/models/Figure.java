package com.example.tictactoe.models;

import com.example.tictactoe.R;

public class Figure {

    private static final int CIRCLE_DRAWABLE = R.drawable.circle;
    private static final int CIRCLE_HIGHLIGHTED = R.drawable.circle_win;
    private static final int CROSS_DRAWABLE = R.drawable.cross;
    private static final int CROSS_HIGHLIGHTED = R.drawable.cross_win;
    private static final char CROSS_CHAR = 'x';
    private static final char CIRCLE_CHAR = 'o';

    public static final Figure FIGURE_CROSS = new Figure(CROSS_CHAR, CROSS_DRAWABLE, CROSS_HIGHLIGHTED);
    public static final Figure FIGURE_CIRCLE = new Figure(CIRCLE_CHAR, CIRCLE_DRAWABLE, CIRCLE_HIGHLIGHTED);

    private final char mFigure;
    private final int mFigureDrawable;
    private final int mHighlightedFigure;

    private Figure(char figure, int figureDrawable, int highlightedFigure){
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
