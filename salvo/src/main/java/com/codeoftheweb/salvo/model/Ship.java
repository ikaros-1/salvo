package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;


    private TypeShip typeShip;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="gameplayer_id")
    @JsonIgnore
    private GamePlayer gamePlayer;

    @ElementCollection
    private List<String> location;

    public Ship(){};

    @JsonCreator
    public Ship(@JsonProperty("name") String name,@JsonProperty("locations") String[] locations){
        this.gamePlayer=null;
        this.location=Arrays.asList(locations);
        this.typeShip=TypeShip.valueOf(name);
    }


    public Ship(TypeShip typeShip,GamePlayer gamePlayer,List<String> locations){
        this.typeShip=typeShip;
        this.gamePlayer=gamePlayer;
        this.location=locations;
    }

    public Ship(TypeShip typeShip,GamePlayer gamePlayer){new Ship(typeShip,gamePlayer,new ArrayList<String>()); }

    public void addLocation(String location){
        this.location.add(location);
    }

    public long getId() {
        return id;
    }

    public TypeShip getTypeShip() {
        return typeShip;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Collection<String> getLocations() {
        return location;
    }

    static public boolean validarShip(Ship ship){
        if(ship.location.size()!=ship.typeShip.getLength())
            return false;

    }

    public Map<String,Object> toMakeShipDTO(){
        Map dto= new HashMap<String,Object>();
        dto.put("type",this.getTypeShip());
        dto.put("location",this.getLocations().stream().collect(Collectors.toList()));
        return dto;
    }

}


