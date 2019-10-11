package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;

    private String password;

    private boolean logging;

    @OneToMany(mappedBy = "player",fetch = FetchType.EAGER)
    private Set<Score> scores;

    @OneToMany(mappedBy = "player",fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;



    @JsonIgnore
    public List<Game> getGames(){
        return gamePlayers.stream().map(gamePlayer -> gamePlayer.getGame()).collect(toList());
    }

    public Player() {
    }

    public Player(String userName,String password){
        this.userName=userName;
        this.password=password;
    };

    public Player(String userName,String password,GamePlayer gamePlayer){
        this.gamePlayers=new HashSet<GamePlayer>();
        this.gamePlayers.add(gamePlayer);
        this.userName=userName;
        this.password=password;
    }


    public void addGamePlayer(GamePlayer gamePlayer){
        this.gamePlayers.add(gamePlayer);
    }

    public String getUserName() {
        return userName;
    }

    public String ToString(){
        return userName;
    }

    public long getId() {
        return id;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Score getScore(Game game) {
        for (Score score : getScores()) {
            if (score.getGame() == game)
                return score;
        }
        return null;
    }

    public static Map<String,Object> makePlayerDTO(Player player){
        Map dto = new HashMap<String, Object>();
        dto.put("email", player.getUserName());
        dto.put("id", player.getId());
        return dto;
    }

    public static Map<String,Object> makeScorePlayer(Player player){
        Map dto = new HashMap<String, Object>();
        float total=0;
        int win=0;
        int lost=0;
        int tied=0;
        for (Score score1 : player.getScores()) {
            switch (String.valueOf(score1.getScore())) {
                case "1.0":
                    win++;
                    break;
                case "0":
                    lost++;
                    break;
                case "0.5":
                    tied++;
                    break;
            }
            total+=score1.getScore();
        }
        dto.put("email",player.getUserName());
        dto.put("total",total);
        dto.put("win",win);
        dto.put("lost",lost);
        dto.put("tied",tied);
        return dto;
    }

}