package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="game_id")
    @JsonIgnore
    private Game game;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="player_id")
    @JsonIgnore
    private Player player;

    private float score;

    private LocalDateTime finishDate;

    public Score(){};

    public Score(Game game, Player player, float score) {
        this.game = game;
        this.player = player;
        this.score = score;
        this.finishDate = LocalDateTime.now();
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

    public float getScore() {
        return score;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public Map<String,Object> toMakeScoreDTO(){
        Map<String,Object> dto=new HashMap<>();
        dto.put("id",this.getId());
        dto.put("score",this.getScore());
        dto.put("finish",this.getFinishDate());
        return dto;
    }


}
