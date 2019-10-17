package com.codeoftheweb.salvo.controller;


import com.codeoftheweb.salvo.ActivePlayerStore;
import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    ActivePlayerStore activePlayerStore;

    //-------------------------------------------------------------

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public ResponseEntity<Object> IsLogin(Authentication auth){
        if(auth!=null)
            return new ResponseEntity("Is loggin",HttpStatus.ACCEPTED);
        else
            return new ResponseEntity("Not loggin",HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication auth){
        /*Player player=playerRepository.findByUserName(auth.getName());*/
        Map<String,Object> dto= new HashMap<>();
        /*dto.put("player",Player.makePlayerDTO(player));
        dto.put("games",player.getGames().stream().map(Game::makeGameScore));
        dto.put("players_online",activePlayerStore.getPlayers());*/
        List<Game> games=gameRepository.findAll();
        dto.put("games",games.stream().filter((game)->{
            return game.getScores()==null;
        }).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping(path="/games",method = RequestMethod.POST)
    public ResponseEntity<Object> register_Games(Authentication auth){
        if(auth==null)
            return new ResponseEntity<>("if not logged in",HttpStatus.UNAUTHORIZED);
        Game game=new Game();
        gameRepository.save(game);
        gamePlayerRepository.save(new GamePlayer(game,playerRepository.findByUserName(auth.getName())));
        return new ResponseEntity<>("",HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/{id}/",method = RequestMethod.GET)
    public Map<String,Object> get_Game_Players(@PathVariable("id")@NonNull Long id_Game){
        Game game=gameRepository.getOne(id_Game);
        Map<String,Object> dto =new HashMap<>();
        dto.put("Players",game.getPlayers());
        return dto;
    }


    @RequestMapping(path="/games/{id}",method = RequestMethod.POST)
    public ResponseEntity<Object> join_Games(Authentication auth,@PathVariable("id")@NonNull Long id_Game){
        if(auth==null)
            return new ResponseEntity<>("if not logged in",HttpStatus.UNAUTHORIZED);
        Game game=gameRepository.getOne(id_Game);
        if(game.getGamePlayers().size()>1)
            return new ResponseEntity<>("if no such game, game full, already member",HttpStatus.FORBIDDEN);
        gamePlayerRepository.save(new GamePlayer(gameRepository.getOne(id_Game),playerRepository.findByUserName(auth.getName())));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //---------------------------------------------------------------

    @GetMapping(path= "/game_view/{id}")
    public Map<String,Object> getGame(@PathVariable("id") Long id) {
        GamePlayer gamePlayer=gamePlayerRepository.getOne(id);
        return gamePlayer.toMakeGameswithShip();
    }

    @RequestMapping("/gameslist")
    public List<Object> getListGames(){
        List<Game> games = gameRepository.findAll();
        return games.stream().map(Game::toMakeGameScore).collect(Collectors.toList());
    }

    @RequestMapping("/leaderboard")
    public List<Object> getScore(Authentication auth){
        List<Player> players = playerRepository.findAll();
        return players.stream().map(Player::toMakeScorePlayer).collect(Collectors.toList());
    }

    @RequestMapping(path="/players",method= RequestMethod.POST)
    public ResponseEntity<Object> register_Player(@RequestParam("username") String username,@RequestParam("password") String password){

        if(username.isEmpty() || password.isEmpty()){
            return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
        }
        if(playerRepository.findByUserName(username)!= null){
            return new ResponseEntity<>("Username already in use",HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(username,password));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
