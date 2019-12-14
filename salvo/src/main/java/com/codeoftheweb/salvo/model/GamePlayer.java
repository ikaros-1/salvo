package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        return this.id;
    }

    public Game getGame() {
        return this.game;
    }


    public Player getPlayer() {
        return this.player;
    }

    public LocalDateTime getJoinDate() {
        return this.joinDate;
    }

    public Set<Ship> getShips() {
        return this.ships;
    }

    public Set<Salvo> getSalvoes() {
        return this.salvoes;
    }

    public Score getScore(){
        for (Score score : getPlayer().getScores()) {
            if (getGame().getScores().contains(score)) {
                return score;
            }
        }
        return null;
    }

    public Map<String,Object> toMakeGamesPlayerDTO(){
        Map dto = new HashMap<String, Object>();
        dto.put("player", this.getPlayer().toMakePlayerDTO());
        dto.put("id", this.getId());
        return dto;
    }
    public Map<String,Object> toMakeGameswithShip(){
        Set<Ship> ships = new HashSet<Ship>();
        Map dto=this.getGame().toMakeGamesDTO();
        dto.put("ships", this.getShips().stream().map(Ship::toMakeShipDTO).collect(Collectors.toList()));
        Set<Salvo> salvoes=new HashSet<>();
        Game game=this.getGame();
        for (GamePlayer gamePlayer1 : game.getGamePlayers()) {
            salvoes.addAll(gamePlayer1.getSalvoes());
        }

        dto.put("salvo",salvoes.stream().map(Salvo::toMakeSalvoDTO).collect(Collectors.toList()));
        return dto;
    }

    public  Map<String,Object> toMakeGamesPlayerScore(){
        Map dto = new HashMap<String,Object>();
        dto.put("id",this.getId());

        dto.put("score",this.getScore().getScore());
        return dto;
    }

    public  Map<String,Object> toMakeGamesPlayerGame(){
        Map<String,Object> dto=new HashMap<>();
        dto.put("gpid",this.getId());
        dto.put("id",this.getPlayer().getId());
        dto.put("username",this.getPlayer().getUserName());
        return dto;
    }

    public int getTurn(){
        int turn=0;
        for(Salvo salvo:this.salvoes){
            if(turn<salvo.getTurn())
                turn=salvo.getTurn();
        }
        return turn+1;
    }

    public boolean isTurn(GamePlayer op){
        if(this.getTurn()==op.getTurn())
            return true;
        else
            return false;
    }
//Verifica que se pongan 1 de cada tipo de barco
    static public boolean validarTiposBarcos(Ship[] ships){
        int sum=0;
        int mul=1;
        for(Ship _ship:ships){
            sum+=_ship.getTypeShip().getId();
            mul*=_ship.getTypeShip().getId();
        }
        if(sum!=15)
            return false;
        if(mul!=120)
            return false;
        return true;
    }
//Verifico los salvos
    public boolean validarSalvoes(Salvo salvo) {
        List<String> locations=new ArrayList<String>();
        this.getSalvoes().stream().forEach(_salvo->locations.addAll(_salvo.getLocations()));
        if (salvo.getLocations().size() != 5)
            return false;
        for (String location : salvo.getLocations()){
            if (!(location.matches("^[A-J]+[1-9]$|^[A-J]+[1]+[0]$")))
                return false;
            if(locations.contains(locations))
                return false;
        }
        return true;
    }
}
