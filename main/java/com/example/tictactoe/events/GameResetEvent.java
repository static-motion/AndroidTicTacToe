package com.example.tictactoe.events;

import java.util.LinkedList;
import java.util.Queue;

public class GameResetEvent {
    private Queue<Integer> cellIds = new LinkedList<>();

    public GameResetEvent() {
    }

    public void addId(int id){
        cellIds.add(id);
    }

    public int getId(){
        return cellIds.remove();
    }
}
