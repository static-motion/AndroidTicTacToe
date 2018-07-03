package com.example.tictactoe.events;

public class OpponentMoveEvent {
    private byte[] coordinates;

    public OpponentMoveEvent(byte[] coordinates) {
        this.coordinates = coordinates;
    }

    public byte[] getCoordinates() {
        return coordinates;
    }
}
