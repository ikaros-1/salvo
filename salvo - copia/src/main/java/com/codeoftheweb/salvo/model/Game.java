package com.codeoftheweb.salvo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    private Set<Score> scores;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @JsonIgnore
    public List<Player> getPlayers(){
        return gamePlayers.stream().map(gameplayer-> gameplayer.getPlayer()).collect(toList());
    }

    @JsonIgnore
    public List<Ship> getShip(){
        List<Ship> ships=new ArrayList<Ship>();
        this.getGamePlayers().stream().map(gamePlayer -> gamePlayer.getShips().stream().map(ship-> ships.add(ship)));
        return ships;
    }

    public Game(){this.creationDate= LocalDateTime.now();}

    public Game(GamePlayer gamePlayer){
        this.gamePlayers=new HashSet<GamePlayer>();
        this.gamePlayers.add(gamePlayer);
        this.creationDate= LocalDateTime.now();
    }
    public void addGamePlayers(GamePlayer gamePlayer){
        this.gamePlayers.add(gamePlayer);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public static Map<String,Object> makeGamesDTO(Game game){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map map=new HashMap<String,Object>();
        Set<GamePlayer> gamePlayers= game.getGamePlayers();
        map.put("gameplayers", gamePlayers.stream().map(GamePlayer::makeGamesPlayerDTO));
        map.put("created",game.getCreationDate().format(formatter));
        map.put("id",game.getId());
        return  map;
    }

    /*public static Map<String,Object> makeGameswithShip(Game game){
        Set<Ship> ships = new HashSet<Ship>();
        Map dto=makeGamesDTO(game);
        Set<GamePlayer> gamePlayers= game.getGamePlayers();
        gamePlayers.stream().forEach((gamePlayer)->{ships.addAll(gamePlayer.getShips());});
        dto.put("Ships", ships.stream().map(Ship::makeShipDTO).collect(Collectors.toList()));
        return dto;
    }*/
    public static Map<String,Object> makeGameScore(Game game){
        Map map=new HashMap<String,Object>();
        map.put("id",game.getId());
        map.put("gameplayers",game.getGamePlayers().stream().map(GamePlayer::makeGamesPlayerScore).collect(Collectors.toList()));
        return map;
    }

}
