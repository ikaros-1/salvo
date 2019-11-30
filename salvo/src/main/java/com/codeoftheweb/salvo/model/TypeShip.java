package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeShip{
    Carrier("Carrier",5,1),
    Battleship("Battleship",4,2),
    Destroyer("Destroyer",3,3),
    Submarine("Submarine",3,4),
    Patrol_boat("Patrol_boat",2,5);

    private String name;
    private int length;
    private int id;

    TypeShip(String name,int length) {
        this.name = name;
        this.length=length;
        this.id=id;
    }

    @Override
    public String toString(){
        return this.name;
    }

    public String getName() {
        return name;
    }

    public int getLength() { return length; }

    public int getId() {
        return id;
    }

    @JsonCreator
    static TypeShip findValue(@JsonProperty("length") int length, @JsonProperty("name") String name){
        return Arrays.stream(TypeShip.values()).filter(v -> v.length == length && v.name.equals(name)).findFirst().get();
    }


}