package com.example.tictactoe;

public class SearchingForDevicesEvent {
    private boolean mIsSearching;

    SearchingForDevicesEvent(boolean isSearching){
        this.mIsSearching = isSearching;
    }

    public boolean isSearching() {
        return mIsSearching;
    }
}
