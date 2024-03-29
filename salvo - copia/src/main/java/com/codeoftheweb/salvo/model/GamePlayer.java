package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private Set<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private Set<Salvo> salvoes;

    private LocalDateTime joinDate;

    public GamePlayer(){
        this.joinDate=LocalDateTime.now();
    }


    public GamePlayer(Game game , Player player){
        this.player=player;
        this.game=game;
        this.joinDate= LocalDateTime.now();
        this.ships=new HashSet<>();
        this.salvoes=new HashSet<>();
    }

    public void addShip(Ship ship){
        this.ships.add(ship);
    }

    public void addSalvo(Salvo salvo){
        this.salvoes.add(salvo);
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }


    public Player getPlayer() {
        return player;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public Score getScore(){
        for (Score score : getPlayer().getScores()) {
            if (getGame().getScores().contains(score)) {
                return score;
            }
        }
        return null;
    }

    public static Map<String,Object> makeGamesPlayerDTO(GamePlayer gamePlayer){
        Map dto = new HashMap<String, Object>();
        dto.put("player", Player.makePlayerDTO(gamePlayer.getPlayer()));
        dto.put("id", gamePlayer.getId());
        return dto;
    }
    public static Map<String,Object> makeGameswithShip(GamePlayer gamePlayer){
        Set<Ship> ships = new HashSet<Ship>();
        Map dto=Game.makeGamesDTO(gamePlayer.getGame());
        dto.put("ships", gamePlayer.getShips().stream().map(Ship::makeShipDTO).collect(Collectors.toList()));
        Set<Salvo> salvoes=new HashSet<>();
        for (GamePlayer gamePlayer1 : gamePlayer.getGame().getGamePlayers()) {
            salvoes.addAll(gamePlayer1.getSalvoes());
        }
        dto.put("salvo",salvoes.stream().map(Salvo::makeSalvoDTO).collect(Collectors.toList()));
        return dto;
    }

    public static Map<String,Object> makeGamesPlayerScore(GamePlayer gamePlayer){
        Map dto = new HashMap<String,Object>();
        dto.put("id",gamePlayer.getId());

        dto.put("score",gamePlayer.getScore().getScore());
        return dto;
    }


}
