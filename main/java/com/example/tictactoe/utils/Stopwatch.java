package com.example.tictactoe.utils;

public class Stopwatch {

    private long startTime = 0;
    private long stopTime = 0;
    private boolean isRunning = false;


    public void start() {
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
    }


    public void stop() {
        this.stopTime = System.currentTimeMillis();
        this.isRunning = false;
    }


    public long elapsed() {
        long elapsed;
        if (isRunning) {
            elapsed = (System.currentTimeMillis() - startTime);
        } else {
            elapsed = (stopTime - startTime);
        }
        return elapsed;
    }


    public long elapsedSeconds() {
        long elapsed;
        if (isRunning) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000);
        } else {
            elapsed = ((stopTime - startTime) / 1000);
        }
        return elapsed;
    }
}
