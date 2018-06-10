package com.example.tictactoe;

class DeviceFoundEvent {
    private String EndpointId;
    private String AuthenticationToken;
    private String EndpointName;

    DeviceFoundEvent(String endpointId, String authenticationToken, String endpointName) {
        EndpointId = endpointId;
        AuthenticationToken = authenticationToken;
        EndpointName = endpointName;
    }

    public String getEndpointId() {
        return EndpointId;
    }

    public String getAuthenticationToken() {
        return AuthenticationToken;
    }

    public String getEndpointName() {
        return EndpointName;
    }
}