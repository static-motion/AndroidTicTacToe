package com.example.tictactoe.events;

public class PlayerDisconnectedEvent {

    private String playerName;

    public PlayerDisconnectedEvent(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
