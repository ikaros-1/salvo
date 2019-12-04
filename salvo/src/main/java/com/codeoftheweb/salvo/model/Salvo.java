package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="gameplayer_id")
    @JsonIgnore
    private GamePlayer gamePlayer;

    private int turn;

    @ElementCollection
    private List<String> locations;

    @JsonCreator
    public Salvo(@JsonProperty("location") String[] location){
        this.locations= Arrays.asList(location);
        this.turn=0;
        this.gamePlayer=null;
    }

    public Salvo(GamePlayer gamePlayer,int turn,List<String> locations){
        this.gamePlayer=gamePlayer;
        this.turn=turn;
        this.locations=locations;
    }

    public Salvo(){};
    //public Salvo(GamePlayer gamePlayer,int turn) {new Salvo(gamePlayer,turn,new ArrayList<String>());};

    public void addLocation(String string){
        locations.add(string);
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Map<String,Object> toMakeSalvoDTO(){
        Map dto= new HashMap<String,Object>();
        dto.put("turn",this.getTurn());
        dto.put("player",this.getGamePlayer().getId());
        dto.put("locations",this.getLocations());
        return dto;
    }


}
