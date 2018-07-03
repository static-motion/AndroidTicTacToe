package com.example.tictactoe.events;

public class DeviceConnectedEvent {

    private String endpointName;

    public DeviceConnectedEvent(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getEndpointName() {
        return endpointName;
    }
}
