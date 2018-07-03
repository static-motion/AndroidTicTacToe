package com.example.tictactoe.interfaces;

public interface DifficultySettingContract {

    int winBias();

    int loseBias();

    int depth();

    String name();
}
