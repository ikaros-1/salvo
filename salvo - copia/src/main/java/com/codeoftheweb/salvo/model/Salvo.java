package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static Map<String,Object> makeSalvoDTO(Salvo salvo){
        Map dto= new HashMap<String,Object>();
        dto.put("turn",salvo.getTurn());
        dto.put("player",salvo.getGamePlayer().getPlayer());
        dto.put("locations",salvo.getLocations());
        return dto;
    }

}
