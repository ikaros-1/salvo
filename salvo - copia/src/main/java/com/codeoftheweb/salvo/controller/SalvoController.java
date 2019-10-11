package com.codeoftheweb.salvo.controller;


import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/games")
    public List<Object> getGames(){
        List<Game> games = gameRepository.findAll();
        return games.stream().map(Game:: makeGamesDTO).collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{id}")
    @JsonPropertyOrder({"id","created","GamePlayers","Ships"})
    public Map<String,Object> getGame(@PathVariable("id") Long id) {
        GamePlayer gamePlayer=gamePlayerRepository.getOne(id);
        return GamePlayer.makeGameswithShip(gamePlayer);
    }

    @RequestMapping("/gameslist")
    public List<Object> getListGames(){
        List<Game> games = gameRepository.findAll();
        return games.stream().map(Game::makeGameScore).collect(Collectors.toList());
    }

    @RequestMapping("/leaderboard")
    public List<Object> getScore(){
        List<Player> players = playerRepository.findAll();
        return players.stream().map(Player::makeScorePlayer).collect(Collectors.toList());
    }
}
