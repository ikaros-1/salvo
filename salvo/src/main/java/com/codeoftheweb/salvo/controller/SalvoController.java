package com.codeoftheweb.salvo.controller;


import com.codeoftheweb.salvo.ActivePlayerStore;
import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Ship;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class                                  SalvoController {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @Autowired
    ShipRepository shipRepository;

    @Autowired
    ActivePlayerStore activePlayerStore;

    //-------------------------------------------------------------
    @Autowired
    private AuthenticationManager authManager;

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public ResponseEntity<Object> IsLogin(Authentication auth){
        Map<String,Object> dto=new HashMap<>();
        dto.put("username",auth.getName());
        if(auth!=null)
            return new ResponseEntity(dto,HttpStatus.ACCEPTED);
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
            return game.getScores().size()==0;
        })
                .map(Game::toMakeGamePlayer)
                .collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping(path="/games",method = RequestMethod.POST)
    public ResponseEntity<Object> register_Games(Authentication auth){
        if(auth==null)
            return new ResponseEntity<>("if not logged in",HttpStatus.UNAUTHORIZED);
        Game game=new Game();
        gameRepository.save(game);
        GamePlayer gamePlayer=new GamePlayer(game,playerRepository.findByUserName(auth.getName()));
        gamePlayerRepository.save(gamePlayer);
        Map<String,Object> dto=new HashMap<>();
        dto.put("gpid",gamePlayer.getId());
        return new ResponseEntity<>(dto,HttpStatus.CREATED);
    }


    @RequestMapping(path = "/games/{id}/player",method = RequestMethod.GET)
    public ResponseEntity<Object> get_Game_Players(@PathVariable("id")@NonNull Long id_Game,Authentication auth){
        Game game=gameRepository.getOne(id_Game);
        for(GamePlayer gamePlayer : game.getGamePlayers()) {
            if(gamePlayer.getPlayer().isUsername(auth)){
                Map<String,Object> dto =new HashMap<>();
                dto.put("gpid",gamePlayer.getId());
                return new ResponseEntity<>(dto,HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
    }


    @RequestMapping(path="/games/{id}/players",method = RequestMethod.POST)
    public ResponseEntity<Object> join_Games(Authentication auth,@PathVariable("id")@NonNull Long id_Game){
        if(auth==null)
            return new ResponseEntity<>("if not logged in",HttpStatus.UNAUTHORIZED);
        try{
        Game game=gameRepository.getOne(id_Game);

            if(game.getGamePlayers().size()>1 )
            return new ResponseEntity<>("if no such game, game full, already member",HttpStatus.FORBIDDEN);
        if(game.getPlayers().contains(playerRepository.findByUserName(auth.getName())))
            return new ResponseEntity<>("You has in the game",HttpStatus.FORBIDDEN);
        GamePlayer gamePlayer=new GamePlayer(gameRepository.getOne(id_Game),playerRepository.findByUserName(auth.getName()));
        gamePlayerRepository.save(gamePlayer);
        Map<String,Object> dto=new HashMap<>();
        dto.put("gpid",gamePlayer.getId());
        return new ResponseEntity<>(dto,HttpStatus.CREATED);
        }
        catch(EntityNotFoundException e){
            return new ResponseEntity<>("This game is not created",HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(path="/games/players/{gamePlayerId}/ships",method = RequestMethod.POST)
    public ResponseEntity<Object> addShip(Authentication auth,@PathVariable("gamePlayerId")@NonNull Long id_GamePlayer,@RequestBody Ship[] ships){
        try {
            if (auth == null)
                return new ResponseEntity<>("if not logged in", HttpStatus.UNAUTHORIZED);
            GamePlayer gamePlayer = gamePlayerRepository.getOne((Long) id_GamePlayer);
            if (!gamePlayer.getPlayer().isUsername(auth))
                return new ResponseEntity<>("Is not your gameplayer", HttpStatus.UNAUTHORIZED);
            if (gamePlayer.getShips().size() != 0)
                return new ResponseEntity<>("You landed your ships", HttpStatus.FORBIDDEN);
            if (ships.length != 5)
                return new ResponseEntity<>("You send bad ships", HttpStatus.NOT_ACCEPTABLE);
            else
            for(Ship ship :ships){
                ship.setGamePlayer(gamePlayer);
                shipRepository.save(ship);
            }
            return new ResponseEntity<>("", HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>("You send bad ships", HttpStatus.NOT_ACCEPTABLE);
        }
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
    public ResponseEntity<Object> register_Player(@RequestBody() Player player){
        try{
        if(player.getUserName().isEmpty() || player.getPassword().isEmpty() ){
            return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
        }
        if(playerRepository.existsByUserName(player.getUserName())== true || player.getUserName().contains("guest")){
            return new ResponseEntity<>("Username already in use",HttpStatus.FORBIDDEN);
        }
        playerRepository.save(player);
        return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>("Info players is bad",HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(path="/guest",method= RequestMethod.GET)
    public ResponseEntity<Object> register_Guest(HttpServletRequest req){
        try {
            String username = "Guest" + LocalDateTime.now().getNano();
            String password = String.valueOf(LocalDateTime.now().getNano());
            while (playerRepository.existsByUserName(username)) {
                username = "Guest" + LocalDateTime.now().getNano();
            }
            Player player = new Player(username, password);
            playerRepository.save(player);
            UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
            Authentication auth = authManager.authenticate(authReq);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            HttpSession session = req.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
            return new ResponseEntity<>("{id:"+player.getId()+"}",HttpStatus.ACCEPTED);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.toString(),HttpStatus.CONFLICT);
        }
    }
}
