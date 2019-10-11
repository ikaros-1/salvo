package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public Map<String,Object> makeScoreDTO(Score score){
        Map<String,Object> dto=new HashMap<>();
        dto.put("id",score.getId());
        dto.put("score",score.getScore());
        dto.put("finish",score.getFinishDate());
        return dto;
    }

}
