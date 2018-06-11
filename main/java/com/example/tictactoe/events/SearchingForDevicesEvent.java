package com.example.tictactoe.events;

public class SearchingForDevicesEvent {
    private boolean mIsSearching;

    public SearchingForDevicesEvent(boolean isSearching){
        this.mIsSearching = isSearching;
    }

    public boolean isSearching() {
        return mIsSearching;
    }
}
