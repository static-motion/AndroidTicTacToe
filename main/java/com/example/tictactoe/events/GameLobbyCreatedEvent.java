package com.example.tictactoe.events;

public class GameLobbyCreatedEvent {
    private String mMessage;

    public GameLobbyCreatedEvent(String message) {
        this.mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
