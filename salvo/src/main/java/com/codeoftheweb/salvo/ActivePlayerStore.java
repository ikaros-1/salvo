package com.codeoftheweb.salvo;

import java.util.ArrayList;
import java.util.List;

public class ActivePlayerStore {

    private List<String> players;

    public ActivePlayerStore(){
        this.players= new ArrayList<String>();
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
