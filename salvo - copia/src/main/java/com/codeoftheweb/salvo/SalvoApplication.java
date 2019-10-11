package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Range;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository,ScoreRepository scoreRepository){
		return (args) ->{

            playerRepository.save(new Player("j.bauer@ctu.gov"));
            playerRepository.save(new Player("c.obrian@ctu.gov"));
            Game game=new Game();
            gameRepository.save(game);
            gamePlayerRepository.save(new GamePlayer(gameRepository.getOne((long)1),playerRepository.getOne((long)1)));
            gamePlayerRepository.save(new GamePlayer(gameRepository.getOne((long)1),playerRepository.getOne((long)2)));
            game=new Game();
            gameRepository.save(game);
            gamePlayerRepository.save(new GamePlayer(gameRepository.getOne((long)2),playerRepository.getOne((long)1)));
            gamePlayerRepository.save(new GamePlayer(gameRepository.getOne((long)2),playerRepository.getOne((long)2)));
            game=new Game();
            gameRepository.save(game);

            List<String> localitation1 = Arrays.asList(new String[]{"H1", "H2", "H3"});

            shipRepository.save(new Ship(TypeShip.BattleShip,gamePlayerRepository.getOne((long)1),localitation1));
//
            localitation1 = Arrays.asList(new String[]{"A2", "B2", "C2"});

            shipRepository.save(new Ship(TypeShip.BattleShip,gamePlayerRepository.getOne((long)1),localitation1));

            localitation1 = Arrays.asList(new String[]{"A1", "A2", "A3"});

            shipRepository.save(new Ship(TypeShip.BattleShip,gamePlayerRepository.getOne((long)2),localitation1));

            localitation1=Arrays.asList(new String[]{"F5","D6","A9"});

            salvoRepository.save(new Salvo(gamePlayerRepository.getOne(1L),1,localitation1));

            localitation1=Arrays.asList(new String[]{"A1","B6","D3"});

            salvoRepository.save(new Salvo(gamePlayerRepository.getOne(2L),1,localitation1));

            localitation1=Arrays.asList(new String[]{"F1","C6","A8"});

            salvoRepository.save(new Salvo(gamePlayerRepository.getOne(2L),2,localitation1));

            scoreRepository.save(new Score(gameRepository.getOne(1L),playerRepository.getOne(1L),  1F));
            scoreRepository.save(new Score(gameRepository.getOne(1L),playerRepository.getOne(2L),0));
            scoreRepository.save(new Score(gameRepository.getOne(2L),playerRepository.getOne(1L),  0.5F));
            scoreRepository.save(new Score(gameRepository.getOne(2L),playerRepository.getOne(2L), 0.5F));
		};
	}

}
