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
        int pos=0;
        boolean changenumber=false;
        if(ship.location.size()!=ship.typeShip.getLength())
            return false;
        if(ship.location.get(0).charAt(0)==ship.location.get(ship.location.size()-1).charAt(0)){
            changenumber=true;
        }
        else{
            changenumber=false;
        }
        for(String _location:ship.location){
            if(!_location.matches("^[A-J]+[1-9]$|^[A-J]+[1]+[0]$"))
                return false;
            if(changenumber){
                if(!(_location.charAt(0)==ship.location.get(pos).charAt(0)&& (Integer.parseInt(_location.substring(1))+pos)==Integer.parseInt(ship.location.get(pos).substring(1)))){
                    return false;
                }
            }
            else {
                if(!(_location.charAt(1)==ship.location.get(pos).charAt(1)&& _location.codePointAt(0)==ship.location.get(pos).codePointAt(0)+pos  ))
                    return false;
            }
            pos++;
        }
        return true;
    }

    public Map<String,Object> toMakeShipDTO(){
        Map dto= new HashMap<String,Object>();
        dto.put("type",this.getTypeShip());
        dto.put("location",this.getLocations().stream().collect(Collectors.toList()));
        return dto;
    }

}


